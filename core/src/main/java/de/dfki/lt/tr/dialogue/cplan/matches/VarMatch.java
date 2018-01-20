package de.dfki.lt.tr.dialogue.cplan.matches;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.Environment;

/** This class represents matches on the right hand side that do apply to
 *  the current node, or to some global variable or function call.
 *
 *  If _lval is null, the match is performed against the current node.
 *  Otherwise, the _lval represents the variable or function call whose value
 *  will be matched against.
 */
public class VarMatch extends Match {
  private MatchLVal _lval;
  private Match _match;

  public VarMatch(Environment env, MatchLVal lval, Match match) {
    _env = env;
    _lval = lval;
    _match = match;
  }

  public DagEdge getLVal(Bindings bindings) {
    DagEdge current = null;
    current = _lval.getBinding(null, bindings);
    if (current == null && _lval instanceof GlobalVar) {
      // simulate matching against an unbound variable by matching against an
      // empty node
      current = new DagEdge((short)-1, new DagNode(_env));
    }
    return current;
  }

  @Override
  protected boolean match(DagEdge here, Bindings bindings) {
    if (_lval == null) {
      if (_match.startMatch(here, bindings)) {
        // bind the current location locally to "#"
        bindings.bind("#", here, Bindings.LOCAL);
        return true;
      }
      return false;
    }
    return _match.startMatch(getLVal(bindings), bindings);
  }

  @Override
  public String toString() {
    if (_lval == null) {
      return _match.toString();
    }
    return "(" + _lval.toStringBare() + " ~ " + _match + ")";
  }

  @Override
  public Match deepCopy() {
    VarMatch newMatch =
      new VarMatch(_env, (MatchLVal) ((Match)_lval).deepCopy(), _match.deepCopy());
    return copy(newMatch);
  }
}
