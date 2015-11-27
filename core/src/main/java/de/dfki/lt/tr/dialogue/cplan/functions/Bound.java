package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class Bound implements Function {

  @Override
  public Object apply(List<DagNode> args) {
    return args.get(0) != null;
  }

  @Override
  public String name() {
    return "bound";
  }

  @Override
  public int arity() {
    return 1;
  }

  @Override
  public void register(UtterancePlanner planner) {}

}
