package de.dfki.lt.tr.dialogue.cplan;

public interface Processor {

  /** Turn on tracing for this processor, and use tracer to execute it */
  public abstract void setTracing(RuleTracer tracer);

  /** Return the tracer that is used, or null if tracing is turned off */
  public abstract RuleTracer getTracing();

  /** Apply the rules in this processor to the input lf, using bindings to
   *  expand variables,
   */
  public abstract DagNode applyRules(DagNode lf, Bindings bindings);

}
