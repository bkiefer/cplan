package de.dfki.lt.tr.dialogue.cplan.io;

import java.util.regex.Pattern;

import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.SpecialDagNode;

public class LFDagPrinter extends DagPrinter {

  private boolean _ruleMode = false;

  private StringBuilder sb;

  private String newNominalName(int corefNo) {
    return "nom" + ((corefNo == 0) ? ++_maxCoref : Math.abs(corefNo));
  }

  public void setRuleMode(boolean mode) {
    _ruleMode = mode;
  }

  static final Pattern isId = Pattern.compile("[a-zA-Z][-_a-zA-Z0-9]*|[0-9]+");

  /** Surround a type name with double quotes if it contains double quotes or
   *  spaces and escape double quotes in the string correctly.
   */
  static String getTypeString(String typeName) {
    if (typeName.indexOf('"') >= 0) {
      typeName = "\"" + typeName.replaceAll("\"", "\\\\\"") + "\"";
    } else if (! isId.matcher(typeName).matches()) {
      typeName =  "\"" + typeName + "\"";
    }
    return typeName;
  }

  @Override
  public void toStringRec(DagNode here, StringBuilder s) {
    sb = s;
    tsr(here);
  }

  @Override
  public void tsr(DagNode here) {
    if (here == null) return;

    here = here.dereference();

    boolean root = (sb.length() == 0);

    // subclasses will print themselves
    if (here instanceof SpecialDagNode) {
      ((SpecialDagNode)here).toStringRec(this);
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
          toStringRec(id, sb);
        }
        sb.append(':');
        if (type != null) {
          toStringRec(type, sb);
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
        toStringRec(id, sb);
      }
      sb.append(':');
      if (type != null) {
        toStringRec(type, sb);
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
        toStringRec(prop, sb);
      }

      //sb.append(readable ? here.getTypeName() : here.getType());
      while(edge != null) {
        if (printCaret)
          sb.append(" ^ ");
        else
          printCaret = true;
        sb.append("<")
        .append(readable ? edge.getName() : edge.getFeature())
        .append(">");
        toStringRec(edge.getValue(), sb);
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
          toStringRec(sub, sb);
        }
      }
      else {
        String name = here.getTypeName();
        sb.append(readable ? getTypeString(name) : name);
      }
    }
  }

  public DagPrinter append(String s) {
    sb.append(s);
    return this;
  }

  public DagPrinter append(char c) {
    sb.append(c);
    return this;
  }
}
