package de.dfki.lt.tr.dialogue.cplan;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.gui.LFModelAdapter;
import de.dfki.lt.tr.dialogue.cplan.gui.UPMainFrame;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public class InteractivePlanner extends CcgUtterancePlanner
implements UPMainFrame.CloseAllListener {

  /** The root directory that contains resources in its etc/ subdirectory */
  private File _rootDir;

  /** The buffer name for the file output in case of a connection to Emacs */
  private String _compilationBufferName = "*cplanner*";
  /** An Emacs connector */
  private J2Emacs _j2e = null;

  public InteractivePlanner() {
    LFModelAdapter.init();
  }

  public File getResourcesDir() {
    return new File(_rootDir, "etc/");
  }

  public File getPreferencesFile() {
    File prefs = new File(_rootDir, ".cplanner");
    return prefs;
  }

  private boolean setRootDir(File file) {
    _rootDir = file;
    if (! _rootDir.isDirectory() || ! getResourcesDir().isDirectory()) {
      _rootDir = null;
      return true;
    }
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

  public static class BatchTest {
    public List<TestItem> items = new ArrayList<TestItem>();
    public List<BadItem> bad = new ArrayList<BadItem>();

    public String percentageGood() {
      int good = items.size() - bad.size();
      return "Successful: " + good + " (" +
      (int)((100.0 * good) / items.size()) + "%)";
    }
  }

  public static class TestItem {
    public DagNode lf;
    public Set<String> answers;
    public Position position;

    public TestItem(DagNode d, Set<String> a, Position l) {
      lf = d;
      answers = a;
      position = l;
    }
  }

  public static class BadItem {
    public int testItemIndex;
    public DagNode outputLf;
    public String realized;

    public BadItem(int t, DagNode out, String r) {
      testItemIndex = t;
      outputLf = out;
      realized = r;
    }
  }

  public BatchTest batchProcess(File batchFile) throws IOException {
    Reader in = new FileReader(batchFile);
    Lexer l = new Lexer(batchFile.getCanonicalPath(), in);
    LFParser parser = new LFParser(l);
    BatchTest bt = new BatchTest();
    do {
      boolean good = parser.parse();
      Position pos = l.getStartPos();
      if (! good) break;
      DagNode nextLf = parser.getResultLF();
      Set<String> answers = new HashSet<String>();
      do {
        String nextSentence = l.readLine();
        if (nextSentence.isEmpty()) break;
        answers.add(nextSentence);
      } while (true);
      bt.items.add(new TestItem(nextLf, answers, pos));
    } while (! l.atEOF());
    int i = 0;
    for (TestItem item : bt.items) {
      DagNode result = process(item.lf);
      String generated = "";
      try {
        generated = doRealization(result);
      }
      catch (NullPointerException ex) {
        generated = "*FAILURE*";
      }
      if (! item.answers.contains(generated)) {
        bt.bad.add(new BadItem(i, result, generated));
      }
      ++i;
    }
    return bt;
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
      _j2e.startEmacs(f, p.line, p.column, "");
    }
    catch (IOException ioex) {
      Logger.getRootLogger().warn(ioex);
    }
  }

  private static void usage(String msg) {
    String[] usage = {
        "Usage: UPDebugger [-b<atch> inputfile] [-c<ompileonly>] [-d<ebugdags>]",
        "                  [-g<ui>] [-t<race>={1,2,3}] [-e<macs>]",
        "                  <projectfile>",
        "      -t : bit 1: trace match, bit 2 : trace modification"
    };
    System.out.println(msg);
    for (String us : usage) System.out.println(us);
    System.exit(1);
  }

  @SuppressWarnings({ "unchecked", "null" })
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

    OptionParser parser = new OptionParser("b:cdgt:e::r:");
    OptionSet options = null;
    try {
      options = parser.parse(args);
    }
    catch (OptionException ex) {
      usage("Error parsing options: " + ex.getLocalizedMessage());
      System.exit(1);
    }

    if (! options.has("r")) {
      usage("root directory must be specified");
      System.exit(1);
    }
    File rootDir = new File((String) options.valueOf("r"));

    List<String> nonOptionArgs = options.nonOptionArguments();

    String optionArg = null;

    char what = 'i';
    // x and T are only for test purposes
    String[] actionOptions = { "b", "c", "g" };
    for (String action : actionOptions) {
      if (options.has(action)) {
        if (what != 'i')
          usage("Only one of -b, -c or -g allowed.") ;
        what = action.charAt(0);
        optionArg = (String) options.valueOf(action);
      }
    }

    InteractivePlanner ip = new InteractivePlanner();
    if (ip.setRootDir(rootDir)) {
      usage("argument of -r is not the root directory:" + rootDir);
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
    case 'b':
      try {
        if (nonOptionArgs.size() == 0) {
          usage("No project file specified");
        } else {
          File batchFile = new File(optionArg);
          if (! batchFile.exists()) {
            usage("Batch input file not found:" + batchFile);
          } else {
            ip.readProjectFile(new File(nonOptionArgs.get(0)));
            BatchTest bt = ip.batchProcess(batchFile);
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
