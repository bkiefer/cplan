package de.dfki.lt.tr.dialogue.cplan;

/** An adapter class for DagNode to wrap some type system */
public interface Hierarchy {
  /** Establish a mapping between the internal types of the hierarchy and
   *  DagNode's number representation
   */
  public abstract void registerType(String name, int id);

  /** Tell me if type1 subsumes type2, i.e., is equal to or more general
   *  than type2
   */
  public abstract boolean subsumes(int type1, int type2);
}
