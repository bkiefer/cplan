package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;

public class ConstantFunction extends AbstractFunction {

  private String _constant;

  public ConstantFunction(String constant) {
    _constant = constant;
  }

  public Object apply(List<DagNode> args) {
    return _constant;
  }

  public String name() {
    return "constant"+_constant;
  }

  public int arity() {
    return 0;
  }
}
