package de.dfki.lt.tr.dialogue.cplan;


public abstract class SpecialDagNode extends DagNode {

  protected SpecialDagNode(Environment env, int status) {
    super(env, status);
  }

  protected SpecialDagNode(Environment env) {
    super(env);
  }

  /** All subtypes of DagNode have to override this method to get the cloning
   *  correctly
   */
  @Override
  public abstract DagNode clone(int type);

  /** Evaluate this special dag node for expansion of right hand side */
  protected abstract DagNode evaluate(DagNode input, Bindings bindings);

  /** This is called in DagNode's toStringRec function */
  public abstract void toStringSpecial(StringBuilder sb);

}

