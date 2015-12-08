package de.dfki.lt.tr.dialogue.cplan.matches;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;

/** A syntax tree node representing the occurence of a local variable */
public class LocalVar extends Match implements MatchLVal {
  private String _varName;

  public LocalVar(String varName) {
    _varName = varName;
  }

  @Override
  public String toString() {
    return super.toString() + "#" + _varName + ":";
  }

  @Override
  public String toStringBare() {
    return super.toString() + "#" + _varName;
  }

  /** A local var that matches a whole node rather than a prop or type value
   *  is NOT under the id feature, (otherwise it would not be possible to
   *  bind the id value (the nominal name), which the syntax does not allow
   *  anyway :)
   */
  @Override
  protected boolean match(DagEdge input, Bindings bindings) {
    DagEdge bound = bindings.getBinding(_varName, Bindings.LOCAL);
    if (bound == null) {
      bindings.bind(_varName, input, Bindings.LOCAL);
      return true;
    } else {
      // test equality of input vs. what is bound to the variable
      return bound.getValue().dereference().equals(
          input.getValue().dereference());
    }
    // return false lf.isUnifiable(input);
  }

  /** Return the binding associated with this local variable, if there is any
   */
  public DagEdge getBinding(DagEdge input, Bindings bindings) {
    if (_varName == null) return input;
    return bindings.getBinding(_varName, Bindings.LOCAL);
  }

  @Override
  public Match deepCopy() {
    LocalVar newMatch = new LocalVar(_varName);
    return copy(newMatch);
  }
}
