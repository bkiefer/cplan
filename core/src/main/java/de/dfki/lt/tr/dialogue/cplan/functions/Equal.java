package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;

public class Equal extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    return args.get(0).equals(args.get(1)) ? 1 : 0;
  }

  public String name() {
    return "equal";
  }

  public int arity() {
    return 2;
  }
}
