package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;

public class Bound extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    return args.get(0) != null ? 1 : 0;
  }

  public String name() {
    return "bound";
  }

  public int arity() {
    return 1;
  }
}
