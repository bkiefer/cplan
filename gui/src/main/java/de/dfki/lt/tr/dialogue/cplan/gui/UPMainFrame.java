package de.dfki.lt.tr.dialogue.cplan.gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.BadLocationException;

import de.dfki.lt.loot.gui.DrawingPanel;
import de.dfki.lt.loot.gui.MainFrame;
import de.dfki.lt.loot.gui.Style;
import de.dfki.lt.loot.gui.adapters.EmptyModelAdapter;
import de.dfki.lt.loot.gui.adapters.ModelAdapter;
import de.dfki.lt.loot.gui.util.FileAssociation;
import de.dfki.lt.loot.gui.util.FileProcessor;
import de.dfki.lt.loot.gui.util.FileProcessorAdapter;
import de.dfki.lt.tr.dialogue.cplan.*;
import de.dfki.lt.tr.dialogue.cplan.batch.BatchTest;
import de.dfki.lt.tr.dialogue.cplan.batch.ProgressListener;
import de.dfki.lt.tr.dialogue.cplan.batch.BatchTest.BatchType;
import de.dfki.lt.tr.dialogue.cplan.util.ListRangeModel;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

/**
 * <code>UPMainFrame</code> defines the main window of the content planner
 * interactive GUI
 *
 * @author Bernd Kiefer, DFKI
 * @version
 */
@SuppressWarnings("serial")
public class UPMainFrame extends MainFrame {

  /** FileProcessor for project files
   */
  private static class ProjectFileProcessor extends FileProcessorAdapter {
    /** Set the project file to the given file and load its contents */
    public boolean processFile(File projectFile, MainFrame mf) {
      UPMainFrame upmf = null;
      try {
        upmf = (UPMainFrame)mf;
      }
      catch (ClassCastException cex) {
        // for total safety
        logger.error("ProjectFileProcessor was called on a non UPMainFrame");
        return false;
      }
      if (projectFile == null) {
        upmf._projectFile = null;
        upmf._currentDir = new File(".");
      }
      else {
        upmf.setTitle("Reading project file: " + projectFile);
        upmf._projectFile = projectFile;
        upmf._currentDir = upmf._projectFile.getAbsoluteFile().getParentFile();
        upmf.readProjectFile();
        upmf._preferences.recentFiles().add(projectFile.getAbsolutePath());
        try {
          File historyFile = upmf._planner.getHistoryFile();
          if (historyFile == null) {
            historyFile = upmf._currentDir;
          }
          upmf._history.load(historyFile);
          upmf.setStatusLine("rule files reloaded");
        }
        catch (IOException ioex) {
          upmf.setStatusLine("Problem reading history file: "
              + ioex.getLocalizedMessage(), Color.RED);
        }
      }
      /** Enable/Disable reload rules, process, start trace
       *  depending on existence of rule file
       */
      upmf.updateButtonStates();
      return (projectFile == null || upmf._projectFile != null);
    }
  }

  /** Objects that listen to the change of the processor's run state */
  private List<RunStateListener> _runStateListeners =
    new ArrayList<RunStateListener>();

  private static final Color NORMAL_COLOR = Color.white;
  private static final Color ERROR_COLOR = new Color(255, 150, 150);

  private static final int LOAD_BTN = 0;
  private static final int PROCESS_BTN = LOAD_BTN + 1;
  private static final int TRACE_BTN = PROCESS_BTN + 1;
  private static final int STOP_BTN = TRACE_BTN + 1;
  /*
  private static final int STEP_BTN = STOP_BTN + 1;
  private static final int CONTINUE_BTN = STEP_BTN + 1;
  */
  private static final int CLEAR_BTN = STOP_BTN + 1;
  private static final int REALIZE_BTN = CLEAR_BTN + 1;
  private static final int PARSE_BTN = REALIZE_BTN + 1;
  private static final int BATCH_REALIZE_BTN = PARSE_BTN + 1;
  private static final int BATCH_PARSE_BTN = BATCH_REALIZE_BTN + 1;
  private static final int BATCH_PLAN_BTN = BATCH_PARSE_BTN + 1;

