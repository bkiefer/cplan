package de.dfki.lt.tr.dialogue.cplan.util;

import java.util.ArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ListRangeModel<E> extends ArrayList<E>
implements BoundedRangeModel {
  private DefaultBoundedRangeModel _brm = new DefaultBoundedRangeModel();

  @Override
  public boolean add(E e) {
    boolean result = super.add(e);
    _brm.setMaximum(size() - 1);
    return result;
  }

  // TODO modify the other add/remove methods appropriately

  @Override
  public void addChangeListener(ChangeListener x) {
    _brm.addChangeListener(x);
  }

  @Override
  public int getExtent() {
    return _brm.getExtent();
  }

  @Override
  public int getMaximum() {
    return _brm.getMaximum();
  }

  @Override
  public int getMinimum() {
    return 0;
  }

  @Override
  public int getValue() {
    return _brm.getValue();
  }

  @Override
  public boolean getValueIsAdjusting() {
    return _brm.getValueIsAdjusting();
  }

  @Override
  public void removeChangeListener(ChangeListener x) {
    _brm.removeChangeListener(x);
  }

  @Override
  public void setExtent(int newExtent) {
    _brm.setExtent(newExtent);
  }

  @Override
  public void setMaximum(int newMaximum) {
  }

  @Override
  public void setMinimum(int newMinimum) {
  }

  @Override
  public void setRangeProperties(int value, int extent, int min, int max,
      boolean adjusting) {
    _brm.setRangeProperties(value, extent, 0, isEmpty() ? 0 : size() - 1,
        adjusting);
  }

  @Override
  public void setValue(int newValue) {
    _brm.setValue(newValue);
  }

  @Override
  public void setValueIsAdjusting(boolean b) {
    _brm.setValueIsAdjusting(b);
  }
}
