package de.dfki.lt.tr.dialogue.cplan;

import de.dfki.lt.tr.dialogue.cplan.util.IntIDMap;
import de.dfki.lt.tr.dialogue.cplan.util.ShortIDMap;

/** An adapter class for DagNode to wrap some type system */
public class FlatHierarchy implements Hierarchy {

  protected IntIDMap<String> nameToType = new IntIDMap<String>();

  protected ShortIDMap<String> nameToFeature = new ShortIDMap<String>();

  /** Tell me if type1 subsumes type2, i.e., is equal to or more general
   *  than type2
   */
  @Override
  public boolean subsumes(int type1, int type2) { return false; }

  @Override
  public short getFeatureId(String name) {
    if (nameToFeature.contains(name))
      return nameToFeature.getId(name);
    else
      return nameToFeature.register(name);
  }

  @Override
  public String getFeatureName(short feature) {
    if (feature >= 0)
      return nameToFeature.fromId(feature);
    else
      return "ILL";
  }

  @Override
  public int getTypeId(String name) {
    if (nameToType.contains(name))
      return nameToType.getId(name);
    else {
      return nameToType.register(name);
    }
  }

  @Override
  public String getTypeName(int type) {
    return nameToType.fromId(type);
  }
}
