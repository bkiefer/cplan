package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public class Endswith extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    String str = toString(args.get(0));
    String end = toString(args.get(1));
//    System.out.println(str + "          " + end + "    " + str.endsWith(end));
    return str.endsWith(end) ? 1 : 0;
  }

  /** java operation arg1.endsWith(arg2) */
  public String name() {
    return "endswith";
  }

  public int arity() {
    return 2;
  }
}
