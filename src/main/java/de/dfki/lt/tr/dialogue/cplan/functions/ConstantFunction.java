package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class ConstantFunction implements Function {

  private String _constant;

  public ConstantFunction(String constant) {
    _constant = constant;
  }

  @Override
  public Object apply(List<DagNode> args) {
    return _constant;
  }

  @Override
  public String name() {
    return "constant"+_constant;
  }

  @Override
  public int arity() {
    return 0;
  }
  
  @Override
  public void register(UtterancePlanner planner) { }

}
