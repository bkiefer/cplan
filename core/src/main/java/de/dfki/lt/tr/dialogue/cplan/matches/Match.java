package de.dfki.lt.tr.dialogue.cplan.matches;


import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;

/** Abstract superclass of all syntax tree nodes for the parsing of graph
 *  transformation matching rules.
 */
public abstract class Match {
  protected boolean _negated = false;

  /** See if, given the variable bindings, all conditions specified in this
   *  subtree can be fulfilled on the given input node
   */
  protected abstract boolean match(DagEdge input, Bindings bindings);

  /** This method keeps track of the embedding of the matches which is important
   *  when a local failure occurs. In this case, all bindings that have been
   *  created in the meantime have to be retracted. This is the only method
   *  that is allowed to call match directly.
   */
  protected final boolean matches(DagEdge input, Bindings bindings) {
    int currentLevel = bindings.getLevel();
    boolean result = match(input, bindings);
    if (_negated) result = ! result;
    if (! result) {
      // in case of local failure, we have to retract the variable bindings
      // that were established in that subtree
      bindings.retractToLevel(currentLevel);
    }
    return result;
  }

  public boolean startMatch(DagEdge input, Bindings bindings) {
    // nothing matches against an unbound variable or a function returning null
    if (input == null) return false;
    boolean result = matches(input, bindings);
    // We have a global success, so make the remaining bindings permanent
    return result;
  }

  public void setNegated(boolean value) {
    _negated = value;
  }

  /** Convert this subtree into normal form. At the moment, this only converts
   *  trees of directly nested conjunctions into a leftist tree, i.e., a chain
   *  of conjunctions. This might be extended in the future to transform
   *  the tree into disjunctive normal form.
   */
  void normalForm() {}

  /** It is obligatory for implementation of toString in the subclasses of
   *  Match to first call this super method.
   */
  @Override
  public String toString() {
    return (_negated ? "!" : "");
  }

  /** Relevant for variables which have to be printed with `:' in almost all
   *  cases, except for when they are under TYPE or PROP
   */
  public String toStringBare() {
    return toString();
  }

  /** return a deep copy of this object */
  public abstract Match deepCopy();

  /** for the subclasses, to copy fields of Match properly */
  protected Match copy(Match m) {
    m._negated = _negated;
    return m;
  }

public DagEdge getBinding(DagEdge input, Bindings bindings) {
	// TODO Auto-generated method stub
	return null;
}

}

