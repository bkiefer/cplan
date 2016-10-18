package de.dfki.lt.tr.dialogue.cplan.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UnificationException;
import de.dfki.lt.tr.dialogue.cplan.VarDagNode;

public class Addition extends Action {

  private static final Logger logger = LoggerFactory.getLogger("UtterancePlanner");

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
      if (root.getFeature() == DagNode.PROP_FEAT_ID) {
        DagNode prop = toAdd.getValue(DagNode.PROP_FEAT_ID);
        if (prop == null) {
          logger.warn("Trying to set proposition to non-atomic value, ignoring");
        } else {
          // TODO should rather be type unification
          root.setValue(prop);
        }
      } else if (root.getFeature() == DagNode.TYPE_FEAT_ID) {
        DagNode type = toAdd.getValue(DagNode.TYPE_FEAT_ID);
        if (type == null) {
          type = toAdd.getValue(DagNode.PROP_FEAT_ID);
        }
        if (type == null) {
          logger.warn("Trying to set type to non-atomic value, ignoring");
        } else {
          // TODO should rather be type unification
          root.setValue(type);
        }
      } else {
        root.getValue().add(toAdd);
      }
    }
    catch (UnificationException uex) {
      logger.error("{}", uex);
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return toString(" ^ ");
  }

}