  private RunnableAction[] _actions = null;

  /* specs describing the buttons in the tool bar */
  @Override
  protected RunnableAction[] actionSpecs() {
    Object [][] specs = {
    {"Load", "edit-redo", "Reload Rules", "Reload Rules",
      new Runnable() { public void run() { reloadCurrentProject(); } } },
    {"Process", "gnome-run", "Process Input", "Process",
      new Runnable() { public void run() { processInput(true, false); } } },
    {"Trace", "go-next", "Trace Processing", "Trace",
      new Runnable() { public void run() { processTracing(); } } },
    /*
    {"Step", "go-jump", "Next Trace Step", "Step",
      new Runnable() { public void run() { continueProcessing(); } } },
    {"Continue", "go-last", "Continue until end", "Continue",
      new Runnable() {
        public void run() { _tracer.setTracing(0); continueProcessing(); }
      }
    },
    */
    {"Stop", "process-stop", "Stop processing", "Emergency Stop",
      new Runnable() { public void run() { stopProcessing(); } }
    },
    {"Clear", "edit-clear", "Clear Input", "Clear",
      new Runnable() { public void run() { clearInput(); } }
    },
    {"Realize", "generate-text", "Realize output", "Realize",
      new Runnable() { public void run() { processInput(true, true); } }
    },
    {"Parse", "insert-text", "Analyze Sentence", "Parse",
      new Runnable() { public void run() { parseInput(); } }
    },
    {"Batch realization", "batch", "Batch realize", "Batch realize",
      new Runnable() { public void run() { batchProcess(BatchType.GENERATION);}}
    },
    {"Batch parse", "batch", "Batch parse", "Batch parse",
      new Runnable() { public void run() { batchProcess(BatchType.PARSING);}}
    },
    {"Batch plan", "batch", "Batch plan", "Batch plan",
      new Runnable() { public void run() { batchProcess(BatchType.PLANNING);}}
    }
    };
    _actions = new RunnableAction[specs.length];
    int i = 0;
    for(Object[] spec : specs){
      _actions[i++] = new RunnableAction((String) spec[0], (String) spec[1],
          (String) spec[2], (String) spec[3], null, (Runnable) spec[4]);
    }
    return _actions;
  }

  @Override
  protected MyMenu[] menuSpecs(RunnableAction[] actions) {
    Object[] specs = {
      newAction(),
      openAction(),
      closeAction(),
      null,
      recentFiles(),
      null,
      loadHistoryAction(), saveHistoryAction(), clearHistoryAction(),
      null,
      exitAction(),
      /*
      {"Select Font", null,
         new Runnable() {
          public void run() {
           FontChooser f = new FontChooser();
           f.getChosenFont();
         }
       }
      }
      */
    };
    MyMenu[] menuBarSpecs = {
        new MyMenu("File", KeyEvent.VK_F, specs),
    };

    return menuBarSpecs;
  }


  /* *************************************************************************
   * fields for GUI elements
   * *************************************************************************/

  /** contains the input text */
  private JTextArea _inputArea;

  /** This displays the input lf */
  private DrawingPanel _inputDisplay;

  /** This displays the output lf */
  private DrawingPanel _outputDisplay;

  /** The split pane containing the above displays */
  private JSplitPane _displayPane;

  /* *************************************************************************
   * fields for processing elements beyond the GUI
   * *************************************************************************/

  /** This contains the selected rules file and current directory. */
  //private File _currentDir = null;
  private File _projectFile = null;

  /** The processing unit behind this frame */
  private InteractivePlanner _planner = null;

  /** RuleTracer objects to perform tracing */
  private RuleTracer _oldTracer = null;
  private CollectEventsTracer _tracer = null;

