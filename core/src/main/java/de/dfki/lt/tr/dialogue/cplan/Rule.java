package de.dfki.lt.tr.dialogue.cplan;

public interface Rule {
  public boolean matches(DagEdge root, DagEdge here, Bindings bindings) ;

  public boolean executeActions(DagEdge root, DagEdge curr, Bindings bindings);

  public boolean oneShot();

  public int id();
}
