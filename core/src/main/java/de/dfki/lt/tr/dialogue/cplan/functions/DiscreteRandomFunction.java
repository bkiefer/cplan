package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;
import java.util.Random;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class DiscreteRandomFunction implements Function {

  private Random rand;

  public DiscreteRandomFunction() {
    rand = new Random(System.currentTimeMillis());
  }

  public Object apply(List<DagNode> args) {
    int i = rand.nextInt(args.size());
    return args.get(i);
  }

  public int arity() {
    return -1;
  }

  public String name() {
    return "random";
  }

  public void register(UtterancePlanner planner) { }

}
