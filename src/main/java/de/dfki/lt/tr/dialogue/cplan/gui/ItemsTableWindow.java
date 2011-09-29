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

  private UPMainFrame _parent;

  /** show the list of test items, or the test failures */
  private JTable _itemsDisplay;

  private BatchTest _bt;

  private boolean _bad;

  /* *************************************************************************
   * Button and Menu specifications
   * *************************************************************************/

  /* specs describing the buttons in the tool bar */
  protected Object[][] actionSpecs() {
    Object [][] results = {
        {"Rerun", "view-refresh", "Rerun Batch", "Rerun",
          new Runnable() { public void run() { _bt.run(); } } },
        {"Reload", "edit-redo", "Relaod Batch File", "Reload",
            new Runnable() {
              public void run() {
                try {
                  _bt.reload();
                } catch (IOException e) {
                  _parent.setStatusLine(e.getMessage(), Color.RED);
                }
              }
            }
        },
        {"Close", "window-close", "Close Trace Window", "Close",
          new Runnable() { public void run() { close(); } }
        },
    };
    return results;
  }

  /* *************************************************************************
   * fields for GUI elements
   * *************************************************************************/

  /** Name of tool bar containing the action buttons */
  protected String _toolBarName = "Batch Testing";

  /** Action Buttons */
  protected ArrayList<JButton> _buttons = new ArrayList<JButton>();

  private static String[] namesBad = {
    "Input LF", "Output LF", "Realized output", "Expected Output"
  };

  private static String[] names = { "Input LF", "Expected Output" };

  /** A read-only model to display the test items in a JTable */
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

  class SharedListSelectionHandler implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel)e.getSource();
      // single selection: only one row selected
      int row = lsm.getMinSelectionIndex();
      TestItem testItem = null;
      if (_bad) {
        testItem = _bt.getItem(_bt.getBad(row).testItemIndex);
        _parent.setOutput(_bt.getBad(row).outputLf);
      } else {
        testItem = _bt.getItem(row);
      }
      _parent.setInput(testItem.lf);
      _parent.showPosition(testItem.position);
    }
  }

  /** a method to close a frame from within the program */
  private void close() {
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  /* **********************************************************************
   * Constructors
   * ********************************************************************** */

  public ItemsTableWindow(UPMainFrame parent, BatchTest bt, boolean bad) {
    super(parent, "Failed Test Items", false);
    _parent = parent;
    _bt = bt;
    _bad = bad;
    initPanel();
  }

  private void initPanel() {
    // create content panel and add it to the frame
    JPanel contentPane = new JPanel(new BorderLayout());
    // contentPane.setLayout(new BorderLayout());
    this.setContentPane(contentPane);

    JToolBar toolBar =
      _parent.newToolBar(actionSpecs(), _toolBarName, _buttons);
    if (toolBar != null) {
      contentPane.add(toolBar, BorderLayout.NORTH);
    }

    // create scrollable display areas
    _itemsDisplay = new JTable(new ItemsTableModel(_bt));
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
    this.setPreferredSize(new Dimension(800, 500));
    // set handler for closing operations
    // CLOSE_ON_EXIT
    // this.addWindowListener(new Terminator());
    // display the frame
    this.pack();
    // displayPane.setDividerLocation(.33);

    this.setVisible(true);
  }

}
