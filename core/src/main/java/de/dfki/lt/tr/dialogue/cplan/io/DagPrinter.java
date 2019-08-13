package de.dfki.lt.tr.dialogue.cplan.io;

import java.util.IdentityHashMap;

import de.dfki.lt.tr.dialogue.cplan.DagNode;

public abstract class DagPrinter {
  private IdentityHashMap<DagNode, Integer> corefs;
  protected int _maxCoref;

  protected boolean readable = true;

  public abstract void toStringRec(DagNode dag, StringBuilder sb) ;

  public abstract void tsr(DagNode dag); // internal recursion

  public abstract DagPrinter append(String s);

  public abstract DagPrinter append(char c);

  public void setReadable(boolean val) {
    readable = val;
  }

  public void getCorefs(DagNode root) {
    corefs = new IdentityHashMap<DagNode, Integer>();
    _maxCoref = root.countCorefsLocal(corefs, 0);
  }

  protected int getCorefNo(DagNode here) {
    int corefNo = corefs.get(here);
    if (corefNo > 0)
      corefs.put(here, -corefNo);
    return corefNo;
  }
}
