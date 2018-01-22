package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

/** Count the words in the argument string.
 *  "Words" here means: delimited by string boundary or any white space
 */
public class WordCount extends AbstractFunction {

  public Object apply(List<DagNode> args) {
    String[] words = toString(args.get(0)).split("\\s+");
    return words.length;
  }

  /** Count the words in this string (split is any sequence of white space) */
  public String name() {
    return "wc";
  }

  public int arity() {
    return 1;
  }
}
