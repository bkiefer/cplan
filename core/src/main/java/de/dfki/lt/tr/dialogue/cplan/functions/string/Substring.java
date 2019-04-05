package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class Substring implements Function {

  public Object apply(List<DagNode> args) {
    String str = args.get(0).asString();
    String from = args.get(1).asString();
    String to = args.get(2).asString();
    int fromIndex = Integer.parseInt(from);
    if (fromIndex < 0) fromIndex = str.length() + fromIndex;
    int toIndex = Integer.parseInt(to);
    if (toIndex < 0) toIndex = str.length() + toIndex + 1;
    return str.substring(fromIndex, toIndex);
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
