package de.dfki.lt.tr.dialogue.cplan.io;

import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.Environment;
import de.dfki.lt.tr.dialogue.cplan.SpecialDagNode;

public class LFDebugPrinter extends DagPrinter {

  private StringBuilder specialEdgeString(Environment env, DagEdge edge,
      StringBuilder sb) {
    DagNode val = edge.getValue().dereference();
    if (val instanceof SpecialDagNode) {
      ((SpecialDagNode)val).toStringSpecial(env, sb);
    } else {
      sb.append(env.getTypeName(val));
    }
    return sb;
  }

  @Override
  public void toStringRec(Environment env, DagNode hereNode, boolean readable,
      StringBuilder sb) {
    if (hereNode == null) {
      sb.append("(null)");
      return;
    }

    DagNode here = hereNode.dereference();
    int corefNo = getCorefNo(here);
    if (corefNo < 0) { // already printed, only coref
      sb.append(" #").append(-corefNo).append(' ');
      return;
    }
    if (corefNo > 0) {
      sb.append(" #").append(corefNo).append(' ');
    }

    sb.append('[');
    // sb.append(readable ? here.getTypeName() : here.getType());
    DagNode.EdgeIterator fvListIt = here.getTransitionalEdgeIterator();
    if (fvListIt != null && fvListIt.hasNext()) {
      while(fvListIt.hasNext()) {
        DagEdge edge = fvListIt.next();
        short feature = edge.getFeature();
        sb.append(' ');
        if (feature == env.ID_FEAT_ID) {
          specialEdgeString(env, edge, sb).append(':');
        } else if (feature == env.TYPE_FEAT_ID) {
          sb.append(':'); specialEdgeString(env, edge, sb);
        } else if (feature == env.PROP_FEAT_ID) {
          specialEdgeString(env, edge, sb);
        } else {
          sb.append(readable ? env.getName(edge) : edge.getFeature());
          toStringRec(env, edge.getValue(), readable, sb);
        }
      }
    }
    else {
      sb.append('@').append(env.getTypeName(here)).append('@');
    }
    sb.append(']');
  }
}
