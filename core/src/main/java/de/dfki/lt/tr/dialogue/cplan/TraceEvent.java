package de.dfki.lt.tr.dialogue.cplan;

public class TraceEvent {
  /** The state of the DagNode in the last matching phase */
  public DagNode lastMatch;
  /** The point in the dag where the match was performed */
  public DagEdge matchPoint;
  /** The current state of the DagNode after the last application */
  public DagNode curr;
  /** What edge is the action applied to? */
  public DagEdge appPoint;
  /** What is the rule that is matched / applied */
  public BasicRule rule;
  /** What were the bindings when the match was performed */
  public Bindings bindings;
}
