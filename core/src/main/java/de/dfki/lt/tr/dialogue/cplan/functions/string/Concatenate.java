package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public class Concatenate extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    StringBuilder sb = new StringBuilder();
    for (DagNode arg : args) {
      sb.append(toString(arg));
    }
    return sb.toString();
  }

  public int arity() { return Integer.MIN_VALUE; }

  public String name() { return "concatenate"; }
}
