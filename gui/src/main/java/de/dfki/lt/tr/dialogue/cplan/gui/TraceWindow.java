package de.dfki.lt.tr.dialogue.cplan.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JComponent;
//import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import de.dfki.lt.loot.gui.DrawingPanel;
import de.dfki.lt.loot.gui.MainFrame.RunnableAction;
import de.dfki.lt.loot.gui.adapters.EmptyModelAdapter;
import de.dfki.lt.loot.gui.adapters.ModelAdapter;
import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.TraceEvent;
import de.dfki.lt.tr.dialogue.cplan.util.ListRangeModel;

public class TraceWindow extends JFrame
implements UPMainFrame.RunStateListener {
  private static final long serialVersionUID = 1L;

  private static boolean DO_MODAL = false;

  /** Action Buttons */
  private ArrayList<JButton> _actionButtons;

  private UPMainFrame parent;

  /** LF displays for the match feature structure, the input and output to the
   *  current action */
  DrawingPanel _matchDisplay, _fromDisplay, _toDisplay;

  /** show the global variable bindings */
  JTable _globalsDisplay;

  /** show the match and action part of the current rule */
  JTextField _matchText;
  JTextField _actionText;

  /** a model to couple with the scroll bar to represent the current position */
  BoundedRangeModel _current;

  /** List of states that were collected in the last run */
  List<TraceEvent> _events;

  private final TraceWindowArrangement[] _arrangements ={
      new TabbedVertical(), new FourQuadrants()
  };
  public static final int TABBED_VERTICAL = 0;
  public static final int FOUR_QUADRANTS = 1;

  /** <code>Terminator</code> defines action to be done when closing a frame.
   */
  private class Terminator extends WindowAdapter {
    /** This creates a new instance of <code>Terminator</code>. */
    public Terminator() {
      super();
    }

    /** This overrides the <code>windowClosing</code> method of the super class.
     * @param we a <code>WindowEvent</code>
     * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent we) {
      we.getWindow().dispose();
    }

    /** This overrides the <code>windowClosed</code> method of the super class.
     * @param we a <code>WindowEvent</code>
     * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent we) {
      assert(we.getWindow() == TraceWindow.this);
      UPMainFrame myParent = parent();
      myParent.removeRunStateListener(TraceWindow.this);
      if (myParent.isSuspended()) {
        myParent.continueProcessing();
      }
      if (myParent.isRunning()) {
        myParent.stopProcessing();
      }
      myParent.getGlassPane().setVisible(false);
    }
  }

  /** A read-only model to display the global bindings in a JTable */
  private class BindingsTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private SortedMap<String, DagEdge> _globals;
    private ArrayList<String> _keys;

    /** create a new model with initial content */
    public BindingsTableModel(Bindings b) {
      setBindings(b);
    }

    /** \todo i could try to use DagNode as class and see what happens */
    @Override
    public Class<?> getColumnClass(int colIndex) {
      return String.class;
    }

    @Override
    public String getColumnName(int colIndex) {
      return (colIndex == 0) ? "Name" : "Value";
    }

    /** put a new set of bindings into this model, changing its content */
    public void setBindings(Bindings b) {
      if (b == null) {
        _globals = null;
        _keys = new ArrayList<String>(0);
      } else {
        _globals = b.getGlobalBindings();
        Set<String> keys = _globals.keySet();
        _keys = new ArrayList<String>(keys.size());
        _keys.addAll(keys);
      }
      this.fireTableDataChanged();
    }

    public int getColumnCount() { return 2; }

    public int getRowCount() { return _keys.size(); }

    /** This is a read-only model */
    @Override
    public boolean isCellEditable(int row, int col) { return false; }

    /** You're not allowed to call this method */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      assert(false);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= _keys.size())
        return "";
      String key = _keys.get(rowIndex);
      return (columnIndex == 0) ? key : _globals.get(key).getValue().toString();
    }
  }

  class ValueChangeListener implements ChangeListener {

    private JScrollBar scrollBar;

    ValueChangeListener(JScrollBar sb) { scrollBar = sb; }

    /** Called when the trace scrollbar is moved.
     *  Display the state for the new value */
    public void stateChanged(ChangeEvent e) {
      /* TODO ADAPT IF PAGE SCROLL DOES NOT FEEL RIGHT */
      int blockInc = 10;
      if (_current.getMaximum() < 10) {
        blockInc = ((int) _current.getMaximum() / 3);
      } else {
        blockInc = Math.min(10, (int) _current.getMaximum() / 6);
      }
      scrollBar.setBlockIncrement(blockInc);
      displayState(_current.getValue());
    }
  }

  class ShowRuleListener extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        parent().showRule(_events.get(_current.getValue()).rule);
      }
    }
  }


  /* **********************************************************************
   * Constructors
   * ********************************************************************** */

  public TraceWindow(UPMainFrame parent, ListRangeModel<TraceEvent> events) {
    this(parent, events, FOUR_QUADRANTS);
  }

  /** Open a new modal trace window.
   *  @param parent is the window i depend on, which also owns the processor.
   *  @param events is the list of events, which is also a BoundedRangeModel
   *         for use with a scroll bar.
   *  @param arrangement Either FOUR_QUADRANTS or TABBED_VERTICAL, determines
   *         the layout of the Match/Globals/In/Out sub-panels
   */
  @SuppressWarnings("serial")
  public TraceWindow(UPMainFrame myParent, ListRangeModel<TraceEvent> events,
    int arrangement) {
    super("Trace Window");
    parent = myParent;
    //super(parent, "Trace Window", false);
    if (arrangement < TABBED_VERTICAL || arrangement > FOUR_QUADRANTS) {
      throw new IllegalArgumentException("Unknown arrangement: " + arrangement);
    }
    if (DO_MODAL) {
      JComponent myGp = new JComponent() {
        @Override
        protected void paintComponent(Graphics g) {
          // translucent gray
          g.setColor(new Color((190F/255),(190F/255),(190F/255),.4F));
          //g.setColor(Color.RED);
          Rectangle r = getBounds();
          g.fillRect(r.x, r.y, r.width, r.height);
        }
      };
      parent.setGlassPane(myGp);
      myGp.setVisible(true);
    }
    _events = events;
    _current = events;
    initPanel(arrangement);
  }


  /* **********************************************************************
   * Display current state
   * ********************************************************************** */

  void displayState(int nr) {
    if (_events.size() <= nr) return;
    // put the appropriate things into the different parts of this window
    TraceEvent curr = _events.get(nr);
    _matchDisplay.setModel(curr.lastMatch);
    _matchDisplay.getMainView(curr.matchPoint.getValue()).setHighlight(true);

    _fromDisplay.setModel(nr == 0 ? curr.lastMatch : _events.get(nr -1).curr);
    _toDisplay.setModel(curr.curr);
    // TODO can we mark the changes in the structure?? We would have to mark
    // deletions in the in structure, and replacements/insertions in the out
    // structure. Not that easy.

    StringBuilder mat = new StringBuilder();
    curr.rule.appendMatches(mat);
    StringBuilder act = new StringBuilder();
    curr.rule.appendActions(act);

    _matchText.setText(mat.toString());
    _actionText.setText(act.toString());

    ((BindingsTableModel)_globalsDisplay.getModel()).setBindings(curr.bindings);
  }

  /* **********************************************************************
   * Initialization / Creation
   * ********************************************************************** */

  private DrawingPanel newLFPanel() {
    //return new DrawingPanel(new CompactLayout(), new LFModelAdapter());
    return new DrawingPanel(new LFLayout(),
                            new EmptyModelAdapter(ModelAdapter.MAP));
  }

  private static final int SHOW_BTN = 0;
  private static final int SUSPEND_BTN = SHOW_BTN + 1;
  private static final int CONT_BTN = SUSPEND_BTN + 1;
  private static final int STOP_BTN = CONT_BTN + 1;
  //private static final int CLOSE_BTN = STOP_BTN + 1;

  private final Object[][] actionSpecs = {
      {"Show Rule", "document-open", "Show Rule", "Show Rule",
        new Runnable() {
          public void run() {
            parent().showRule(_events.get(_current.getValue()).rule);
          }
        }
      },
      {"Suspend", "media-playback-pause", "Suspend Processing", "Suspend",
        new Runnable() { public void run() { parent().suspendProcessing(); } }
      },
      {"Continue", "media-playback-start", "Continue Processing", "Continue",
        new Runnable() { public void run() { parent().continueProcessing(); } }
      },
      {"Stop", "process-stop", "Stop Processing", "Emergency Stop",
        new Runnable() { public void run() { parent().stopProcessing(); } }
      },
      {"Close", "window-close", "Close Trace Window", "Close",
        new Runnable() { public void run() { close(); } }
      },
  };

  private UPMainFrame parent() {
    return //(UPMainFrame) getOwner();
        parent;
  }

  /** a method to close a frame from within the program */
  public void close() {
    parent().stopProcessing();
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  private JToolBar makeButtons() {
    _actionButtons = new ArrayList<JButton>();
    RunnableAction[] actions = new RunnableAction[actionSpecs.length];
    int i = 0;
    for (Object[] spec : actionSpecs) {
      actions[i++] = new RunnableAction((String) spec[0], (String) spec[1],
          (String) spec[2], (String) spec[3], null, (Runnable) spec[4]);
    }
    JToolBar toolbar =
        parent().newToolBar(actions, "Trace Buttons", _actionButtons);
    toolbar.setFloatable(false);
    return toolbar;
  }

  public void stateChanged() {
    UPMainFrame parentFrame = parent();
    _actionButtons.get(SUSPEND_BTN).setEnabled(parentFrame.isRunning() &&
                                               ! parentFrame.isSuspended());
    _actionButtons.get(CONT_BTN).setEnabled(parentFrame.isRunning() &&
                                            parentFrame.isSuspended());
    _actionButtons.get(STOP_BTN).setEnabled(parentFrame.isRunning());
  }

  private abstract class TraceWindowArrangement {
    public abstract JComponent arrange(JToolBar toolbar,
      JComponent matchDisplay, JComponent globalsDisplay, JComponent matchText,
      JComponent fromDisplay, JComponent toDisplay, JComponent actionText);

    protected JSplitPane balancedSplitPane(int split,
      JComponent upper, JComponent lower) {
      JSplitPane splitPane = new JSplitPane(split, upper, lower);
      splitPane.setDividerLocation(.5);
      splitPane.setResizeWeight(.5);
      return splitPane;
    }

    protected JPanel upDownPane(JComponent upper, JComponent lower) {
      JPanel result = new JPanel(new BorderLayout());
      result.add(upper, BorderLayout.CENTER);
      result.add(lower, BorderLayout.SOUTH);
      return result;
    }
  }

  private class FourQuadrants extends TraceWindowArrangement {
    @Override
    public JComponent arrange(JToolBar toolbar,
      JComponent matchDisplay, JComponent globalsDisplay, JComponent matchText,
      JComponent fromDisplay, JComponent toDisplay, JComponent actionText) {
      GridBagLayout gridbag = new GridBagLayout();
      JPanel displayPane = new JPanel(gridbag);
      GridBagConstraints c = new GridBagConstraints();

      JSplitPane upperSplitPane = balancedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
          matchDisplay, globalsDisplay);
      JSplitPane lowerSplitPane = balancedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
          fromDisplay, toDisplay);

      c.fill = GridBagConstraints.BOTH;
      c.gridx = 0; c.gridy = 0; c.gridwidth = 1; c.weightx = 1; c.weighty = 0;
      gridbag.setConstraints(toolbar, c); displayPane.add(toolbar);
      ++c.gridy; c.weighty = 1;
      gridbag.setConstraints(upperSplitPane, c); displayPane.add(upperSplitPane);
      ++c.gridy; c.weighty = 0;
      gridbag.setConstraints(matchText, c); displayPane.add(matchText);
      ++c.gridy; c.weighty = 1;
      gridbag.setConstraints(lowerSplitPane, c); displayPane.add(lowerSplitPane);
      ++c.gridy; c.weighty = 0;
      gridbag.setConstraints(actionText, c); displayPane.add(actionText);

      return displayPane;
    }
  }

  private class TabbedVertical extends TraceWindowArrangement {
    @Override
    public JComponent arrange (JToolBar toolbar,
      JComponent matchDisplay, JComponent globalsDisplay, JComponent matchText,
      JComponent fromDisplay, JComponent toDisplay, JComponent actionText) {
      // create a tabbed pane for match and globals
      JTabbedPane matchUpperPane = new JTabbedPane(JTabbedPane.TOP) ;
      matchUpperPane.addTab("MatchLF", matchDisplay);
      matchUpperPane.addTab("Globals", globalsDisplay);

      toolbar.setOrientation(JToolBar.VERTICAL);

      JPanel matchPane = upDownPane(matchUpperPane, matchText);
      matchPane.add(toolbar, BorderLayout.WEST);

      JSplitPane splitPane =
        balancedSplitPane(JSplitPane.HORIZONTAL_SPLIT, fromDisplay, toDisplay);

      JPanel actionPane = upDownPane(splitPane, _actionText);
      JSplitPane displayPane =
        new JSplitPane(JSplitPane.VERTICAL_SPLIT, matchPane, actionPane);
      return displayPane;
    }
  }

  private void initPanel(int arrangement) {
      // create content panel and add it to the frame
    JPanel contentPane = new JPanel(new BorderLayout());
    //contentPane.setLayout(new BorderLayout());
    this.setContentPane(contentPane);

    // create scrollable display areas
    _matchDisplay = newLFPanel();
    _matchDisplay.setToolTipText("LF that is matched against");
    JScrollPane matchDisplay = new JScrollPane(_matchDisplay);
    _globalsDisplay = new JTable(new BindingsTableModel(null));

    _matchText = new JTextField();
    _matchText.setEditable(false);
    _matchText.setToolTipText("Match part, double click to go to rule");
    _matchText.addMouseListener(new ShowRuleListener());

    JToolBar buttonContainer = makeButtons();

    _fromDisplay = newLFPanel();
    _fromDisplay.setToolTipText("Input to action");
    _toDisplay = newLFPanel();
    _toDisplay.setToolTipText("Output of action");
    JScrollPane fromDisplay = new JScrollPane(_fromDisplay);
    JScrollPane toDisplay = new JScrollPane(_toDisplay);

    _actionText = new JTextField();
    _actionText.setEditable(false);
    _actionText.setToolTipText("Action part, double click to go to rule");
    _actionText.addMouseListener(new ShowRuleListener());

    JComponent displayPane =
      _arrangements[arrangement].arrange(buttonContainer,
          matchDisplay, _globalsDisplay, _matchText,
          fromDisplay, toDisplay, _actionText);

    contentPane.add(displayPane, BorderLayout.CENTER);
    JScrollBar traceScroller = new JScrollBar(JScrollBar.HORIZONTAL);
    traceScroller.setModel(_current);
    _current.addChangeListener(new ValueChangeListener(traceScroller));
    contentPane.add(traceScroller, BorderLayout.PAGE_END);

    // use native windowing system to position new frames
    this.setLocationByPlatform(true);
    this.setPreferredSize(new Dimension(800, 500));
    // set handler for closing operations
    this.addWindowListener(new Terminator());
    // display the frame
    this.pack();
    //displayPane.setDividerLocation(.33);
    int unitIncrement = _matchDisplay.getDefaultTextHeight();
    // display the frame
    matchDisplay.getHorizontalScrollBar().setUnitIncrement(unitIncrement);
    matchDisplay.getVerticalScrollBar().setUnitIncrement(unitIncrement);
    fromDisplay.getHorizontalScrollBar().setUnitIncrement(unitIncrement);
    fromDisplay.getVerticalScrollBar().setUnitIncrement(unitIncrement);
    toDisplay.getHorizontalScrollBar().setUnitIncrement(unitIncrement);
    toDisplay.getVerticalScrollBar().setUnitIncrement(unitIncrement);
    stateChanged();
    parent().addRunStateListener(this);
    _current.setValue(0);
    this.setVisible(true);
  }
}
