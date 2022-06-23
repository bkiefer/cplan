package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class Split implements Function {

  public Object apply(List<DagNode> args) {
    String input = args.get(0).asString();
    String regex = args.get(1).asString();
    String[] tokens = input.split(regex);
    DagNode res = new DagNode().setNominal();
    int i = 1;
    for (String token : tokens) {
      res.addEdge(DagNode.getFeatureId(Integer.toString(i)),
          new DagNode(DagNode.PROP_FEAT_ID, new DagNode(token)));
      ++i;
    }
    return res;
  }

  public int arity() { return 2; }

  public String name() { return "split"; }

  public void register(UtterancePlanner planner) {}

}
