package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.PlanningException;

/** This will throw a java exception, taking the exception message as argument
 */
public class ThrowExceptionFunction extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    if (args != null && args.size() >= 1) {
      throw new PlanningException(_planner.getEnvironment().toString(args.get(0)));
    } else {
      throw new PlanningException();
    }
  }

  public int arity() {
    return -1;
  }

  public String name() {
    return "throwException";
  }
}
