package de.dfki.lt.tr.dialogue.cplan;

import java.util.HashMap;

import opennlp.ccg.grammar.Grammar;
import opennlp.ccg.unify.SimpleType;
import opennlp.ccg.unify.UnifyFailure;

public class CcgHierarchy implements Hierarchy {

  private Hierarchy _h;

  /** The CCG grammar whose type system i'm adapting */
  private Grammar _g;

  private HashMap<Integer, SimpleType> _typeMap;

  private int lastRegistered = -1;

  public CcgHierarchy(Grammar g) {
    _h = new FlatHierarchy();
    _g = g;
    _typeMap = new HashMap<Integer, SimpleType>();
  }

  public CcgHierarchy(Grammar g, Hierarchy h) {
    //this.nameToFeature = h.nameToFeature;
    //this.nameToType = h.nameToType;
    _g = g;
    _h = h;
    _typeMap = new HashMap<Integer, SimpleType>();
  }

  private void registerType(String name, int id) {
    if (! _typeMap.containsKey(id)) {
      SimpleType newType = null;
      if (_g.types.containsSimpleType(name)) {
        newType = _g.types.getSimpleType(name);
      }
      _typeMap.put(id, newType);
    }
  }

  public int getTypeId(String name) {
    int newId = _h.getTypeId(name);
    if (newId > lastRegistered) {
      lastRegistered = newId;
      registerType(name, newId);
    }
    return newId;
  }

  public boolean subsumes(int type1, int type2) {
    SimpleType st1 = _typeMap.get(type1);
    SimpleType st2 = _typeMap.get(type2);
    if (st1 == null || st2 == null) return _h.subsumes(type1, type2);
    try {
      SimpleType unified = (SimpleType) st1.unify(st2, null);
      return unified.equals(st2);
    }
    catch (UnifyFailure uf) {
    }
    return false;
  }

  @Override
  public short getFeatureId(String name) {
    return _h.getFeatureId(name);
  }

  @Override
  public String getTypeName(int id) {
    return _h.getTypeName(id);
  }

  @Override
  public String getFeatureName(short id) {
    return _h.getFeatureName(id);
  }

}
