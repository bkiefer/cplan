package de.dfki.lt.tr.dialogue.cplan.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
//import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.dfki.lt.loot.gui.MainFrame.RunnableAction;
import de.dfki.lt.tr.dialogue.cplan.BatchTest;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.ParsingTestItem;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.RealizationTestItem;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.ResultItem;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.TestItem;
import java.awt.event.WindowAdapter;

public class ItemsTableWindow extends JFrame {
  private static final long serialVersionUID = 1L;

  private ItemsTableModel _model;

  private boolean _showGood, _showBad;

  private boolean[] _showColumn = { true, true, false, true, false };

  private static BiFunction<ResultItem, BatchTest, String>[] _showFunction = new BiFunction[5];
  static {
    _showFunction[0] = (ResultItem item, BatchTest bt) -> item.itemStatus.name().substring(0, 1);
    _showFunction[1] = (ResultItem item, BatchTest bt) -> bt.getItem(item.testItemIndex).input().toString();
    _showFunction[2] = (ResultItem item, BatchTest bt) -> item.outputLf.toString(true);
    _showFunction[3] = (ResultItem item, BatchTest bt) -> item.realized;
    _showFunction[4] = (ResultItem item, BatchTest bt) -> BatchTest.showSet(bt.getItem(item.testItemIndex).output());
  };

  private Map<Integer, Integer> _mapCols = new HashMap<>();;

  private UPMainFrame _parent;

  /* *************************************************************************
   * Button and Menu specifications
   * *************************************************************************/

  /* specs describing the buttons in the tool bar */
  protected RunnableAction[] actionSpecs() {
    Object [][] specs = {
        {"Reload", "edit-redo", "Reload Batch File", "Reload",
          new Runnable() { public void run() { reload(); } } },
        {"Rerun", "gnome-run", "Rerun Batch", "Rerun",
          new Runnable() { public void run() { rerun(); } } },
        {"Close", "window-close", "Close Batch Window", "Close",
          new Runnable() { public void run() { close(); } } },
    };
    RunnableAction[] results = new RunnableAction[specs.length];
    int i = 0;
    for(Object[] spec : specs){
      results[i++] = new RunnableAction((String) spec[0], (String) spec[1],
          (String) spec[2], (String) spec[3], null, (Runnable) spec[4]);
    }
    return results;
  }

  /* *************************************************************************
   * fields for GUI elements
   * *************************************************************************/

  /** displays error and status information */
  protected JLabel _statusLine;

  /** The table displaying the items */
  JTable _itemsDisplay;

  private static String[] columnNames = {
    " ", "Input LF", "Output LF", "Realized output", "Expected Output"
  };

  private void setColMap() {
    _mapCols.clear();
    int from = 0;
    for (int i = 0; i < columnNames.length; ++i) {
      if (_showColumn[i]) _mapCols.put(from++, i);
    }
  }


