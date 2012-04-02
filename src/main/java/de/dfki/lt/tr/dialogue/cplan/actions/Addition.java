package de.dfki.lt.tr.dialogue.cplan.actions;

import org.apache.log4j.Logger;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UnificationException;
import de.dfki.lt.tr.dialogue.cplan.VarDagNode;

public class Addition extends Action {

  public Addition(VarDagNode lval, DagNode dagNode) {
    super(lval, dagNode);
  }

  @Override
  public boolean apply(DagEdge input, Bindings bindings) {
    DagEdge root = _lval.getLvalBinding(input, bindings);
    if (root == null) return false;
    // cloning the rule is necessary because the variable nodes
    // have to be deleted destructively
    DagNode toAdd = _rval.cloneFS();
    try {
      toAdd = toAdd.expandVars(bindings);
      root.getValue().add(toAdd);
    }
    catch (UnificationException uex) {
      Logger.getLogger("UtterancePlanner").error(uex);
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return toString(" ^ ");
  }

}
