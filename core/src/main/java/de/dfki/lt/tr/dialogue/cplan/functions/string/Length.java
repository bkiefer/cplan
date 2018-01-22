package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public class Length extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    String arg = toString(args.get(0));
    return arg.length();
  }

  /** Count the words in this string (split is any sequence of white space) */
  public String name() {
    return "length";
  }

  public int arity() {
    return 1;
  }
}
