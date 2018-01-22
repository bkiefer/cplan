package de.dfki.lt.tr.dialogue.cplan.actions;

import de.dfki.lt.tr.dialogue.cplan.*;

public class Deletion extends Action {

  public Deletion(Environment env, VarDagNode lval, DagNode rval) {
    super(env, lval, rval);
  }

  @Override
  public boolean apply(DagEdge input, Bindings bindings) {
    DagEdge root = _lval.getLvalBinding(input, bindings);
    if (root == null) return false;
    root.getValue().removeEdge(_rval.getEdgeIterator().next().getFeature());
    return true;
  }

  public String toString() {
    return  _env.toString(_lval) + " ! <"
        + _env.getFeatureName(_rval.getEdgeIterator().next().getFeature()) +">";
  }
}

