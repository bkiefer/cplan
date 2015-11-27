package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class ConstantFunction implements Function {

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
  
  public void register(UtterancePlanner planner) { }

}
