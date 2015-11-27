package de.dfki.lt.tr.dialogue.cplan;

public interface RuleTracer {
  public static final int MATCHING = 1;
  public static final int MODIFICATION = MATCHING << 1;
  public static final int ALL = MATCHING | MODIFICATION;

  public abstract void traceMatch(DagEdge root, DagEdge current,
    BasicRule r, Bindings bindings);

  public abstract void traceBeforeApplication(DagEdge root, DagEdge current,
    BasicRule r, Bindings bindings);

  public abstract void traceAfterApplication(DagEdge root, DagEdge current,
    BasicRule r, Bindings bindings);

  public abstract void setTracing(int mode);

}