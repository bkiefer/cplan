package de.dfki.lt.tr.dialogue.cplan.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import de.dfki.lt.loot.gui.DrawingPanel;
import de.dfki.lt.loot.gui.MainFrame;
import de.dfki.lt.loot.gui.adapters.EmptyModelAdapter;
import de.dfki.lt.loot.gui.adapters.ModelAdapter;
import de.dfki.lt.tr.dialogue.cplan.CollectEventsTracer;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.InteractivePlanner;
import de.dfki.lt.tr.dialogue.cplan.LoggingTracer;
import de.dfki.lt.tr.dialogue.cplan.BasicRule;
import de.dfki.lt.tr.dialogue.cplan.RuleTracer;
import de.dfki.lt.tr.dialogue.cplan.SuspendableTracer;
import de.dfki.lt.tr.dialogue.cplan.TraceEvent;
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
  private static final int CLEAR_BTN = TRACE_BTN + 1;
  private static final int REALIZE_BTN = CLEAR_BTN + 1;


  @Override
  protected Object[][] actionSpecs() {
    Object [][] results = {
    {"Load", "edit-redo", "Reload Rules", "Reload Rules",
      new Runnable() { public void run() { reloadCurrentProject(); } } },
    {"Process", "gnome-run", "Process Input", "Process",
      new Runnable() { public void run() { processInput(); } } },
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
    {"Realize", "insert-text", "Realize output", "Realize",
      new Runnable() {
       public void run() {
         String result = _planner.realize(_output);
         setStatusLine(result);
       }
      }
    }
    };
    return results;
  }

  @Override
  protected Object[][] menuSpecs() {
    Object [][] results = {
      { "New",
        KeyStroke.getKeyStroke(Character.valueOf('n'), InputEvent.ALT_DOWN_MASK),
        new Runnable() { public void run() { newFrame(); } }
      },
      {"Open", KeyEvent.VK_O,
        new Runnable() { public void run() { openFileDialog(); } }
      },
      {"Close",
        KeyStroke.getKeyStroke(Character.valueOf('w'),
          InputEvent.META_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
        new Runnable() { public void run() { close(); } }
      },
      { null },
      { "Recent Files", null, null },
      { null },
      { "Load History", null,
        new Runnable() { public void run() { loadHistory(); } } },
      { "Save History", null,
        new Runnable() { public void run() { saveHistory(); } } },
      { "Clear History", null,
        new Runnable() { public void run() { _history.clear(); } } },
      { null },
      {"Exit",
        KeyStroke.getKeyStroke(Character.valueOf('a'),
            InputEvent.ALT_DOWN_MASK),
        new Runnable() { public void run() { closeAll(); } }
      }
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
    return results;
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
  private Thread _processingThread;

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
    _planner = ip;
    _history = new de.dfki.lt.loot.gui.util.InputHistory();
    loadPreferences();
    _preferredSize = new Dimension(800, 500);
    setTracing();
    startEmacs();
    initFrame();
    updateButtonStates();
    _displayPane.setDividerLocation(.5);
    setTitle(title);
    openFile(ruleFile == null ? null : new File(ruleFile));

    setProcessorStopped();
    addRunStateListener(new RunStateListener() {
      @Override
      public void stateChanged() { updateButtonStates(); }
    });
  }

  /* **********************************************************************
   *  For GuiTracer, and other objects to communicate things to the user
   * ********************************************************************** */

  /** display the given data structure in the input area */
  public void setInputDisplay(DagNode dag) {
    _inputDisplay.setModel(dag);
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
  protected void newFrame() {
    UPMainFrame newMf = new UPMainFrame("Empty Planner");
    newMf.openFileDialog();
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
      @Override
      public void suspended(boolean isSuspended) {
        setProcessorSuspended(isSuspended);
      }
    });
    if (processInput()) {
      @SuppressWarnings("unused")
      TraceWindow tw = new TraceWindow(UPMainFrame.this, events);
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

  /** Set the project file to the given file and load its contents */
  @Override
  public boolean openFile(File projectFile) {
    if (projectFile == null) {
      _projectFile = null;
      _currentDir = new File(".");
    }
    else {
      setTitle("Reading project file: " + projectFile);
      _projectFile = projectFile;
      _currentDir = _projectFile.getAbsoluteFile().getParentFile();
      readProjectFile();
      _recentFiles.add(projectFile.getPath());
      try {
        File historyFile = _planner.getHistoryFile();
        if (historyFile == null) {
          historyFile = _currentDir;
        }
        _history.load(historyFile);
        setStatusLine("rule files reloaded");
      }
      catch (IOException ioex) {
        setStatusLine("Problem reading history file: "
            + ioex.getLocalizedMessage(), Color.RED);
      }
    }
    /** Enable/Disable reload rules, process, start trace
     *  depending on existence of rule file
     */
    updateButtonStates();
    return (projectFile == null || _projectFile != null);
  }

  @Override
  protected FileNameExtensionFilter getFileFilter() {
    return null;
  }

  // *************************************************************************
  // processing state, signalling and executing methods
  // *************************************************************************

  public void showRule(BasicRule r) {
    _planner.showRule(r);
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
    if (_oldTracer != null) {
      _planner.setTracing(_oldTracer);
    }
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

  /** This method takes the input string from the input area and tries to
   *  process it. If parsing the input reveals a syntax error, the caret is
   *  put to the error position and the error is signaled by using a reddish
   *  color as background for the input area. Otherwise, the processing is
   *  started in a new thread.
   */
  private boolean processInput() {
    clearStatusLine();
    _inputArea.setBackground(NORMAL_COLOR);
    String currentText = _inputArea.getText();
    if (currentText.isEmpty()) {
      return false;
    }

    _input = _planner.parseLfString(currentText);
    setInputDisplay(_input); // will be cleared if null
    if (_input != null) {
      // add currentText to the history
      _history.add(currentText);

      // should be in its own thread, with a listener that sets the
      // output, and an indicator that it's running
      if (isStopped()) {
        _processingThread = new Thread(
            new Runnable() {
              public void run() {
                try {
                  processingStarts();
                  _output = _planner.process(_input);
                  setInputDisplay(_input);
                  setOutputDisplay(_output);
                }
                finally {
                  processingEnds();
                }
              }
            } );
        _processingThread.start();
        return true;
      }
    }
    else {
      setOutputDisplay(null);
      Position errPos = _planner.getLastLFErrorPosition();
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
    if (_preferences != null && _preferences.containsKey("tracing")) {
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
          || ! _preferences.containsKey("emacs")
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
