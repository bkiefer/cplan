package de.dfki.lt.tr.dialogue.cplan.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.dfki.lt.tr.dialogue.cplan.BatchTest;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.BadItem;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.TestItem;

public class ItemsTableWindow extends JDialog {
  private static final long serialVersionUID = 1L;

  private BatchTest _bt;

  private boolean _bad;

  /* *************************************************************************
   * Button and Menu specifications
   * *************************************************************************/

  /* specs describing the buttons in the tool bar */
  protected Object[][] actionSpecs() {
    Object [][] results = {
        {"Rerun", "view-refresh", "Rerun Batch", "Rerun",
          new Runnable() { public void run() { rerun(); } } },
        {"Reload", "edit-redo", "Relaod Batch File", "Reload",
          new Runnable() { public void run() { reload(); } } },
        {"Close", "window-close", "Close Batch Window", "Close",
          new Runnable() { public void run() { close(); } } },
    };
    return results;
  }

  /* *************************************************************************
   * fields for GUI elements
   * *************************************************************************/

  /** displays error and status information */
  protected JLabel _statusLine;

  private static String[] namesBad = {
    "Input LF", "Output LF", "Realized output", "Expected Output"
  };

  private static String[] names = { "Input LF", "Expected Output" };

  /** A read-only model to display the test items in a JTable.
   *  Depending on the _bad field of {@link ItemsTableWindow}, this will
   *  either show all test items, or the failed tests.
   */
  private class ItemsTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    protected String[] colNames() { return _bad ? namesBad : names; }

    /** create a new model with initial content */
    public ItemsTableModel(BatchTest bt) { setTest(bt); }

    /** \todo i could try to use DagNode as class and see what happens */
    @Override
    public Class<?> getColumnClass(int colIndex) { return String.class; }

    @Override
    public String getColumnName(int colIndex) { return colNames()[colIndex]; }

    /** put a new set of bindings into this model, changing its content */
    public void setTest(BatchTest bt) {
      _bt = bt;
      this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() { return colNames().length; }

    @Override
    public int getRowCount() {
      return _bad ? _bt.badSize() : _bt.itemSize();
    }

    /** This is a read-only model */
    @Override
    public boolean isCellEditable(int row, int col) { return false; }

    /** You're not allowed to call this method */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      assert (false);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (_bad) {
        BadItem bad = _bt.getBad(rowIndex);
        switch (columnIndex) {
        case 0: return _bt.getItem(bad.testItemIndex).lf;
        case 1: return bad.outputLf;
        case 2: return bad.realized;
        case 3: return showSet(_bt.getItem(bad.testItemIndex).answers);
        }
      } else {
        switch (columnIndex) {
        case 0: return _bt.getItem(rowIndex).lf;
        case 1: return showSet(_bt.getItem(rowIndex).answers);
        }
      }
      assert(false); return null;
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

  private static String showSet(Set<String> strings) {
    if (strings.isEmpty())
      return "[]";
    Iterator<String> it = strings.iterator();
    StringBuilder sb = new StringBuilder();
    sb.append("[ ").append(it.next());
    while (it.hasNext()) {
      sb.append(" | ").append(it.next());
    }
    sb.append(" ]");
    return sb.toString();
  }


  private void rerun() {
    _bt.run();
    setStatusLine(_bt.percentageGood());
  }

  private void reload() {
    try {
      _bt.reload();
    } catch (IOException e) {
      setStatusLine(e.getMessage(), Color.RED);
    }
  }


  class SharedListSelectionHandler implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel)e.getSource();
      // single selection: only one row selected
      int row = lsm.getMinSelectionIndex();
      TestItem testItem = null;
      if (_bad) {
        testItem = _bt.getItem(_bt.getBad(row).testItemIndex);
        parent().setOutput(_bt.getBad(row).outputLf);
      } else {
        testItem = _bt.getItem(row);
      }
      parent().setInput(testItem.lf);
      parent().showPosition(testItem.position);
    }
  }

  /** a method to close a frame from within the program */
  private void close() {
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  private UPMainFrame parent() {
    return (UPMainFrame) getOwner();
  }

  /* **********************************************************************
   * Constructors
   * ********************************************************************** */

  public ItemsTableWindow(UPMainFrame parent, BatchTest bt, boolean bad) {
    super(parent, "Failed Test Items", false);
    _bt = bt;
    _bad = bad;
    initPanel();
    setStatusLine(_bt.percentageGood());
  }

  private void initPanel() {
    // create content panel and add it to the frame
    JPanel contentPane = new JPanel(new BorderLayout());
    // contentPane.setLayout(new BorderLayout());
    this.setContentPane(contentPane);

    // no need to store the action buttons in a field, they don't change state
    JToolBar toolBar = parent().newToolBar(actionSpecs(), "Batch Tools",
        new ArrayList<JButton>());
    contentPane.add(toolBar, BorderLayout.NORTH);

    // add status line
    _statusLine = new JLabel();
    contentPane.add(_statusLine, BorderLayout.SOUTH);
    clearStatusLine();

    // to show the list of test items, or the test failures
    JTable itemsDisplay = new JTable(new ItemsTableModel(_bt));
    // _itemsDisplay.addMouseListener(new ShowItemListener());
    JScrollPane toDisplay = new JScrollPane(itemsDisplay);
    itemsDisplay.setFillsViewportHeight(true);
    // _itemsDisplay.setCellSelectionEnabled(true);
    itemsDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel listSelectionModel = itemsDisplay.getSelectionModel();
    listSelectionModel.addListSelectionListener(
        new SharedListSelectionHandler());

    contentPane.add(itemsDisplay.getTableHeader(), BorderLayout.PAGE_START);
    contentPane.add(toDisplay, BorderLayout.CENTER);

    // use native windowing system to position new frames
    this.setLocationByPlatform(true);
    this.setPreferredSize(new Dimension(800, 400));
    // set handler for closing operations
    // CLOSE_ON_EXIT
    // this.addWindowListener(new Terminator());
    // display the frame
    this.pack();
    // displayPane.setDividerLocation(.33);

    this.setVisible(true);
  }

}
