package de.dfki.lt.tr.dialogue.cplan.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.*;

public class Assignment extends Action {

  private static final Logger logger = LoggerFactory.getLogger("UtterancePlanner");

  public Assignment(Environment env, VarDagNode lval, DagNode dagNode) {
    super(env, lval, dagNode);
  }

  @Override
  public boolean apply(DagEdge current, Bindings bindings) {
    DagEdge root = _lval.getLvalBinding(current, bindings);
    if (root == null) return false;
    // cloning the rule is necessary because the variable nodes
    // have to be deleted destructively
    DagNode toAssign = _rval.cloneFS();
    try {
      toAssign = toAssign.expandVars(_env, bindings);
    }
    catch (UnificationException uex) {
      logger.error("{}", uex);
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
