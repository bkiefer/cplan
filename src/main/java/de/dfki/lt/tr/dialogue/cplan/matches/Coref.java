package de.dfki.lt.tr.dialogue.cplan.matches;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;

public class Coref extends Match {

  private MatchLVal _sub;

  /** A coreference check against m, which must be a global or local variable,
   *  and thus a MatchLval
   */
  public Coref(Match m) {
    assert(m instanceof MatchLVal);
    _sub = (MatchLVal) m;
  }

  @Override
  public String toString() {
    return super.toString() + "= " + _sub;
  }

  /** return true if the value of input and the value of the bound edge are
   *  the same dag node (modulo dereferencing)
   */
  @Override
  protected boolean match(DagEdge input, Bindings bindings) {
    DagEdge bound = _sub.getBinding(input, bindings);
    if (bound == null) {
      return false;
    }
    return bound.getValue().dereference() == input.getValue().dereference();
  }

  @Override
  public Match deepCopy() {
    Coref newMatch = new Coref(((Match)_sub).deepCopy());
    return copy(newMatch);
  }

}
