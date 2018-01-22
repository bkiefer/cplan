package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.Environment;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public abstract class AbstractFunction implements Function {
  protected UtterancePlanner _planner;

  @Override
  public void register(UtterancePlanner planner) {
    _planner = planner;
  }

  protected String toString(DagNode dag) {
    return _planner.getEnvironment().toString(dag, false);
  }

  public DagNode evaluate(List<DagNode> args) {
    Object o = apply(args);
    DagNode dag;
    if (! (o instanceof DagNode)) {
      Environment env = _planner.getEnvironment();
      dag = new DagNode(env.PROP_FEAT_ID, env.getDagNode(o.toString()));
    } else {
      dag = (DagNode)o;
    }
    return dag;
  }
}