  private DagNode _input, _output;
  // thread for running single requests
  private Thread _processingThread;
  // thread for running a batch test
  private Thread _batchThread;

  private int _processorState;
  private static final int STOPPED = 0;
  private static final int RUNNING = 1;
  private static final int SUSPENDED = 2;


  /* **********************************************************************
   * Constructors
   * ********************************************************************** */

  public UPMainFrame(String title) {
    this(title, new InteractivePlanner(), null);
  }

  public UPMainFrame(String title, InteractivePlanner ip, String ruleFile) {
    _fileProcessor = new ProjectFileProcessor();
    _planner = ip;

    int historySize = 0;
    try {
      if (_planner.hasSetting("history_size")) {
        historySize = Integer.parseInt((String)_planner.getSetting("history_size"));
      }
    } catch (NumberFormatException nex) {
      _planner.getLogger().warn("specified history size is not a number");
    }
    _history = new de.dfki.lt.loot.gui.util.InputHistory(historySize);

    _textFont = new Font("Liberation Mono", Font.PLAIN, 18);
    super.setUIFont(new FontUIResource(_textFont));
    Style.increaseDefaultFontSize((double)_textFont.getSize() / 12.0);

    _preferredSize = new Dimension(800, 500);
    initFrame();
    setTracing();
    startEmacs();
    _toolBarName = "Content Planner Tools";
    updateButtonStates();
    _displayPane.setDividerLocation(.5);
    setTitle(title);
    try {
      _fileProcessor.processFile(ruleFile == null ? null : new File(ruleFile),
          this);
    } catch (IOException e) {
      _planner.getLogger().error("Problem reading project file" + e);
    }

    setProcessorStopped();
    addRunStateListener(new RunStateListener() {
      public void stateChanged() { updateButtonStates(); }
    });
  }

  /* **********************************************************************
   *  For GuiTracer, and other objects to communicate things to the user
   * ********************************************************************** */

  /** set the field and display the given data structure in the input area */
  public void setInput(DagNode dag) {
    setInputDisplay(_input = dag);
    _inputArea.setText(dag.toString());
  }

  /** display the given data structure in the input area */
  public void setInputDisplay(DagNode dag) {
    _inputDisplay.setModel(dag);
  }

  /** set the field and display the given data structure in the output area */
  public void setOutput(DagNode dag) {
    setOutputDisplay(_output = dag);
  }

  /** display the given data structure in the output area */
  public void setOutputDisplay(DagNode dag) {
    _outputDisplay.setModel(dag);
  }


  /* **********************************************************************
   * Action methods
   * ********************************************************************** */

  /** Create a new frame for a possibly different project */
  @Override
  protected MainFrame newFrame() {
    UPMainFrame newMf = new UPMainFrame("Empty Planner");
    newMf.openFileDialog(newMf._fileProcessor);
    return newMf;
  }

  /** Wipe the input area clean */
  private void clearInput() {
    _inputArea.setBackground(NORMAL_COLOR);
    _inputArea.setText("");
  }

  /** Wipe the input area clean */
  @Override
  protected void setInput(String text) {
    _inputArea.setBackground(NORMAL_COLOR);
    _inputArea.setText(text);
  }

  private void processTracing() {
    ListRangeModel<TraceEvent> events = new ListRangeModel<TraceEvent>();
    _tracer = new CollectEventsTracer(events);
    _tracer.addListener(new SuspendableTracer.SuspendListener() {
      public void suspended(boolean isSuspended) {
        setProcessorSuspended(isSuspended);
      }
    });
    if (processInput()) {
      @SuppressWarnings("unused")
      TraceWindow tw = new TraceWindow(UPMainFrame.this, events);
      runPlanner();
    }
  }

  // **********************************************************************
  // PROJECT FILE HANDLING
  // **********************************************************************

