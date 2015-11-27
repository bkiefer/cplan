package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class Equal implements Function {

  public Object apply(List<DagNode> args) {
    return args.get(0).equals(args.get(1));
  }

  public String name() {
    return "equal";
  }

  public int arity() {
    return 2;
  }

  public void register(UtterancePlanner planner) {}

}
