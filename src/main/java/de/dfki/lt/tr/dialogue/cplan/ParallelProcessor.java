package de.dfki.lt.tr.dialogue.cplan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class ParallelProcessor implements Processor {

  private List<Rule> _rules;

  /** An object to trace matching / application of rules */
  private RuleTracer _tracer;

  /** Constructor: initialize the processor with a list of rules */
  public ParallelProcessor(List<Rule> basicRules) {
    _rules = basicRules;
  }

  @Override
  public void setTracing(RuleTracer t) {
    List<Rule> rules = new ArrayList<Rule>(_rules.size());
    for (Rule r : _rules) {
      rules.add(TracedRule.traceRule(r, t));
    }
    _rules = rules;
    _tracer = t;
  }

  @Override
  public RuleTracer getTracing() {
    return _tracer;
  }

  public Collection<Rule> getRules() {
    return _rules;
  }

  /** A local class to store successful matches together with the local bindings
   *  that were established during the match for later application of rule
   *  actions.
   */
  private static class RuleAction {
    Rule _rule;
    DagEdge _applicationPoint;
    Bindings _bindings;

    public RuleAction(Rule r, Vector<DagEdge> path, Bindings bindings) {
      _rule = r;
      _applicationPoint = path.lastElement();
      _bindings = bindings.transferLocalBindings();
    }

    public void apply(DagEdge root, Bindings bindings) {
      bindings.restoreLocalBindings(_bindings);
      _rule.executeActions(root, _applicationPoint, bindings);
    }

    @Override
    public String toString() {
      return _rule.toString() + "\n"
          + _applicationPoint + "\n" + _bindings + "\n";
    }
  }

  /** This method applies the rules in a pseudo-parallel way to all nodes in the
   *  graph.
   *
   *  The graph is traversed in depth-first postorder, means: the root first,
   *  then the first daughter, then this nodes first daughter, etc.
   *  The successful matches are stored, together with the local bindings that
   *  have been established during the match.
   */
  private List<RuleAction> computeMatches(DagEdge lfEdge, Bindings bindings) {
    List<RuleAction> result = new LinkedList<RuleAction>();
    IdentityHashMap<DagNode, DagNode> visited =
      new IdentityHashMap<DagNode, DagNode>();

    Stack<DagEdge> path = new Stack<DagEdge>();
    Stack<Iterator<DagEdge>> iterators = new Stack<Iterator<DagEdge>>();
    {
      List<DagEdge> root = new ArrayList<DagEdge>();
      root.add(lfEdge);
      iterators.push(root.iterator());
    }
    path.push(null);
    while (! iterators.isEmpty()) {
      DagEdge nextEdge = null;
      while (nextEdge == null && ! iterators.isEmpty()) {
        if (iterators.peek().hasNext()) {
          nextEdge = iterators.peek().next();
          path.pop();
          path.push(nextEdge);
        }
        else {
          path.pop();
          iterators.pop();
        }
      }
      if (nextEdge != null) {
        assert(nextEdge == path.lastElement());
        // don't match against nodes without edges
        DagNode applyNode = nextEdge.getValue().dereference();
        if (! (applyNode.edgesAreEmpty() || visited.containsKey(applyNode))) {
          // apply rules to the node under path in the modified structure
          visited.put(applyNode, applyNode);
          for (Rule rule : _rules) {
            if (rule.matches(lfEdge, nextEdge, bindings)) {
              result.add(new RuleAction(rule, path, bindings));
            }
          }
          iterators.push(applyNode.getEdgeIterator());
        }
        path.push(null);
      }
    }
    return result;
  }

  /** Match all rules pseudo-parallel to all nodes in the graph, then execute
   *  the actions in the order in which they were found. Rule matching is
   *  applied in the order in which the rules are loaded.
   */
  @Override
  public DagNode applyRules(DagNode lf, Bindings bindings) {
    DagEdge lfEdge = new DagEdge((short)-1, lf);
    List<RuleAction> actions = computeMatches(lfEdge, bindings);
    if (actions != null) {
      for (RuleAction action : actions) {
        action.apply(lfEdge, bindings);
      }
      return lfEdge.getValue();
    }
    else {
      return lf;
    }
  }
}
