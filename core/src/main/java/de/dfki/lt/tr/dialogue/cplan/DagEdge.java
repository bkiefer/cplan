package de.dfki.lt.tr.dialogue.cplan;

import java.util.IdentityHashMap;
import java.util.Iterator;

public class DagEdge {

  short _feature;
  DagNode _value;

  public DagEdge(short feature, DagNode value) {
    _feature = feature;
    _value = value;
  }

  public short getFeature() {
    return _feature;
  }

  public DagNode getValue() {
    return _value;
  }

  public String getName() {
    return DagNode.getFeatureName(_feature);
  }

  @Override
  public String toString() {
    return '<' + DagNode.getFeatureName(_feature) + '>' + _value;
  }
  
  public DagEdge evaluate(Bindings bindings) {
    return this;
  }
  
  /** Get the last edge of the given path.
   *  Works correctly only on non-temporary dags.
   *  
   *  TODO: THINK ABOUT A WAY TO ALLOW VARIABLES IN THE PATH 
   *  unfortunately this potentially creates quite a big disjunction, which is
   *  why it's currently forbidden
   */
  public DagEdge walkOrCreatePath(Path path) {
    DagEdge current = this;
    for (short feature : path) {
      DagEdge next = current.getValue().getEdge(feature);
      if (next == null) {
        DagNode value = current.getValue();
        value.setNominal();
        next = current.getValue().addEdge(feature, new DagNode());
      }
      current = next;
    }
    return current;
  }

  /** Get the last edge of the given path.
   *  Works correctly only on non-temporary dags.
   */
  public DagEdge walkPath(Path path) {
    DagEdge current = this;
    for (short feature : path) {
      DagEdge next = current._value.getEdge(feature);
      if (next == null) {
        return null;
      }
      current = next;
    }
    return current;
  }

  /** return all edges that emerge from the target node of this edge */
  public Iterator<DagEdge> getEdges(short feature) {
    return _value.getEdges(feature);
  }

  /** Set the target node of this edge to \a newTarget */
  public void setValue(DagNode newTarget) {
    _value = newTarget;
  }
  /** Almost identical to copyResultRec, but does not use the
   *  generation-protected slots.
   */

  public DagEdge copyIntermediate(IdentityHashMap<Object, Object> origToCopy) {
    DagEdge edgeCopy = (DagEdge) origToCopy.get(this);
    if (edgeCopy == null) {
      edgeCopy = copySafely(_value.copyIntermediate(origToCopy));
      origToCopy.put(this, edgeCopy);
    }
    return edgeCopy;
  }

  public DagEdge copySafely(DagNode newValue) {
    return new DagEdge(_feature, newValue);
  }
}
