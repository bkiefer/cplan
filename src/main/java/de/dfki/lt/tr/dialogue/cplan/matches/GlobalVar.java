package de.dfki.lt.tr.dialogue.cplan.matches;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;

/** A syntax tree node representing the occurrence of a global variable */
public class GlobalVar extends Match implements MatchLVal {
  private String _varName;

  public GlobalVar(String varName) {
    _varName = varName;
  }

  @Override
  public String toString() {
    return super.toString() + "##" + _varName + ":";
  }

  @Override
  public String toStringBare() {
    return super.toString() + "##" + _varName;
  }

  /** This constitutes a new global match ?? Is there a scenario where it would
   *  be meaningful to check the contents of a global var against the
   *  current (maybe embedded) node AND the rest of the match expression,
   *  instead of testing only the global var against the rest? Why not?
   *  To distinguish, could we use the assignment syntax? Or a custom
   *  globalMatch function?
   */
  @Override
  protected boolean match(DagEdge input, Bindings bindings) {
    DagEdge bound = bindings.getBinding(_varName, Bindings.GLOBAL);
    if (bound == null) {
      return false;
    }
    return bound.getValue().dereference().equals(
        input.getValue().dereference());
  }

  /** Return the binding associated with this global variable, if there is any
   */
  @Override
  public DagEdge getBinding(DagEdge input, Bindings bindings) {
    return bindings.getBinding(_varName, Bindings.GLOBAL);
  }

  @Override
  public Match deepCopy() {
    GlobalVar newMatch = new GlobalVar(_varName);
    return copy(newMatch);
  }
}
