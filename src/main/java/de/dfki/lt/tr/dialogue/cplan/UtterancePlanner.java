package de.dfki.lt.tr.dialogue.cplan;

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
import java.util.List;

import org.apache.log4j.Logger;

import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.util.IniFileHandler;
import de.dfki.lt.tr.dialogue.cplan.util.IniFileReader;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public class UtterancePlanner {

  /** This logger should be used only for messages concerning rule loading and
   *  processing issues. It is meant to be the console/error output of this
   *  content planner.
   */
  protected Logger logger = Logger.getLogger("UtterancePlanner");

  /** The processing engine of this content planner */
  private ArrayList<Processor> _processors;
  /** The lexer to use in logical form parsing */
  private Lexer _lfLexer;
  /** The parser for logical forms. */
  private LFParser _lfParser;

  /** The lexer to use in rule and logical form parsing */
  private Lexer _ruleLexer;

  /** All the errors from the last round of rule reading */
  private List<Position> _errors;

  /** All the files from the rule reading, in sections. A section is a pair
   *  of section name / rule list
   */
  private List<Object[]> _ruleSections;

  /** The current project file */
  protected File _projectFile;

  /** some settings, like location of the history file and the CCG grammar */
  protected HashMap<String, String> _settings;

  /** A flag that indicates that processing should be stopped immediately.
   *
   *  Useful in the GUI to stop infinite loops, or if processing times out.
   */
  private boolean _interrupted;

  /** The tracer that is currently used to trace rules */
  private RuleTracer _currentTracer;

  static {
    DagNode.init();
  }

  protected void init() {
    FunctionFactory.init(this);
    _errors = new ArrayList<Position>();
    _ruleSections = new ArrayList<Object[]>();

    _ruleLexer = new Lexer();
    _ruleLexer.setErrorLogger(logger);

    _lfLexer = new Lexer();
    _lfParser = new LFParser(_lfLexer);
    _lfParser.errorVerbose = true;
    _settings = new HashMap<String, String>();
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

  /** Return the directory that contains the current project file */
  public File getProjectDir() {
    return _projectFile.getParentFile();
  }

  public File resolveProjectFile(String projectFileName) {
    File projectFile = new File(projectFileName);
    if (! projectFile.isAbsolute()) {
      projectFile = new File(getProjectDir(), projectFileName);
    }
    return projectFile;
  }

  public boolean hasSetting(String key) {
    return _settings.containsKey(key);
  }

  public String getSetting(String key) {
    return _settings.get(key);
  }

  public File getHistoryFile() {
    if (hasSetting("history_file")) {
      return resolveProjectFile(getSetting("history_file"));
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
    RuleParser ruleParser = new RuleParser(_ruleLexer);
    ruleParser.errorVerbose = true;
    ruleParser.setDebugLevel(0);
    ruleParser.reset(inputDescription, r);
    ruleParser.parse();
    List<Rule> fileRules = ruleParser.getRules();
    _errors.addAll(_ruleLexer.getAllErrorPositions());
    return fileRules;
  }


  /** Read all rule files given in \c ruleFiles */
  private List<Rule> readAllRules(List<File> ruleFiles) {
    List<Rule> basicRules = new ArrayList<Rule>();
    for (File f : ruleFiles) {
      try {
        String grammarEncoding = _settings.get("encoding");
        if (grammarEncoding == null) {
          grammarEncoding = "UTF-8";
        }
        Reader r =
            new InputStreamReader(new FileInputStream(f), grammarEncoding);
        basicRules.addAll(readRules(r, f.getPath()));
        logger.info("Reading rule file: " + f);
      }
      catch (FileNotFoundException fnfex) {
        logger.warn("Could not find rule file: " + f);
      }
      catch (IOException ioex) {
        logger.warn("Could not read rule file: " + f + " (" + ioex +")");
      }
    }
    return basicRules;
  }

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
  @SuppressWarnings("unchecked")
  public void readProjectFile(File projectFile)
  throws FileNotFoundException, IOException {
    // reset global status
    _ruleSections.clear();
    _settings.clear();
    _processors = null;
    _errors.clear();

    _projectFile = projectFile.getAbsoluteFile();

    IniFileReader.readFile(_projectFile, new IniFileHandler() {
      private List<File> ruleFiles = null;
      boolean readSettings = false;

      @Override
      public void sectionStart(String sectionName) {
        if (sectionName.startsWith("Rules")) {
          sectionName = sectionName.substring(5).trim();
          ruleFiles = new ArrayList<File>();
          Object[] newSection = { sectionName, ruleFiles };
          _ruleSections.add(newSection);
        } else if (sectionName.startsWith("Settings")) {
          readSettings = true;
          ruleFiles = null;
        }
      }

      @Override
      public void sectionEnd(String sectionName) {
        if (sectionName.startsWith("Rules")) {
          if (ruleFiles != null) {
            // addProcessor(readAllRules(ruleFiles));
            ruleFiles = null;
          }
        } else if (sectionName.startsWith("Settings")) {
          readSettings = false;
        }
      }

      @Override
      public void keyValuePair(String key, String value) {
        if (ruleFiles != null) {
          File nextFile = resolveProjectFile(key);
          ruleFiles.add(nextFile);
        } else if (readSettings) {
          _settings.put(key, value);
        }
      }
    });

    if (_settings.containsKey("plugin_directory")) {
      File pluginPath = resolveProjectFile(_settings.get("plugin_directory"));
      FunctionFactory.registerPlugins(pluginPath, UtterancePlanner.this);
    }

    for(Object[] ruleSection : _ruleSections) {
      addProcessor(readAllRules((List<File>) ruleSection[1]));
    }
  }

  public List<Position> getErrors() {
    return _errors;
  }

  @SuppressWarnings("unchecked")
  public List<File> getAllRuleFiles() {
    List<File> result = new ArrayList<File>();
    for (Object[] section : _ruleSections) {
      result.addAll((List<File>) section[1]);
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

  /** Convert the given input string, which contains a (partial) logical form,
   *  into an internal data structure for processing.
   */
  public DagNode parseLfString(String input) {
    if (input.isEmpty())
      return null;
    StringReader sr = new StringReader(input);
    _lfParser.reset("Console", sr);
    try {
      if (_lfParser.parse()) {
        return _lfParser.getResultLF();
      }
    }
    catch (IOException ioex) {
      // this will hardly ever been thrown
      ioex.printStackTrace();
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      // may occur during parsing of LF in LFParser. Just die silently.
    }
    return null;
  }

  /** Get the error position of the last LF parse, or null, if there is no
   *  such error.
   */
  public Position getLastLFErrorPosition() {
    return _lfLexer.getLastErrorPosition();
  }

}
