package de.dfki.lt.tr.dialogue.cplan.matches;


import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;

/** A syntax tree node representing an atomic node */
public class Atom extends Match {
  String _value;

  public Atom(String value) {
    _value = value;
  }

  @Override
  public String toString() {
    return super.toString() + _value;
  }

  @Override
  protected boolean match(DagEdge input, Bindings bindings) {
    return DagNode.subsumesType(DagNode.getTypeId(_value),
        input.getValue().getType());
  }

  @Override
  public Match deepCopy() {
    Atom newAtom = new Atom(_value);
    return copy(newAtom);
  }

}
