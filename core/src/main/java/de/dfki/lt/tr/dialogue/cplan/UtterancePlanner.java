package de.dfki.lt.tr.dialogue.cplan;

import static de.dfki.lt.tr.dialogue.cplan.Constants.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.util.IniFileReader;
import de.dfki.lt.tr.dialogue.cplan.util.Pair;
import de.dfki.lt.tr.dialogue.cplan.util.PairList;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public class UtterancePlanner {

  protected class RuleSection {
    public RuleSection(String sectionName, String sectionEncoding, List<File> sectionFiles) {
      name = sectionName;
      encoding = sectionEncoding;
      files = sectionFiles;
    }

    public String encoding, name;
    public List<File> files;
  }

  protected class ProjectConfiguration {
    public ProjectConfiguration(File rootFile) { _projectRootFile = rootFile; }

    /** The current project file */
    public File _projectRootFile;

    /** some settings, like location of the history file and the CCG grammar */
    public HashMap<String, Object> settings = new HashMap<>();

    /** All the files from the rule reading, in sections. A section is a pair
     *  of section name / rule list
     */
    public List<RuleSection> ruleSections = new ArrayList<>();

    /** The list of directories containing plugins */
    public List<File> pluginDirectories = new ArrayList<>();
  }



  /** This logger should be used only for messages concerning rule loading and
   *  processing issues. It is meant to be the console/error output of this
   *  content planner.
   */
  protected Logger logger = LoggerFactory.getLogger("UtterancePlanner");

  /** The processing engine of this content planner */
  private ArrayList<Processor> _processors;

  /** The lexer to use in rule and logical form parsing */
  private Lexer _ruleLexer;

  /** All the errors from the last round of rule reading */
  private List<Position> _errors;

  /** The current project configuration */
  ProjectConfiguration _configuration;

  /** A flag that indicates that processing should be stopped immediately.
   *
   *  Useful in the GUI to stop infinite loops, or if processing times out.
   */
  private boolean _interrupted;

  /** The tracer that is currently used to trace rules */
  private RuleTracer _currentTracer;

  Environment env; // package visible for tests

  protected void init() {
    FunctionFactory.init(this);
    _errors = new ArrayList<Position>();

    env = new Environment();
    _ruleLexer = new Lexer();
    _ruleLexer.setErrorLogger(logger);
  }

  public UtterancePlanner() {
    init();
  }

  /** package visibility to allow unit tests to access this */
  void addProcessor(List<Rule> basicRules) {
    if (_processors == null) {
      _processors = new ArrayList<Processor>();
    }
    _processors.add(new ParallelProcessor(basicRules));
  }

  public void setTracing(RuleTracer rt) {
    _currentTracer = rt;
    for (Processor processor : _processors) {
      processor.setTracing(_currentTracer);
    }
  }

  public RuleTracer getTracing() {
    return _currentTracer;
  }

  /** Return the current project file */
  public File getProjectFile() {
    return _configuration._projectRootFile;
  }

  /** Return the directory that contains the current project file */
  public File getProjectDir() {
    return getProjectFile().getParentFile();
  }

  public static File resolvePath(File baseFile, String dependentFileName) {
    File dependentFile = new File(dependentFileName);
    if (! dependentFile.isAbsolute()) {
      dependentFile = new File(baseFile.getParentFile(), dependentFileName);
    }
    return dependentFile;
  }

  public boolean hasSetting(String key) {
    return _configuration == null ? false : _configuration.settings.containsKey(key);
  }

  public Object getSetting(String key) {
    return _configuration == null ? null : _configuration.settings.get(key);
  }

  public File getHistoryFile() {
    if (hasSetting(KEY_HISTORY_FILE)) {
      return resolvePath(getProjectFile(), (String)getSetting(KEY_HISTORY_FILE));
    }
    return null;
  }

  /** read all rules from reader \c r and add them to the \c rules list.
   *
   *  If errors occur, they are collected in the _errors field of this
   *  planner.
   *  @param r                 a reader where the rule input comes from
   *  @param inputDescription  a description of the input source, e.g., a file
   *                           name
   *  @return the list of all successfully parsed rules
   */
  private List<Rule> readRules(Reader r, String inputDescription)
  throws IOException {
    RuleParser ruleParser = new RuleParser(_ruleLexer, env);
    ruleParser.setErrorVerbose(true);
    ruleParser.setDebugLevel(0);
    ruleParser.reset(inputDescription, r);
    ruleParser.parse();
    List<Rule> fileRules = ruleParser.getRules();
    _errors.addAll(_ruleLexer.getAllErrorPositions());
    return fileRules;
  }


  /** Read all rule files given in \c ruleFiles */
  private List<Rule> readRules(File ruleFile, String encoding) {
    List<Rule> basicRules = new LinkedList<Rule>();
    try {
      Reader r = new InputStreamReader(new FileInputStream(ruleFile), encoding);
      basicRules.addAll(readRules(r, ruleFile.getPath()));
      logger.info("Reading rule file: {}", ruleFile);
    }
    catch (FileNotFoundException fnfex) {
      logger.warn("Could not find rule file: {}", ruleFile);
    }
    catch (IOException ioex) {
      logger.warn("Could not read rule file: {} ({})", ruleFile, ioex);
    }
    return basicRules;
  }


  protected String readLocalSettings(File projectFile,
      PairList<String, PairList<String, String>> project,
      String encoding) {
    //ProjectFileHandler handler = new ProjectFileHandler(projectFile);
    PairList<String, String> localSettings = project.find(SECTION_SETTINGS);

    if (localSettings != null) {
      // when processed like this, there's no need to propagate this
      String pluginDirectory = localSettings.find(KEY_PLUGIN_DIR);
      if (pluginDirectory != null) {
        // TODO: ACCOMODATE FOR A LIST OF PLUGIN DIRECTORIES
        File pluginPath = resolvePath(projectFile, pluginDirectory);
        _configuration.pluginDirectories.add(pluginPath);
      }
      // only needed locally to read rules (no propagation to global settings)
      String localEncoding = localSettings.find(KEY_ENCODING);
      if (localEncoding != null) {
        encoding = localEncoding;
      }
      String history = localSettings.find(KEY_HISTORY_FILE);
      if (history != null) {
        _configuration.settings.put(KEY_HISTORY_FILE, history);
      }
    }
    return encoding;
  }

  protected void readRuleSection(File projectFile,
      PairList<String, PairList<String, String>> project,
      String encoding) throws FileNotFoundException, IOException {
    for (Pair<String, PairList<String, String>> section : project) {
      String sectionName = section.getFirst();
      if (sectionName.startsWith(SECTION_RULES)) {
        List<File> ruleFiles = new ArrayList<File>(section.getSecond().size());
        _configuration.ruleSections.add(new RuleSection(sectionName, encoding, ruleFiles));
        for (Pair<String, String> fileName : section.getSecond()) {
          File ruleFile = resolvePath(projectFile, fileName.getFirst());
          ruleFiles.add(ruleFile);
        }
      } else if (sectionName.equals(SECTION_INCLUDE)) {
        for (Pair<String, String> fileName : section.getSecond()) {
          readOneProjectFile(resolvePath(projectFile, fileName.getFirst()), encoding);
        }
      }
    }
  }

  protected void readOneProjectFile(File projectFile, String encoding)
      throws FileNotFoundException, IOException {
    PairList<String, PairList<String, String>> project =
        IniFileReader.readIniFile(projectFile);
    String localEncoding = readLocalSettings(projectFile, project, encoding);
    readRuleSection(projectFile, project, localEncoding);
  }

  protected void initHierachy() {
    if (! env.isInitialized())
      env.init(new FlatHierarchy());
  }

  protected void loadPlugins() {
    for (File pluginPath : _configuration.pluginDirectories) {
      FunctionFactory.registerPlugins(pluginPath, this);
    }
  }

  protected void loadRules() {
    for (RuleSection section : _configuration.ruleSections) {
      for (File ruleFile : section.files) {
        List<Rule> sectionRules = readRules(ruleFile, section.encoding);
        addProcessor(sectionRules);
      }
    }
  }

  /** Load all things contained in the configuration in the right way */
  protected void load() {
    initHierachy();
    /** First load the plugins, then the rules */
    loadPlugins();
    loadRules();
  }

  /** This uses the data in the project file to initialize the processor
   *  The first call to this function gets non-null settings and null rules,
   *  while for all included project files, the rules should get collected
   *  in the provided rules list.
   *  If settings propagate to the outside must be decided in every subclass
   *  and for every setting.
   * @throws IOException
   * @throws FileNotFoundException
   *
   *
  protected void finishProject(
      File projectFile,
      PairList<String, PairList<String, String>> project,
      HashMap<String, String> settings,
      PairList<String, List<File>> ruleSections)
          throws FileNotFoundException, IOException {
    //ProjectFileHandler handler = new ProjectFileHandler(projectFile);
    PairList<String, String> localSettings = project.find(SECTION_SETTINGS);

    String encoding = "UTF-8";
    if (localSettings != null) {
      // when processed like this, there's no need to propagate this
      String pluginDirectory = localSettings.find(KEY_PLUGIN_DIR);
      if (pluginDirectory != null) {
        // TODO: ACCOMODATE FOR A LIST OF PLUGIN DIRECTORIES
        File pluginPath = resolvePath(projectFile, pluginDirectory);
        FunctionFactory.registerPlugins(pluginPath, this);
      }
      // only needed locally to read rules (no propagation to global settings)
      String localEncoding = localSettings.find(KEY_ENCODING);
      if (localEncoding != null) {
        encoding = localEncoding;
      }
      String history = localSettings.find(KEY_HISTORY_FILE);
      if (history != null) {
        settings.put(KEY_HISTORY_FILE, history);
      }
    }
    for (Pair<String, PairList<String, String>> section : project) {
      String sectionName = section.getFirst();
      if (sectionName.startsWith(SECTION_RULES)) {
        List<File> ruleFiles = new ArrayList<File>(section.getSecond().size());
        ruleSections.add(sectionName, ruleFiles);
        for (Pair<String, String> fileName : section.getSecond()) {
          File ruleFile = resolvePath(projectFile, fileName.getFirst());
          ruleFiles.add(ruleFile);
          List<Rule> sectionRules = readRules(ruleFile, encoding);
          addProcessor(sectionRules);
        }
      } else if (sectionName.equals(SECTION_INCLUDE)) {
        for (Pair<String, String> fileName : section.getSecond()) {
          readProjectFileInner(resolvePath(projectFile, fileName.getFirst()),
              settings, ruleSections);
        }
      }
    }
  }

  protected void readProjectFileInner(File projectFile,
      HashMap<String, String> settings,
      PairList<String, List<File>> ruleSections)
          throws FileNotFoundException, IOException {
    PairList<String, PairList<String, String>> project =
        IniFileReader.readIniFile(projectFile);
    finishProject(projectFile, project, settings, ruleSections);
  }*/

  /** Read a content planner project file.
   *
   *  The project file contains grammar specific settings, such as were to
   *  find the corresponding CCG grammar, or plug-ins, and the (possibly staged)
   *  rules files.
   *
   *  @param projectFile a File object that points to a content planner project
   *         file, containing all information on how to load the grammar for
   *         the content planner. This may also contain a setting that loads
   *         additional plug-ins.
   *  @throws FileNotFoundException if the file can not be found.
   *          IOException if reading the file fails
   */
  public void readProjectFile(File rootFile)
      throws FileNotFoundException, IOException {
    // reset global status
    _configuration = new ProjectConfiguration(rootFile.getAbsoluteFile());
    _processors = null;
    _errors.clear();
    readOneProjectFile(rootFile, "UTF-8");
    load();
  }

  public List<Position> getErrors() {
    return _errors;
  }

  public List<File> getAllRuleFiles() {
    List<File> result = new ArrayList<File>();
    for (RuleSection section : _configuration.ruleSections) {
      result.addAll(section.files);
    }
    return result;
  }

  /** This function is only present to support testing, therefore it has
   *  package visibility. It should not be used from the outside.
   */
  List<Rule> readRulesFromString(String ruleString) {
    List<Rule> rules = new ArrayList<Rule>();
    try {
      rules.addAll(readRules(new StringReader(ruleString), "input"));
    } catch (IOException e) {
      // this will never be thrown
      e.printStackTrace();
    }
    return rules;
  }

  public int MAX_ITERATIONS = 100;

  /** Run the processor until there is no more change */
  private DagNode computeFixpoint(DagNode input) {
    Bindings bindings = new Bindings();
    _interrupted = false;
    DagNode result = input.cloneFS();
    DagNode.invalidate();
    for (Processor processor : _processors) {
      processor.init();
      boolean changed = true;
      int iterations = 0;
      while (changed && ! _interrupted &&
          (MAX_ITERATIONS < 0 || ++iterations < MAX_ITERATIONS)) {
        result = processor.applyRules(result, bindings);
        changed = (! result.equals(input) || bindings.globalBindingsChanged());
        input = result.copyIntermediate(new IdentityHashMap<Object, Object>());
      }
      if (iterations >= MAX_ITERATIONS) {
        logger.warn("MAX ITERATIONS EXCEEDED: POSSIBLY INFINITE RECURSION");
      }
    }
    _interrupted = false;
    return input;
  }

  /** Process the input with the loaded rules */
  public DagNode process(DagNode lf) {
    return computeFixpoint(lf);
  }

  synchronized public void interruptProcessing() {
    _interrupted = true;
  }
}
