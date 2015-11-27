package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.PlanningException;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

/** This will throw a java exception, taking the exception message as argument
 */
public class ThrowExceptionFunction implements Function {

  @Override
  public Object apply(List<DagNode> args) {
    if (args != null && args.size() >= 1) {
      throw new PlanningException(args.get(0).toString());
    } else {
      throw new PlanningException();
    }
  }

  @Override
  public int arity() {
    return -1;
  }

  @Override
  public String name() {
    return "throwException";
  }

  @Override
  public void register(UtterancePlanner planner) {  }

}
