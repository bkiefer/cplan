package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public class Substring extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    String str = toString(args.get(0));
    String from = toString(args.get(1));
    String to = toString(args.get(2));
    int toIndex = Integer.parseInt(to);
    if (toIndex < 0) toIndex = str.length() - toIndex;
    return str.substring(Integer.parseInt(from), toIndex);
  }

  /** java operation arg1.substring(arg2, arg3) */
  public String name() {
    return "substring";
  }

  public int arity() {
    return 3;
  }
}
