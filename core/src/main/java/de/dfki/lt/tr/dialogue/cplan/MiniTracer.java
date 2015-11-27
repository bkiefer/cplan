package de.dfki.lt.tr.dialogue.cplan;

public class MiniTracer implements RuleTracer {

  private int _ruleTracing = 0;

  public MiniTracer(int bitmask) {
    setTracing(bitmask);
  }

  public void traceMatch(DagEdge root, DagEdge current, BasicRule r,
      Bindings bindings) {
    if ((_ruleTracing & MATCHING) != 0) {
      System.out.println(r.getPosition());
    }
  }

  public void traceBeforeApplication(DagEdge root, DagEdge current, BasicRule r,
      Bindings bindings){
  }

  public void traceAfterApplication(DagEdge root, DagEdge current, BasicRule r,
      Bindings bindings){
    if ((_ruleTracing & MODIFICATION) != 0) {
      System.out.println(r.getPosition());
    }
  }

  public void setTracing(int bitmask) {
    _ruleTracing = bitmask;
  }
}
