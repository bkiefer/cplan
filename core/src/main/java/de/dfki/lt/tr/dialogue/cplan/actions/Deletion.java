package de.dfki.lt.tr.dialogue.cplan.actions;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.VarDagNode;

public class Deletion extends Action {

  public Deletion(VarDagNode lval, DagNode rval) {
    super(lval, rval);
  }

  @Override
  public boolean apply(DagEdge input, Bindings bindings) {
    DagEdge root = _lval.getLvalBinding(input, bindings);
    if (root == null) return false;
    root.getValue().removeEdge(_rval.getEdgeIterator().next().getFeature());
    return true;
  }

  @Override
  public String toString() {
    return  _lval.toString() + " ! <"
    + _rval._env.getFeatureName(_rval.getEdgeIterator().next().getFeature()) +">";
  }
}

