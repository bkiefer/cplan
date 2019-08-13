package de.dfki.lt.tr.dialogue.cplan.io;

import java.util.Iterator;

import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.SpecialDagNode;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;

public class LFDagPrettyPrinter extends DagPrinter {

  private boolean _ruleMode = false;

  private int width = 80;

  private int indentation = 2;

  private Layouter<NoExceptions> pp;

  private boolean root;

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

  public void tsr(DagNode here) {
    if (here == null) return;

    here = here.dereference();

    // subclasses will print themselves
    if (here instanceof SpecialDagNode) {
      StringBuilder sb = new StringBuilder();
      ((SpecialDagNode)here).toStringRec(this);
      pp.beginI(); pp.print(sb.toString()); pp.end();
      return;
    }

    // arrange for _isNominal nodes without id that the nominal name is
    // derived from the coref no instead.

    pp.beginCInd();
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
        //pp.add(idName).append(':');
        // pp.add('('); // should not be required
        if (id == null) {
          pp.print(newNominalName(-corefNo));
        } else {
          tsr(id);
        }
        pp.print(":");
        if (type != null) {
          tsr(type);
        }
        // pp.print(')'); // see above
        pp.end();
        return;
      }

      boolean printCaret = false;
      if (root && ! _ruleMode) {
        pp.print("@");
      } else {
        pp.print("(");
      }
      if (id == null) {
        pp.print(newNominalName(corefNo));
      } else {
        tsr(id);
      }
      pp.print(":");
      if (type != null) {
        tsr(type);
      }
      if (root && ! _ruleMode) {
        pp.print("(");
      } else {
        printCaret = true;
      }
      root = false;

      if (prop != null) {
        if (printCaret) {
          pp.brk(); pp.print("^ ");
        }
        else
          printCaret = true;
        tsr(prop);
      }

      //pp.print(readable ? here.getTypeName() : here.getType());
      while(edge != null) {
        pp.beginCInd(0);
        if (printCaret) {
          pp.brk(); pp.print("^ ");
        } else {
          pp.brk(0); // comment this if you want the first term after the paren
          printCaret = true;
        }
        pp.print("<" + (readable ? edge.getName() : edge.getFeature()) + ">");
        edge.getValue().toStringRec(this);
        if (it.hasNext()) {
          edge = it.next();
        } else {
          edge = null;
        }
        pp.end();
      }
      pp.print(")");
    } else {
      // a "feature" feature: exactly one edge: prop
      DagNode.EdgeIterator it = here.getTransitionalEdgeIterator();
      if (it.hasNext()) {
        DagEdge edge = it.next();
        assert(edge.getFeature() == DagNode.PROP_FEAT_ID);
        assert(! it.hasNext());
        DagNode sub = edge.getValue();
        if (sub != null) {
          tsr(sub);
        }
      }
      else {
        String name = here.getTypeName();
        pp.print(readable ? LFDagPrinter.getTypeString(name) : name);
      }
    }
    pp.end();
  }

  public DagPrinter append(String s) {
    pp.print(s);
    return this;
  }

  public DagPrinter append(char c) {
    pp.print("" + c);
    return this;
  }

  @Override
  public void toStringRec(DagNode dag, StringBuilder sb) {
    pp = Layouter.getStringLayouter(sb, width, indentation);
    root = true;
    tsr(dag);
    pp.close();
  }

  public void setWidth(int w) {
    width = w;
  }

  public void setIndentation(int i) {
    indentation = i;
  }
}
