package de.dfki.lt.tr.dialogue.cplan.io;

import java.util.Iterator;
import java.util.regex.Pattern;

import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.SpecialDagNode;

public class LFDagPrettyPrinter extends DagPrinter {

  private boolean _ruleMode = false;

  public int MAX_WIDTH = 80;

  private static final String nl = System.getProperty("line.separator");

  private class Info {
    public int pos;
    public int indent;
  }

  private String newNominalName(int corefNo) {
    return "nom" + ((corefNo == 0) ? ++_maxCoref : Math.abs(corefNo));
  }

  public void setRuleMode(boolean mode) {
    _ruleMode = mode;
  }

  boolean isComplex(DagNode dag) {
    boolean propfirst = false;
    Iterator<DagEdge> it = dag.getEdgeIterator();
    if (it.hasNext()) {
      DagEdge e = it.next();
      if (e.getFeature() == DagNode.PROP_FEAT_ID) {
        propfirst = true;
      }
    }
    if (it.hasNext() || ! propfirst) return true;
    return false;
  }

  public void toStringRec(DagNode here, boolean readable, StringBuilder sb,
      Info max_width) {
    if (here == null) return;

    here = here.dereference();

    boolean root = (sb.length() == 0);

    // subclasses will print themselves
    if (here instanceof SpecialDagNode) {
      ((SpecialDagNode)here).toStringSpecial(sb);
      return;
    }

    // arrange for _isNominal nodes without id that the nominal name is
    // derived from the coref no instead.

    if (here.isNominal()) {
      DagNode prop = null ;
      DagNode id = null;
      DagNode type = null;

      DagNode.EdgeIterator it = here.getTransitionalEdgeIterator();
      DagEdge edge = null;
      while (it.hasNext()) {
        edge = it.next();
        short feature = edge.getFeature();
        if (feature == DagNode.ID_FEAT_ID) {
          id = edge.getValue().dereference(); edge = null;
        } else if (feature == DagNode.TYPE_FEAT_ID) {
          type = edge.getValue().dereference(); edge = null;
        } else if (feature == DagNode.PROP_FEAT_ID) {
          prop = edge.getValue().dereference(); edge = null;
        } else {
          break;
        }
      }

      int corefNo = getCorefNo(here);
      if (corefNo < 0) { // already printed, only id:type
        //String idName =
        //  ((id != null) ? id.getTypeName() : newNominalName(-corefNo));
        //sb.append(idName).append(':');
        // sb.append('('); // should not be required
        if (id == null) {
          sb.append(newNominalName(-corefNo));
        } else {
          toStringRec(id, readable, sb, max_width);
        }
        sb.append(':');
        if (type != null) {
          toStringRec(type, readable, sb, max_width);
        }
        // sb.append(')'); // see above
        return;
      }

      boolean printCaret = false;
      if (root && ! _ruleMode) {
        sb.append('@');
      } else {
        sb.append('(');
      }
      if (id == null) {
        sb.append(newNominalName(corefNo));
      } else {
        toStringRec(id, readable, sb, max_width);
      }
      sb.append(':');
      if (type != null) {
        toStringRec(type, readable, sb, max_width);
      }
      if (root && ! _ruleMode) {
        sb.append('(');
      } else {
        printCaret = true;
      }

      if (prop != null) {
        if (printCaret)
          sb.append(" ^ ");
        else
          printCaret = true;
        toStringRec(prop, readable, sb, max_width);
      }

      //sb.append(readable ? here.getTypeName() : here.getType());
      while(edge != null) {
        DagNode val = edge.getValue();
        if (isComplex(val)) {
          sb.append(nl);; //.append(indent);
        }
        if (printCaret)
          sb.append(" ^ ");
        else
          printCaret = true;
        sb.append("<")
        .append(readable ? edge.getName() : edge.getFeature())
        .append(">");
        toStringRec(val, readable, sb, max_width);
        if (it.hasNext()) {
          edge = it.next();
        } else {
          edge = null;
        }
      }
      sb.append(')');
    } else {
      // a "feature" feature: exactly one edge: prop
      DagNode.EdgeIterator it = here.getTransitionalEdgeIterator();
      if (it.hasNext()) {
        DagEdge edge = it.next();
        assert(edge.getFeature() == DagNode.PROP_FEAT_ID);
        assert(! it.hasNext());
        DagNode sub = edge.getValue();
        if (sub != null) {
          toStringRec(sub, readable, sb, max_width);
        }
      }
      else {
        String name = here.getTypeName();
        sb.append(readable ? LFDagPrinter.getTypeString(name) : name);
      }
    }
  }

  @Override
  public void toStringRec(DagNode dag, boolean readable, StringBuilder sb) {
    Info i = new Info(); i.indent = 0;
    toStringRec(dag, readable, sb, i);
  }
}
