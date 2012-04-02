package de.dfki.lt.tr.dialogue.cplan;


/** A decorator to be able to trace the matching and the application phase
 *  of rules with a tracer object.
 */
public class TracedRule implements Rule {
  private BasicRule _r;

  private RuleTracer _tracer;

  protected TracedRule(BasicRule r, RuleTracer t) {
    _r = r;
    _tracer = t;
  }

  /** Return a traced or untraced rule, depending on the value of t
   *  @param r the rule to be traced or untraced, must be a BasicRule if it's
   *           not a TracedRule
   *  @param t the RuleTracer to trace the rule. If null, the rule will be
   *           untraced if it was traced before.
   *  @return the (un)traced rule
   */
  public static Rule traceRule(Rule r, RuleTracer t) {
    if (r instanceof TracedRule) {
      if (t == null) { // untrace rule
        r = ((TracedRule) r)._r;
      } else {
        ((TracedRule) r)._tracer = t;
      }
    } else {
      if (t != null) {
        assert(r instanceof BasicRule);
        r = new TracedRule((BasicRule) r, t);
      }
    }
    return r;
  }

  @Override
  public boolean matches(DagEdge root, DagEdge here, Bindings bindings) {
    boolean result = _r.matches(root, here, bindings);
    if (result)
      _tracer.traceMatch(root, here, _r, bindings);
    return result;
  }

  @Override
  public boolean executeActions(DagEdge root, DagEdge curr, Bindings bindings) {
    _tracer.traceBeforeApplication(root, curr, _r, bindings);
    boolean result = _r.executeActions(root, curr, bindings);
    _tracer.traceAfterApplication(root, curr, _r, bindings);
    return result;
  }

  @Override
  public String toString() {
    return _r.toString();
  }
}
