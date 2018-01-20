package de.dfki.lt.tr.dialogue.cplan;

import java.io.*;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.*;

import de.dfki.lt.j2emacs.J2Emacs;
import de.dfki.lt.loot.gui.Style;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.BatchType;
import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.gui.LFMapFacetLayout;
import de.dfki.lt.tr.dialogue.cplan.gui.LFModelAdapter;
import de.dfki.lt.tr.dialogue.cplan.gui.UPMainFrame;
import de.dfki.lt.tr.dialogue.cplan.util.Position;
import jline.console.ConsoleReader;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class InteractivePlanner extends CcgUtterancePlanner
implements UPMainFrame.CloseAllListener {

  /** The root directory that contains resources in its etc/ subdirectory */
  private File _rootDir;

  /** The user's home directory */
  private File _homeDir;

  /** The name of this application */
  public static String appName = "cplanner";

  /** The buffer name for the file output in case of a connection to Emacs */
  private String _compilationBufferName = "*" + appName + "*";
  /** An Emacs connector */
  private J2Emacs _j2e = null;

  public short[] excludedFeatures;

  public InteractivePlanner() {
    excludedFeatures = new short[3];
    excludedFeatures[0] = env.ID_FEAT_ID;
    excludedFeatures[1] = env.TYPE_FEAT_ID;
    excludedFeatures[2] = env.PROP_FEAT_ID;
    LFModelAdapter.init(excludedFeatures);
    LFMapFacetLayout.init(excludedFeatures);
  }

  public File getResourcesDir() {
    return new File(_rootDir, "etc/");
  }

  public File getPreferencesFile() {
    File prefs = new File(_homeDir, "." + appName);
    return prefs;
  }

  public boolean setRootDir(File file) {
    _homeDir = new File(System.getProperty("user.home"));
    _rootDir = file;
    if (! _rootDir.isDirectory() || ! getResourcesDir().isDirectory()) {
      _rootDir = null;
      return true;
    }
    //  TODO: ?? this is depending on the project ??
    File pluginDirectory = new File(getResourcesDir(), "plugins");
    if (pluginDirectory.isDirectory()) {
      FunctionFactory.registerPlugins(pluginDirectory, this);
    }
    return false;
  }

  public boolean isEmacsAlive() {
    return _j2e != null && _j2e.alive();
  }

  /** Read the project file for this content planner. In addition to the super
   *  class' method, start the Emacs connection.
   *
   *  @see CcgUtterancePlanner
   */
  @Override
  public void readProjectFile(File projectFile) {
    if (isEmacsAlive()) {
      _j2e.clearBuffer(_compilationBufferName);
      _j2e.startBuffering(_compilationBufferName);
    }
    try {
      super.readProjectFile(projectFile);
    }
    catch (IOException ioex) {
      logger.warn("{}", ioex);
    }
    if (isEmacsAlive()) {
      _j2e.markAsProjectFiles(getProjectDir(), getAllRuleFiles());
      _j2e.flushBuffer(_compilationBufferName);
    }
  }

  private ConsoleReader cinput = null;
  private String getInputFromTerminal() {
    String prompt = ">> ";
    String queryString = "";
    String str;
    try {
      if (cinput == null) {
        cinput = new ConsoleReader();
        cinput.setBellEnabled(false);
      }
      while ((str = //input.readLine()
        cinput.readLine(prompt)
      ) != null && (str.length() > 0)) {
        prompt = "/ ";
        queryString += str + "\n";
      }
    } catch (IOException i) {
      return "";
    }
    return queryString;
  }

  public void interactive(int traceFlags) {
    if (traceFlags != 0) {
      setTracing(new LoggingTracer(traceFlags));
    }
    String input = getInputFromTerminal();
    while(! input.isEmpty()) {
      DagNode lf = env.parseLfString(input);
      if (lf != null) {
        System.out.println(lf);
        DagNode result = process(lf);
        System.out.println(result);
      }
      input = getInputFromTerminal();
    }
  }

  private class LoadProjectAction implements J2Emacs.Action {
    private UPMainFrame _mf;

    LoadProjectAction(UPMainFrame mf) { _mf = mf; }

    public void execute(String... args) { _mf.reloadCurrentProject(); }
  }

  void startGui(final String projectFile) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        UPMainFrame mf = new UPMainFrame("ContentPlanner (no rules loaded)",
            InteractivePlanner.this, projectFile);
        mf.registerCloseAllListener(InteractivePlanner.this);
        if (_j2e != null) {
          _j2e.registerAction("reload", new LoadProjectAction(mf));
          _j2e.registerAction("file_changed", new LoadProjectAction(mf));
        }
      }
    });
  }

  public void allClosed() {
    closeEmacs(true);
  }

  public void startEmacsConnection(String emacsPath) {
    _j2e = new J2Emacs("CPlanner", getResourcesDir(), emacsPath);
    _j2e.addStartHook("(load \""
        + new File(getResourcesDir(), "cplan").getAbsolutePath()
        + "\")");
    _j2e.startEmacs();
    // make an EmacsBufferAppender in j2e-compilation mode
    Appender ea = _j2e.new EmacsBufferAppender(_compilationBufferName, true);
    Logger uplogger = Logger.getLogger("UtterancePlanner");
    uplogger.removeAllAppenders();
    uplogger.setAdditivity(false);
    uplogger.addAppender(ea);
  }

  private void closeEmacs(boolean quitEmacs) {
    if (_j2e == null) return;
    if (quitEmacs) {
      _j2e.exitEmacs();
    }
    _j2e.close();
    _j2e = null;
  }

  public void showPosition(Position p) {
    if (_j2e == null) return;
    try {
      File f = new File(p.msg).getCanonicalFile();
      _j2e.visitFilePosition(f, p.line, p.column, "");
    }
    catch (IOException ioex) {
      Logger.getRootLogger().warn(ioex);
    }
  }

  public DagNode parseLfString(String currentText) {
    return env.parseLfString(currentText);
  }

  public Position getLastLFErrorPosition() {
    return env.getLastLFErrorPosition();
  }

  /*
  public void batchGenerateRandom(String optionArg, File projectFile,
    File initialSentences)
  throws IOException {
    final int MAX_EQUAL_REPEAT = 25;

    int repeat = MAX_EQUAL_REPEAT;
    String fileName = optionArg;
    Pattern split = Pattern.compile("^(([0-9]+):)?(.*)$");
    Matcher m = split.matcher(optionArg);
    if (m.matches()) {
      fileName = m.group(3);
      String repeatNo = m.group(2);
      if (repeatNo != null && ! repeatNo.isEmpty()) {
        try {
          repeat = Integer.parseInt(repeatNo);
        }
        catch (NumberFormatException nfex) {
          logger.error("Not a number for batch: " +repeatNo);
          repeat = MAX_EQUAL_REPEAT;
        }
      }
    }
    File batchFile = new File(fileName);
    if (! batchFile.exists()) {
      usage("Batch input file not found:" + batchFile);
      return;
    }

    Pattern punctRegex = Pattern.compile("\\s*(?:[;:,.?]\\s*)*([;:,.?])");
    Pattern spaceRegex = Pattern.compile("\\s+");

    this.readProjectFile(projectFile);

    /*
    HashSet<String> sents = new HashSet<String>();
    if (initialSentences != null) {
      BufferedReader initSents = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(initialSentences), "utf-8"));
      while (initSents.ready()) {
        String sent = initSents.readLine();
        sents.add(sent);
      }
      initSents.close();
    }
    /

    BatchTest bt = this.loadBatch(batchFile, BatchType.GENERATION);
    for (int i = 0; i < bt.itemSize(); ++i) {
      RealizationTestItem item = (RealizationTestItem) bt.getItem(i);
      HashSet<String> sents = new HashSet<String>();
      System.out.println("### " + item.lf);
      for (int e = 0; e < repeat; ++e) {
        ResultItem res = bt.realizeOneItem(item, i);
        //BatchTest bt = this.batchProcess(batchFile);
        //this.logger.info(bt.percentageGood());
        //for (int i = 0; i < bt.goodSize(); ++i) {
        //BatchTest.ResultItem good = bt.getGood(i);
        if (res.itemStatus == BatchTest.Status.GOOD) {
          String result = punctRegex.matcher(res.realized).replaceAll("$1");
          result = spaceRegex.matcher(result).replaceAll(" ");
          if (! sents.contains(result)) {
            sents.add(result);
            e = -1; // new sentence: reset the repeat counter
            System.out.println(result);
          }
        }
      }
      System.err.println(sents.size());
    }
  }*/


  /** Run one of the batch processing steps
   * @throws IOException
   * @throws FileNotFoundException */
  public void runSomeBatch(String project, String batchName, BatchType type,
      String outfile) throws FileNotFoundException, IOException {
    if (null == project) {
      usage("No project file specified");
    }

    File batchFile = new File(batchName);
    if (! batchFile.exists()) {
      usage("Batch input file not found:" + batchFile);
    } else {
      readProjectFile(new File(project));
      Writer out = new PrintWriter(System.out);
      try {
        if (outfile != null) {
          out = new FileWriter(outfile);
        }
        if (type == BatchType.PLANNING_ALL
            || type == BatchType.GENERATION_ALL) {
          Logger.getRootLogger().setLevel(Level.ERROR);
          batchGenerateAllPossibilities(batchFile, out,
              type == BatchType.GENERATION_ALL);
        } else {
          BatchTest bt = batchProcess(batchFile, type);
          bt.save(out);
        }
      } catch (IOException ex) {
        logger.error("Problem during batch processing: " +  ex);
      } finally {
        try {
          if (outfile != null)
            out.close();
        } catch (Exception ex) { throw new RuntimeException(ex); }
      }
      allClosed();
    }
  }

  private static void usage(String msg) {
    String[] usage = {
        "Usage: UPDebugger [-[g]<enerate batch> batchfile]",
        "                  [-[G]<enerate all sentences> batchfile] ",
        "                  [-[a]<nalyze batch> inputfile]",
        "                  [-[p]<lan batch> inputfile]",
        "                  [-[P]<lan all batch> inputfile]",
        "                  [-c<ompileonly>] [-d<ebugdags>]",
        "                  [-i<nteractive shell>] [-t<race>={1,2,3}] [-e<macs>]",
        "                  <projectfile> [batchoutput]",
        "      -t : bit 1: trace match, bit 2 : trace modification",
        "  The GUI will is started by default"
    };
    System.out.println(msg);
    for (String us : usage) System.out.println(us);
    System.exit(1);
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws FileNotFoundException, IOException {
    Logger uplogger = Logger.getLogger("UtterancePlanner");
    Enumeration<Appender> apps = uplogger.getAllAppenders();
    Enumeration<Appender> rapps = Logger.getRootLogger().getAllAppenders();
    if (! apps.hasMoreElements() && ! rapps.hasMoreElements()) {
      uplogger.addAppender( new ConsoleAppender(new PatternLayout("%m%n")));
      uplogger.setAdditivity(false);
      Logger.getRootLogger().addAppender(
          new ConsoleAppender(new SimpleLayout(), "System.err"));
      // Logger.getRootLogger().setLevel(Level.ERROR);
    }

    OptionParser parser = new OptionParser("a:g:p:G:P:icdt:e::");
    OptionSet options = null;
    try {
      options = parser.parse(args);
    }
    catch (OptionException ex) {
      usage("Error parsing options: " + ex.getLocalizedMessage());
      System.exit(1);
    }

    if (System.getProperty("app.dir") == null) {
      usage("application directory must be specified in app.dir property");
      System.exit(1);
    }

    List<String> nonOptionArgs = (List<String>) options.nonOptionArguments();

    String optionArg = null;

    BatchType type = BatchType.NONE;
    char what = '_';
    // x and T are only for test purposes
    String actionOptions = "agpGPi";
    for (int i = 0; i < actionOptions.length(); ++i) {
      char c = actionOptions.charAt(i);
      String action = "" + c;
      if (options.has(action)) {
        if (what != '_') {
          StringBuilder sb = new StringBuilder();
          sb.append("Only one of");
          for (i = 0; i < actionOptions.length(); ++i)
            sb.append(" -" + actionOptions.charAt(i));
          sb.append(" allowed.");
          usage(sb.toString());
        }
        what = c;
        optionArg = (String) options.valueOf(action);
        if (i < BatchType.values().length)
          type = BatchType.values()[i];
      }
    }

    InteractivePlanner ip = new InteractivePlanner();
    if (ip.setRootDir(new File(System.getProperty("app.dir")))) {
      usage("property value of app.dir is not the root directory:"
          + System.getProperty("app.dir"));
    }

    if (options.has("e")) {
      ip.startEmacsConnection((String)options.valueOf("e"));
    }

    if (options.has("d")) {
      ip.env.useDebugPrinter();
    } else {
      ip.env.usePrettyPrinter();
    }

    String project = nonOptionArgs.size() > 0 ? nonOptionArgs.get(0) : null;
    String outfile = nonOptionArgs.size() > 1 ? nonOptionArgs.get(1) : null;

    if (type != BatchType.NONE) {
      ip.runSomeBatch(project, optionArg, type, outfile);
    } else {
      switch (what) {
      case 'c': // only "compile" resp. check rules
        ip.readProjectFile(new File(project));
        break;
      case 'i': {
        // trace flags
        int traceFlags = 0;
        if (options.has("t")) {
          traceFlags = Integer.parseInt((String) options.valueOf("t"));
        }

        if (null == project) {
          usage("No project file specified");
        } else {
          ip.readProjectFile(new File(project));
          ip.interactive(traceFlags);
          ip.allClosed();
        }
        break;
      }
      default:
        ip.startGui(project);
        break;
      }
    }
  }
}
