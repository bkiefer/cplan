package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class Length implements Function {

  public Object apply(List<DagNode> args) {
    String arg = args.get(0).toString(false);
    return arg.length();
  }

  
  /** Count the words in this string (split is any sequence of white space) */
  public String name() {
    return "length";
  }

  public int arity() {
    return 1;
  }

  public void register(UtterancePlanner planner) {
  }

}
