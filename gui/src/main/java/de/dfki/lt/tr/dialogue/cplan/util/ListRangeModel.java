package de.dfki.lt.tr.dialogue.cplan.util;

import java.util.ArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ListRangeModel<E> extends ArrayList<E>
implements BoundedRangeModel {
  private DefaultBoundedRangeModel _brm;

  public ListRangeModel() {
    super();
    _brm = new DefaultBoundedRangeModel();
    setMinimum(0);
    setMaximum(0);
  }

  @Override
  public boolean add(E e) {
    boolean result = super.add(e);
    _brm.setMaximum(size() - 1);
    return result;
  }

  // TODO modify the other add/remove methods appropriately

  public void addChangeListener(ChangeListener x) {
    _brm.addChangeListener(x);
  }

  public int getExtent() {
    return _brm.getExtent();
  }

  public int getMaximum() {
    return _brm.getMaximum();
  }

  public int getMinimum() {
    return 0;
  }

  public int getValue() {
    return _brm.getValue();
  }

  public boolean getValueIsAdjusting() {
    return _brm.getValueIsAdjusting();
  }

  public void removeChangeListener(ChangeListener x) {
    _brm.removeChangeListener(x);
  }

  public void setExtent(int newExtent) {
    _brm.setExtent(newExtent);
  }

  public void setMaximum(int newMaximum) {
    _brm.setMaximum(newMaximum);
  }

  public void setMinimum(int newMinimum) {
    _brm.setMinimum(newMinimum);
  }

  public void setRangeProperties(int value, int extent, int min, int max,
      boolean adjusting) {
    _brm.setRangeProperties(value, extent, 0, isEmpty() ? 0 : size() - 1,
        adjusting);
  }

  public void setValue(int newValue) {
    _brm.setValue(newValue);
  }

  public void setValueIsAdjusting(boolean b) {
    _brm.setValueIsAdjusting(b);
  }
}
