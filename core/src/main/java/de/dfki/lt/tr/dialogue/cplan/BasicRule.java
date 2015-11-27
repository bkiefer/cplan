package de.dfki.lt.tr.dialogue.cplan;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.dfki.lt.tr.dialogue.cplan.actions.Action;
import de.dfki.lt.tr.dialogue.cplan.matches.Match;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public class BasicRule implements Rule {

  private static boolean WARN_APPLY_FAILURE = true;

  private static Logger logger = Logger.getLogger("UtterancePlanner");

  /** The left hand side of the rule */
  //private List<VarMatch> _matches;
  private Match _match;

  /** The right hand side of the rule */
  private List<Action> _replace;

  /** Where was this rule defined */
  private Position _position;

  /** Should this rule be applied only once to a specific node */
  private boolean _oneShot;

  /** A (unique) numeric id for this rule */
  int _id;

  /** Only for use in TracedRule */
  protected BasicRule() { }

  /** Create a new rule: bind the left and right hand side */
  public BasicRule(Match match, List<Action> right, RuleParser.Location pos,
      boolean oneShot) {
    _match = match;
    _replace = right;
    _position = new Position(pos.begin.line, 0, pos.begin.msg);
    _oneShot = oneShot;
  }

  public static StringBuilder appendMatches(Match m, StringBuilder sb) {
    sb.append(m);
    return sb;
  }

  public StringBuilder appendMatches(StringBuilder sb) {
    return appendMatches(_match, sb);
  }

  public static StringBuilder appendActions(Iterable<Action> rep,
      StringBuilder sb) {
    Iterator<Action> it = rep.iterator();
    sb.append(it.next());
    while (it.hasNext()) {
      sb.append(", ").append(it.next());
    }
    return sb;
  }

  public StringBuilder appendActions(StringBuilder sb) {
    return appendActions(_replace, sb);
  }

  public Position getPosition() {
    return _position;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    return
    appendActions(appendMatches(sb)
        .append(_oneShot ? " -> " : " => ")).append(" .").toString();
  }

  public boolean matches(DagEdge root, DagEdge here, Bindings bindings) {
    bindings.resetLocalBindings();
    boolean result = true;
    result = _match.startMatch(here, bindings);
    return result;
  }

  public boolean executeActions(DagEdge root, DagEdge curr, Bindings bindings) {
    for (Action action : _replace) {
      if (! action.apply(curr, bindings)) {
        DagNode.invalidate();
        if (WARN_APPLY_FAILURE) {
          logger.warn("Unification failure in application phase of " + this
              + " to " + curr);
        }
        assert(false);
        return false;
      }
    }
    return true;
  }

  /** This function should only be used to simulate application of single rules
   *  in test cases. In the overall system, multiple rules are applied in
   *  a sequence before copying the result, using applyLocally for a single
   *  application.
   */
  DagNode applyLocallyAndCopy(DagNode currentNode) {
    DagEdge current = new DagEdge((short) -1, currentNode);
    Bindings bindings = new Bindings();
    DagNode result = current.getValue();
    if (matches(null, current, bindings)) {
      // the special variable "#" should now be bound to current
      assert(bindings.getBinding("#", Bindings.LOCAL) == current);
      executeActions(null, current, bindings);
      result = current.getValue().copyAndInvalidate();
    }
    return result;
  }

  /** A function to test the matching part of the rule, only for test
   *  purposes.
   */
  boolean match(DagNode lf) {
    return matches(null, new DagEdge((short)-1, lf), new Bindings());
  }

  public boolean oneShot() { return _oneShot; }

  public int id() { return _id; }

  public void setId(int id) { _id = id; }
}