  /** read the file that contains paths to the rule files to be loaded */
  public void readProjectFile() {
    clearStatusLine();
    _planner.readProjectFile(_projectFile);
    if (! _planner.getErrors().isEmpty()) {
      int errs = _planner.getErrors().size();
      setStatusLine(String.format("WARNING: %1d error%2s in rule files",
                                  errs, (errs == 1 ? "" : "s")),
                    Color.RED);
    }
    LFMapFacetLayout.init();
    this.setTitle(_projectFile.getPath());
  }

  /** Load the current project, show an error Dialog if there is none */
  public void reloadCurrentProject() {
    if (_projectFile == null) {
      JOptionPane.showMessageDialog(UPMainFrame.this, "Error",
          "No Rule File Loaded", JOptionPane.ERROR_MESSAGE);
    } else {
      readProjectFile();
    }
    updateButtonStates();
  }



  // *************************************************************************
  // processing state, signalling and executing methods
  // *************************************************************************

  public void showPosition(Position p) {
    _planner.showPosition(p);
  }

  public void showRule(BasicRule r) {
    showPosition(r.getPosition());
  }

  public interface RunStateListener {
    public abstract void stateChanged();
  }

  public void addRunStateListener(RunStateListener rsl) {
    _runStateListeners.add(rsl);
  }

  public void removeRunStateListener(RunStateListener rsl) {
    _runStateListeners.remove(rsl);
  }

  public boolean isRunning() {
    return (_processorState & RUNNING) != 0;
  }

  public boolean isStopped() {
    return (_processorState & RUNNING) == 0;
  }

  public boolean isSuspended() {
    return (_processorState & SUSPENDED) != 0;
  }

  private void informRunStateListeners() {
    for (RunStateListener rsl : _runStateListeners) {
      rsl.stateChanged();
    }
  }

  private void setProcessorRunning() {
    _processorState = RUNNING;
    informRunStateListeners();
  }
  private void setProcessorStopped() {
    _processorState = STOPPED;
    informRunStateListeners();
  }
  private void setProcessorSuspended(boolean isSuspended) {
    if (isSuspended) {
      _processorState |= SUSPENDED;
    } else {
      _processorState &= ~ SUSPENDED;
    }
    informRunStateListeners();
  }

   /** Sets the _finished flag to what, which signal if the processor is
   *  processing input or not, and enable/disable buttons in the tool bar
   *  depending on this flag (and other conditions.
   */
  private void updateButtonStates() {
    boolean runnable = _projectFile != null && isStopped();
    _actionButtons.get(LOAD_BTN).setEnabled(_projectFile != null);
    _actionButtons.get(PROCESS_BTN).setEnabled(runnable);
    /** EnDisable gui tracing when text tracing was enabled */
    _actionButtons.get(TRACE_BTN).setEnabled(runnable);
    _actionButtons.get(STOP_BTN).setEnabled(_projectFile != null && isRunning());
    /** Enable/Disable step tracing *
    _actionButtons.get(STEP_BTN).setEnabled(! _finished);
    _actionButtons.get(CONTINUE_BTN).setEnabled(! _finished);
    **/
    _actionButtons.get(REALIZE_BTN).setEnabled(runnable);
    _actionButtons.get(PARSE_BTN).setEnabled(runnable);
    _actionButtons.get(BATCH_REALIZE_BTN).setEnabled(runnable);
    _actionButtons.get(BATCH_PARSE_BTN).setEnabled(runnable);
    _actionButtons.get(BATCH_PLAN_BTN).setEnabled(runnable);
  }

  /** Call this method when the processing of new input starts */
  private void processingStarts() {
    if (_oldTracer == null) {
      _oldTracer = _planner.getTracing();
    }
    if (_tracer != null) {
      _planner.setTracing(_tracer);
    }
    setProcessorRunning();
  }

  /** Call this method when the processing of input has finished */
  private void processingEnds() {
    if (_tracer != null) {
      _tracer = null;
    }
    _planner.setTracing(_oldTracer);
    setProcessorStopped();
  }

  public void suspendProcessing() {
    if (_tracer != null) _tracer.suspendProcessing();
  }

