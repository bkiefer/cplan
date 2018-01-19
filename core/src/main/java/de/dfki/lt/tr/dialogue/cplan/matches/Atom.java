package de.dfki.lt.tr.dialogue.cplan.matches;


import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.Environment;

/** A syntax tree node representing an atomic node */
public class Atom extends Match {
  String _value;

  public Atom(Environment env, String value) {
    _env = env;
    _value = value;
  }

  @Override
  public String toString() {
    return super.toString() + _value;
  }

  @Override
  protected boolean match(DagEdge input, Bindings bindings) {
    DagNode val = input.getValue();
    return _env.subsumesType(_env.getTypeId(_value),
        val.getType());
  }

  @Override
  public Match deepCopy() {
    Atom newAtom = new Atom(_env, _value);
    return copy(newAtom);
  }

}
