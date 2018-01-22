package de.dfki.lt.tr.dialogue.cplan.actions;

import de.dfki.lt.tr.dialogue.cplan.*;

public abstract class  Action {

  protected VarDagNode _lval;
  protected DagNode _rval;

  protected Environment _env;

  protected Action(Environment env, VarDagNode lval, DagNode rval) {
    _lval = lval;
    _rval = rval;
    _env = env;
  }

  public abstract boolean apply(DagEdge current, Bindings bindings);

  protected String toString(String operator) {
    return _env.toString(_lval) + operator + _env.toString(_rval);
  }
}
