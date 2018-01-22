package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;

public class CloneNode extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    return args.get(0).copySafely();
  }

  public int arity() {
    return 1;
  }

  public String name() {
    return "clone";
  }
}
