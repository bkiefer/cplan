package de.dfki.lt.tr.dialogue.cplan.actions;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.VarDagNode;

public abstract class  Action {

  protected VarDagNode _lval;
  protected DagNode _rval;

  protected Action(VarDagNode lval, DagNode rval) {
    _lval = lval;
    _rval = rval;
  }

  public abstract boolean apply(DagEdge current, Bindings bindings);

  protected String toString(String operator) {
    return _lval.toString() + operator + _rval.toString();
  }
}
