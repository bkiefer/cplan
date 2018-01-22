package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public class Contains extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    String str = toString(args.get(0));
    String match = toString(args.get(1));
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
}
