package de.dfki.lt.tr.dialogue.cplan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jline.ConsoleReader;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import de.dfki.lt.j2emacs.J2Emacs;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.BatchType;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.RealizationTestItem;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.ResultItem;
import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.gui.LFModelAdapter;
import de.dfki.lt.tr.dialogue.cplan.gui.UPMainFrame;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

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

  public InteractivePlanner() {
    LFModelAdapter.init();
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
      logger.warn(ioex);
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
      DagNode lf = parseLfString(input);
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

    @Override public void execute(String... args) { _mf.reloadCurrentProject(); }
  }

  void startGui(String projectFile) {
    UPMainFrame mf =
      new UPMainFrame("ContentPlanner (no rules loaded)", this, projectFile);
    mf.registerCloseAllListener(this);
    if (_j2e != null) {
      _j2e.registerAction("reload", new LoadProjectAction(mf));
      _j2e.registerAction("file_changed", new LoadProjectAction(mf));
    }
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

  public void batchGenerate(String optionArg, File projectFile,
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
    */

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
  }


  private static void usage(String msg) {
    String[] usage = {
        "Usage: UPDebugger [-[r]<ealize batch> inputfile]",
        "                  [-[R]<realize sentences> [iterationcount:]inputfile [initialsentences]] ",
        "                  [-[p]<arse batch> inputfile]",
        "                  [-c<ompileonly>] [-d<ebugdags>]",
        "                  [-g<ui>] [-t<race>={1,2,3}] [-e<macs>]",
        "                  <projectfile>",
        "      -t : bit 1: trace match, bit 2 : trace modification"
    };
    System.out.println(msg);
    for (String us : usage) System.out.println(us);
    System.exit(1);
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    Logger uplogger = Logger.getLogger("UtterancePlanner");
    Enumeration<Appender> apps = uplogger.getAllAppenders();
    Enumeration<Appender> rapps = Logger.getRootLogger().getAllAppenders();
    if (! apps.hasMoreElements() && ! rapps.hasMoreElements()) {
      uplogger.addAppender( new ConsoleAppender(new PatternLayout("%m%n")));
      uplogger.setAdditivity(false);
      Logger.getRootLogger().addAppender(
          new ConsoleAppender(new SimpleLayout(), "System.err"));
    }

    OptionParser parser = new OptionParser("R:r:p:P:cdgt:e::A:");
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

    List<String> nonOptionArgs = options.nonOptionArguments();

    String optionArg = null;

    char what = 'i';
    // x and T are only for test purposes
    String[] actionOptions = { "R", "r", "p", "P", "c", "g" };
    for (String action : actionOptions) {
      if (options.has(action)) {
        if (what != 'i')
          usage("Only one of -R, -r, -p, -P, -c or -g allowed.") ;
        what = action.charAt(0);
        optionArg = (String) options.valueOf(action);
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
      DagNode.useDebugPrinter();
    } else {
      DagNode.usePrettyPrinter();
    }
    switch (what) {
    case 'r': // Run a batch planning + generation test
      try {
        if (nonOptionArgs.size() == 0) {
          usage("No project file specified");
        } else {
          File batchFile = new File(optionArg);
          if (! batchFile.exists()) {
            usage("Batch input file not found:" + batchFile);
          } else {
            ip.readProjectFile(new File(nonOptionArgs.get(0)));
            BatchTest bt = ip.batchProcess(batchFile, BatchType.PARSING);
            try {
              File batchSave =
                  new File(batchFile.getParent(), batchFile.getName() + ".out");
              bt.save(new FileWriter(batchSave));
            } catch (IOException ioex) {
              bt.save(new PrintWriter(System.out));
            }
            ip.logger.info(bt.percentageGood());
            ip.allClosed();
          }
        }
      }
      catch (IOException ex) {
        ip.logger.error("Problem during batch processing: " +  ex);
      }
      break;
    case 'R': // batch generate sentences from the given parameters
      try {
        if (nonOptionArgs.size() == 0) {
          usage("No project file specified");
        } else {
          File initialSentences = null;
          if (nonOptionArgs.size() > 1) {
            initialSentences = new File(nonOptionArgs.get(1));
          }
          ip.batchGenerate(optionArg, new File(nonOptionArgs.get(0)),
              initialSentences);
          ip.allClosed();
        }
      }
      catch (IOException ex) {
        ip.logger.error("Problem during batch processing: " +  ex);
      }
      break;
    case 'p':
      try {
        if (nonOptionArgs.size() == 0) {
          usage("No project file specified");
        } else {
          File batchFile = new File(optionArg);
          if (! batchFile.exists()) {
            usage("Batch input file not found:" + batchFile);
          } else {
            ip.readProjectFile(new File(nonOptionArgs.get(0)));
            BatchTest bt = ip.batchProcess(batchFile, BatchType.GENERATION);
            try {
              File batchSave =
                  new File(batchFile.getParent(), batchFile.getName() + ".out");
              bt.save(new FileWriter(batchSave));
            } catch (IOException ioex) {
              bt.save(new PrintWriter(System.out));
            }
            ip.logger.info(bt.percentageGood());
            ip.allClosed();
          }
        }
      }
      catch (IOException ex) {
        ip.logger.error("Problem during batch processing: " +  ex);
      }
      break;
    case 'P':
      // do a "planning" batch test
      try {
        if (nonOptionArgs.size() == 0) {
          usage("No project file specified");
        } else {
          File batchFile = new File(optionArg);
          if (! batchFile.exists()) {
            usage("Batch input file not found:" + batchFile);
          } else {
            ip.readProjectFile(new File(nonOptionArgs.get(0)));
            BatchTest bt = ip.batchProcess(batchFile, BatchType.PLANNING);
            try {
              File batchSave =
                  new File(batchFile.getParent(), batchFile.getName() + ".out");
              bt.save(new FileWriter(batchSave));
            } catch (IOException ioex) {
              bt.save(new PrintWriter(System.out));
            }
            ip.logger.info(bt.percentageGood());
            ip.allClosed();
          }
        }
      }
      catch (IOException ex) {
        ip.logger.error("Problem during batch processing: " +  ex);
      }
      break;
    case 'g':
      ip.startGui(nonOptionArgs.size() > 0 ? nonOptionArgs.get(0) : null);
      break;
    case 'c': // only "compile" rules
      break;
    default: {
      // trace flags
      int traceFlags = 0;
      if (options.has("t")) {
        traceFlags = Integer.parseInt((String) options.valueOf("t"));
      }

      if (nonOptionArgs.size() == 0) {
        usage("No project file specified");
      } else {
        ip.readProjectFile(new File(nonOptionArgs.get(0)));
        ip.interactive(traceFlags);
        ip.allClosed();
      }
      break;
    }
    }
  }
}
