package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

import org.apache.log4j.Logger;

/** This will throw a java exception, taking the exception message as argument
 */
public class WarningFunction implements Function {

  protected Logger logger = Logger.getLogger("UtterancePlanner");

  public Object apply(List<DagNode> args) {
    if (args != null && args.size() >= 1) {
      logger.warn(args.get(0).getTypeName());
    }
    return "true";
  }

  public int arity() {
    return 1;
  }

  public String name() {
    return "warning";
  }

  public void register(UtterancePlanner planner) {  }

}