  //private static String[] names = { "Input LF", "Expected Output" };

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
      assert(we.getWindow() == ItemsTableWindow.this);
    }
  }

  private class ShowColAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private int colNo;

    public ShowColAction(int col) {
      super(columnNames[col]);
      colNo = col;
    }

    public void actionPerformed(ActionEvent e) {
      _showColumn[colNo] = ((JCheckBox)e.getSource()).isSelected();
      setColMap();
      ((ItemsTableModel)ItemsTableWindow.this._itemsDisplay.getModel())
      .fireTableStructureChanged();
    }
  };

  /** A read-only model to display the test items in a JTable.
   *  Depending on the _bad field of {@link ItemsTableWindow}, this will
   *  either show all test items, or the failed tests.
   */
  private class ItemsTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    protected String colNames(int i) {
      return columnNames[_mapCols.get(i)];
    }

    private BatchTest _bt;

    /** create a new model with initial content */
    public ItemsTableModel(BatchTest bt) {
      setTest(bt);
      setColMap();
    }

    /** \todo i could try to use DagNode as class and see what happens */
    @Override
    public Class<?> getColumnClass(int colIndex) { return String.class; }

    @Override
    public String getColumnName(int colIndex) {
      return columnNames[_mapCols.get(colIndex)];
    }

    /** put a new set of bindings into this model, changing its content */
    public void setTest(BatchTest bt) {
      _bt = bt;
      fireTableDataChanged();
    }

    public int getColumnCount() { return _mapCols.size(); }

    public int getRowCount() {
      int result = _showGood ? _bt.goodSize() : 0;
      if (_showBad) result += _bt.badSize();
      return result;
    }

    /** This is a read-only model */
    @Override
    public boolean isCellEditable(int row, int col) { return false; }

    /** You're not allowed to call this method */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      assert (false);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      ResultItem item = getItem(rowIndex);
      return _showFunction[_mapCols.get(columnIndex)].apply(item, _bt);
    }

    public ResultItem getItem(int rowIndex) {
      ResultItem item = null;
      if (_showBad) {
        if (rowIndex >= _bt.badSize()) {
          assert(_showGood);
          item = _bt.getGood(rowIndex - _bt.badSize());
        } else {
          item = _bt.getBad(rowIndex);
        }
      } else {
        assert(_showGood);
        item = _bt.getGood(rowIndex);
      }
      return item;
    }

    /** Indirection method that also registers model data change.
     *  @see BatchTest.run()
     */
    public void run() {
      _bt.runBatch();
      fireTableDataChanged();
    }

    /** Indirection method */
    public String percentageGood() {
      return _bt.percentageGood();
    }

    /** Indirection method that also registers model data change.
     *  @see BatchTest.run()
     */
    public void reload() throws IOException {
      _bt.reload();
      fireTableDataChanged();
    }

    /** Indirection method */
    public TestItem getTestItem(int testItemIndex) {
      return _bt.getItem(testItemIndex);
    }
  }

  /* **********************************************************************
   *  Communicate things to the user
   * ********************************************************************** */

  /** Clear the status line (a line at the bottom of the window for status
   *  messages).
   */
  public void clearStatusLine() {
    setStatusLine(" ");
  }

  /** Put the given message into the status line with black (default) text color
   */
  public void setStatusLine(String msg) {
    setStatusLine(msg, Color.BLACK);
  }

  /** Put the given message into the status line with the text color given by
   *  col
   */
  public void setStatusLine(String msg, Color col) {
    _statusLine.setForeground(col);
    _statusLine.setText(msg);
  }

  private void rerun() {
    _model.run();
    setStatusLine(_model.percentageGood());
  }

  private void reload() {
    try {
      _model.reload();
    } catch (IOException e) {
      setStatusLine(e.getMessage(), Color.RED);
    }
  }

  /** transfer the data in the selected row to the main panel
   *
   *  This is the dedicated mouse click handler for the batch display.
   */
  class SharedListSelectionHandler implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel)e.getSource();
      // single selection: only one row selected
      TestItem testItem = null;
      int row = lsm.getMinSelectionIndex();
      if (row < 0)
        return;

      row = _itemsDisplay.convertRowIndexToModel(row);
      ResultItem item = _model.getItem(row);
      testItem = _model.getTestItem(item.testItemIndex);
      parent().showPosition(testItem.position);
      parent().setOutput(item.outputLf);
      if (testItem instanceof RealizationTestItem) {
        parent().setInput(((RealizationTestItem) testItem).lf);
      } else {
        parent().setInput(((ParsingTestItem) testItem).input);
      }
    }
  }

  public class FailBoldTableCellRenderer
  extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -742588800582533386L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
      Component c =
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);

      row = _itemsDisplay.convertRowIndexToModel(row);
      ResultItem item = _model.getItem(row);
      final Color[] colors = {
          new Color(0xB9, 0x4A, 0x48), new Color(0xF2, 0xDE, 0xDE),
          new Color(0xC0, 0x98, 0x53), new Color(0xFC, 0xF8, 0xE3),
          new Color(0x3A, 0x87, 0xAD), new Color(0xD9, 0xED, 0xF7),
          new Color(0x46, 0x88, 0x47), new Color(0xDF, 0xF0, 0xD8)
      };
      c.setForeground(colors[item.itemStatus.ordinal() * 2]);
      c.setBackground(colors[item.itemStatus.ordinal() * 2 + 1]);
      /*
      if ((value instanceof String) && (((String)value).startsWith("***"))) {
        Font f = c.getFont();
        f = f.deriveFont(f.getStyle() | Font.BOLD);
        c.setFont(f);
      }
      */

      /* Only for specific cell
      if (row == SPECIAL_ROW && column == SPECIAL_COULMN) {
        c.setFont(<special font>);
        // you may want to address isSelected here too
        c.setForeground(<special foreground color>);
        c.setBackground(<special background color>);
      }
      */
      return c;
    }
  }

  /** a method to close a frame from within the program */
  private void close() {
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  private UPMainFrame parent() {
    return //(UPMainFrame) getOwner();
        _parent;
  }

  /* **********************************************************************
   * Constructors
   * ********************************************************************** */

  public ItemsTableWindow(UPMainFrame parent, BatchTest bt,
    boolean bad, boolean good) {
    //super(parent, "Batch Test Items", false);
    super("Batch Test Items");
    _parent = parent;
    _showBad = bad; _showGood = good;
    initPanel(bt);
    setStatusLine(_model.percentageGood());
  }

  private void initPanel(BatchTest bt) {
    // create content panel and add it to the frame
    JPanel contentPane = new JPanel(new BorderLayout());
    // contentPane.setLayout(new BorderLayout());
    this.setContentPane(contentPane);

    // no need to store the action buttons in a field, they don't change state
    JToolBar toolBar = parent().newToolBar(actionSpecs(), "Batch Tools",
        new ArrayList<JButton>());
    contentPane.add(toolBar, BorderLayout.NORTH);

    // add check boxes to display only good or bad items
    JCheckBox showGood = new JCheckBox(new AbstractAction("good") {
      private static final long serialVersionUID = 1L;
      public void actionPerformed(ActionEvent e) {
        _showGood = ((JCheckBox)e.getSource()).isSelected();
        ((ItemsTableModel)ItemsTableWindow.this._itemsDisplay.getModel())
        .fireTableDataChanged();
      }
    });
    showGood.setSelected(_showGood);
    JCheckBox showBad = new JCheckBox(new AbstractAction("bad") {
      private static final long serialVersionUID = 1L;
      public void actionPerformed(ActionEvent e) {
        _showBad = ((JCheckBox)e.getSource()).isSelected();
        ((ItemsTableModel)ItemsTableWindow.this._itemsDisplay.getModel())
        .fireTableDataChanged();
      }
    });
    showBad.setSelected(_showBad);

    // add check boxes to display mapper/output/expected columns

    JCheckBox showCol[] = new JCheckBox[columnNames.length - 2];
    for (int col = 2; col < columnNames.length; ++col) {
      showCol[col - 2] = new JCheckBox(new ShowColAction(col));
      showCol[col - 2].setSelected(_showColumn[col]);
    }

    // add status line
    _statusLine = new JLabel();
    clearStatusLine();

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    _statusLine.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPane.add(_statusLine);
    buttonPane.add(Box.createHorizontalGlue());
    showGood.setAlignmentX(Component.RIGHT_ALIGNMENT);
    buttonPane.add(showGood);
    buttonPane.add(Box.createRigidArea(new Dimension(0, 0)));
    showGood.setAlignmentX(Component.RIGHT_ALIGNMENT);
    buttonPane.add(showBad);
    for (int col = 2; col < columnNames.length; ++col) {
      showGood = showCol[col - 2];
      buttonPane.add(Box.createRigidArea(new Dimension(0, 0)));
      showGood.setAlignmentX(Component.RIGHT_ALIGNMENT);
      buttonPane.add(showGood);
    }
    contentPane.add(buttonPane, BorderLayout.SOUTH);

    _model = new ItemsTableModel(bt);
    // to show the list of test items, or the test failures
    _itemsDisplay = new JTable(_model);
    // First column should not be too wide
    _itemsDisplay.getColumnModel().getColumn(0).setMaxWidth(15);

    TableRowSorter<TableModel> sorter =
      new TableRowSorter<TableModel>(_itemsDisplay.getModel());
    sorter.setSortable(1, false);
    //sorter.setSortable(2, false);
    _itemsDisplay.setRowSorter(sorter);

    _itemsDisplay.setDefaultRenderer(_itemsDisplay.getColumnClass(2),
        new FailBoldTableCellRenderer());

    // _itemsDisplay.addMouseListener(new ShowItemListener());
    JScrollPane toDisplay = new JScrollPane(_itemsDisplay);
    _itemsDisplay.setFillsViewportHeight(true);
    // _itemsDisplay.setCellSelectionEnabled(true);
    _itemsDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel listSelectionModel = _itemsDisplay.getSelectionModel();
    listSelectionModel.addListSelectionListener(
        new SharedListSelectionHandler());

    contentPane.add(_itemsDisplay.getTableHeader(), BorderLayout.PAGE_START);
    contentPane.add(toDisplay, BorderLayout.CENTER);

    // use native windowing system to position new frames
    this.setLocationByPlatform(true);
    this.setPreferredSize(new Dimension(800, 400));
    this.addWindowListener(new Terminator());
    // set handler for closing operations
    // CLOSE_ON_EXIT
    // this.addWindowListener(new Terminator());
    // display the frame
    this.pack();
    // displayPane.setDividerLocation(.33);

    this.setVisible(true);
  }

}
