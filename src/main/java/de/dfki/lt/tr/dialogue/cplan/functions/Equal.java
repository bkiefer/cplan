package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class Equal implements Function {

  @Override
  public Object apply(List<DagNode> args) {
    return args.get(0).equals(args.get(1));
  }

  @Override
  public String name() {
    return "equal";
  }

  @Override
  public int arity() {
    return 2;
  }

  @Override
  public void register(UtterancePlanner planner) {}

}
