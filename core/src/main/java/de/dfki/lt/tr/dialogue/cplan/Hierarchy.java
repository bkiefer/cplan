package de.dfki.lt.tr.dialogue.cplan;

/** An adapter class for DagNode to wrap some type system */
public interface Hierarchy {
  /** Tell me if type1 subsumes type2, i.e., is equal to or more general
   *  than type2
   */
  public abstract boolean subsumes(int type1, int type2);

  /** Return a type id for the type name. */
  public int getTypeId(String name);

  /** Return a feature id for the feature name. */
  public short getFeatureId(String name);

  /** Return the name of the type with the given id. */
  public String getTypeName(int id);

  /** Return the name of the feature with the given id. */
  public String getFeatureName(short id);
}