  public void continueProcessing() {
    if (_tracer != null) _tracer.continueProcessing();
  }

  public void stopProcessing() {
    continueProcessing();
    _planner.interruptProcessing();
  }

  /** Start the planner in a new thread, signalling termination via the
   *  registered RunStateListeners.
   *
   *  If startRealization is true, the CCG realizer will be called after the
   *  graph rewriting phase
   *
   * @param startRealization if true, call the CCG realizer, if available.
   */
  private void runPlanner(final boolean startRealization) {
    // should be in its own thread, with a listener that sets the
    // output, and an indicator that it's running
    _processingThread = new Thread(
        new Runnable() {
          public void run() {
            try {
              processingStarts();
              setInputDisplay(_input);
              setOutput(_planner.process(_input));
              if (_output == null) {
                setStatusLine("No output", Color.ORANGE);
              } else {
                if (startRealization)
                  setStatusLine(_planner.doRealization(_output));
              }
            }
            catch (PlanningException ex) {
              setStatusLine(ex.getMessage(), Color.ORANGE);
            }
            catch (NullPointerException ex) {
              setStatusLine("Exception during realization", Color.RED);
            }
            finally {
              processingEnds();
            }
          }
        } );
    _processingThread.start();
  }

  /** @see runPlanner(boolean startRealization) */
  private void runPlanner() { runPlanner(false); }

  /** This method takes the input string from the input area and tries to
   *  process it. If parsing the input reveals a syntax error, the caret is
   *  put to the error position and the error is signaled by using a reddish
   *  color as background for the input area. Otherwise, the processing is
   *  started in a new thread.
   */
  private boolean processInput(boolean startPlanner, boolean startRealization) {
    clearStatusLine();
    _inputArea.setBackground(NORMAL_COLOR);
    String currentText = _inputArea.getText();
    if (currentText.isEmpty()) {
      return false;
    }

    _input = DagNode.parseLfString(currentText);
    setOutputDisplay(null);
    if (_input != null) {
      setInputDisplay(_input);
      // add currentText to the history
      _history.add(currentText);
      if (! isStopped()) return false;
      if (startPlanner)
        runPlanner(startRealization);
      return true;
    }
    else {
      Position errPos = DagNode.getLastLFErrorPosition();
      _inputArea.setBackground(ERROR_COLOR);
      if (errPos.line >= 0) {
        setStatusLine(errPos.msg);
        try {
          int offset = _inputArea.getLineStartOffset(errPos.line - 1);
          offset += errPos.column - 2;
          // _inputArea.insert("\u26A1", offset);
          _inputArea.setCaretPosition(offset);
          _inputArea.requestFocus();
        }
        catch (BadLocationException blex) {
          // just ignore;
          System.out.println("" + blex + errPos.line + errPos.column);
        }
      }
    }
    return false;
  }

  /** @see processInput(boolean startPlanner, boolean startRealization) */
  private boolean processInput() { return processInput(false, false); }

  private boolean parseInput() {
    clearStatusLine();
    _inputArea.setBackground(NORMAL_COLOR);
    String currentText = _inputArea.getText();
    if (currentText.isEmpty()) {
      return false;
    }
    try {
      _input = _planner.analyze(currentText);
      if (_input == null) {
        clearInput(); // will be cleared if null
      } else {
        _history.add(currentText);
        setInputDisplay(_input);
        if (! isStopped()) return false;
        runPlanner();
        return true;
      }
    }
    catch (IllegalArgumentException ex) {
      setStatusLine(ex.getMessage());
    }
    return _input == null;
  }

  private class BatchProcessor implements FileProcessor {
    public BatchTest bt = null;
    public BatchType realizationTest;
    private CcgUtterancePlanner _batchPlanner;

    public BatchProcessor(CcgUtterancePlanner planner, BatchType realize) {
      _batchPlanner = planner;
      realizationTest = realize;
    }

