package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

/** A generic function object */
public interface Function {

  /** to call this function.
   *  @param args a list of @link{DagNode}s.
   *  @return The return value must be either a DagNode, to
   *     return a complex result, or something that has a simple String
   *     representation, which will be put as symbol under the PROP feature.
   */
  public abstract Object apply(List<DagNode> args);

  /** The name of this function, for registration in the factory */
  public abstract String name();

  /** The arity, a negative value that a variable amount of arguments
   * is possible.
   */
  public abstract int arity();

  /** Connect this function to the Planner where it was registered */
  public abstract void register(UtterancePlanner planner);

  /** Evaluate this function with the given arguments */
  public abstract DagNode evaluate(List<DagNode> args);
}
