package de.dfki.lt.tr.dialogue.cplan.actions;

import org.apache.log4j.Logger;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UnificationException;
import de.dfki.lt.tr.dialogue.cplan.VarDagNode;

public class Assignment extends Action {

  private static final Logger logger = Logger.getLogger("UtterancePlanner");

  public Assignment(VarDagNode lval, DagNode dagNode) {
    super(lval, dagNode);
  }

  @Override
  public boolean apply(DagEdge current, Bindings bindings) {
    DagEdge root = _lval.getLvalBinding(current, bindings);
    if (root == null) return false;
    // cloning the rule is necessary because the variable nodes
    // have to be deleted destructively
    DagNode toAssign = _rval.cloneFS();
    try {
      toAssign = toAssign.expandVars(bindings);
    }
    catch (UnificationException uex) {
      Logger.getLogger("UtterancePlanner").error(uex);
      return false;
    }
    if (root.getFeature() == DagNode.PROP_FEAT_ID) {
      DagNode prop = toAssign.getValue(DagNode.PROP_FEAT_ID);
      if (prop == null) {
        logger.warn("Trying to set proposition to non-atomic value, ignoring");
      } else {
        root.setValue(prop);
      }
    } else if (root.getFeature() == DagNode.TYPE_FEAT_ID) {
      DagNode type = toAssign.getValue(DagNode.TYPE_FEAT_ID);
      if (type == null) {
        type = toAssign.getValue(DagNode.PROP_FEAT_ID);
      }
      if (type == null) {
        logger.warn("Trying to set type to non-atomic value, ignoring");
      } else {
        root.setValue(type);
      }
    } else {
      root.setValue(toAssign);
    }
    return true;
  }

  @Override
  public String toString() {
    return toString(" = ");
  }

}