    public boolean processFile(File toProcess, MainFrame mf) {
      try {
        bt = _batchPlanner.loadBatch(toProcess, realizationTest);
      } catch (IOException e) {
        return false;
      }
      return true;
    }

    public FileAssociation getAssociation(String extension) {
      throw new UnsupportedOperationException();
    }

    public FileFilter getFileFilter() { return null; }
  }

  private void batchProcess(BatchType realize) {
    if (_batchThread != null) {
      setStatusLine("Batch already running", Color.RED);
      return;
    }

    // select and process batch file
    final BatchProcessor bp = new BatchProcessor(_planner, realize);
    openFileDialog(bp);
    if (bp.bt == null)
      return;

    showProgressBar();
    bp.bt.setProgressListener(new ProgressListener() {
      private de.dfki.lt.loot.gui.util.ProgressListener pl =
          getProgressBarListener();
      public void setMaximum(int max) { pl.setMaximum(max); }
      public void progress(int value) { pl.progress(value); }
    });

    _batchThread = new Thread(
        new Runnable() {
          public void run() {
            try {
              bp.bt.runBatch();
            }
            catch (PlanningException ex) {
              setStatusLine(ex.getMessage(), Color.ORANGE);
            }
            finally {
              hideProgressBar();
              if (bp.bt.totalSuccess()) {
                setStatusLine("All Test Items passed", Color.GREEN);
              }
              // show the failing items in a list window
              //openItemsWindow(bp.bt);
              @SuppressWarnings("unused")
              ItemsTableWindow tw =
              new ItemsTableWindow(UPMainFrame.this, bp.bt, true, true);
              _batchThread = null;
            }
          }
        } );
    _batchThread.start();
  }


  /* **********************************************************************
   * Initialization / Creation
   * ********************************************************************** */

  @Override
  protected File getPreferencesFile() {
    return _planner.getPreferencesFile();
  }

  @Override
  protected File getResourcesDir() {
    return _planner.getResourcesDir();
  }

  @Override
  protected void reportProblem(String msg) {
    setStatusLine(msg, Color.RED);
  }

  private DrawingPanel newLFPanel() {
    return new DrawingPanel(new LFLayout(),
                            new EmptyModelAdapter(ModelAdapter.MAP));
  }


  private JComponent newInputArea() {
     // The area to input lf's
    _inputArea = new JTextArea();
    _inputArea.setFont(_textFont);
    _inputArea.setRows(5);
    //_inputArea.setColumns(70);
    return new JScrollPane(_inputArea);
  }


  @Override
  protected Component newContentPane() {
    // create scrollable display areas
    _inputDisplay = newLFPanel();
    _outputDisplay = newLFPanel();

    // this must be a field of the frame because the divider location can not
    // be set here
    _displayPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        _inputDisplay.wrapScrollable(),
        _outputDisplay.wrapScrollable());
    _displayPane.setOneTouchExpandable(true);
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        newInputArea(), _displayPane);
    return splitPane;
  }


  private void setTracing() {
    int traceFlags = 0;
    if (_preferences != null && _preferences.contains("tracing")) {
      String trace =  _preferences.get("tracing");
      if (trace.startsWith("all")) {
        traceFlags = 3;
      } else if (trace.startsWith("match")) {
        traceFlags = 1;
      } else if (trace.startsWith("action")) {
        traceFlags = 2;
      }
    }

    if (traceFlags != 0) {
      _planner.setTracing(new LoggingTracer(traceFlags));
    }
  }

  private void startEmacs() {
    // check if it has been started with the -e flag
    if (! _planner.isEmacsAlive()) {
      if (_preferences == null
          || ! _preferences.contains("emacs")
          || _preferences.get("emacs").equals("yes")) {
        _planner.startEmacsConnection(null);
      } else {
        if (! _preferences.get("emacs").equals("no")) {
          _planner.startEmacsConnection(_preferences.get("emacs"));
        }
      }
    }
  }
}
