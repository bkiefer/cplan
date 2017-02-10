package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class Substring implements Function {

  public Object apply(List<DagNode> args) {
    String str = args.get(0).toString(false);
    String from = args.get(1).toString(false);
    String to = args.get(2).toString(false);
    return str.substring(Integer.parseInt(from), Integer.parseInt(to));
  }
  
  /** java operation arg1.substring(arg2, arg3) */
  public String name() {
    return "substring";
  }

  public int arity() {
    return 3;
  }

  public void register(UtterancePlanner planner) {
  }
}
