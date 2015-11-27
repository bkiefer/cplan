package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class Concatenate implements Function{

  @Override
  public Object apply(List<DagNode> args) {
    StringBuilder sb = new StringBuilder();
    for (DagNode arg : args) {
      sb.append(arg.toString(false));
    }
    return sb.toString();
  }

  @Override
  public int arity() { return Integer.MIN_VALUE; }

  @Override
  public String name() { return "concatenate"; }

  @Override
  public void register(UtterancePlanner planner) {}

}
