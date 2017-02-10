package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class Contains implements Function {

  public Object apply(List<DagNode> args) {
    String str = args.get(0).toString(false);
    String match = args.get(1).toString(false);
//    System.out.println(str + "          " + match);
    return str.contains(match) ? 1 : 0;
  }

  /** java operation arg1.contains(arg2) */
  public String name() {
    return "contains";
  }

  public int arity() {
    return 2;
  }

  public void register(UtterancePlanner planner) {
  }
}
