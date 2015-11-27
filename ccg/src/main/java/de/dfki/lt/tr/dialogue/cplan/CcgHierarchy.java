package de.dfki.lt.tr.dialogue.cplan;

import java.util.HashMap;

import opennlp.ccg.grammar.Grammar;
import opennlp.ccg.unify.SimpleType;
import opennlp.ccg.unify.UnifyFailure;

public class CcgHierarchy implements Hierarchy {

  /** The CCG grammar whose type system i'm adapting */
  private Grammar _g;

  private HashMap<Integer, SimpleType> _typeMap;

  public CcgHierarchy(Grammar g) {
    _g = g;
    _typeMap = new HashMap<Integer, SimpleType>();
  }

  public void registerType(String name, int id) {
    if (! _typeMap.containsKey(id)) {
      SimpleType newType = null;
      if (_g.types.containsSimpleType(name)) {
        newType = _g.types.getSimpleType(name);
      }
      _typeMap.put(id, newType);
    }
  }

  public boolean subsumes(int type1, int type2) {
    SimpleType st1 = _typeMap.get(type1);
    if (st1 == null) return false;
    SimpleType st2 = _typeMap.get(type2);
    if (st2 == null) return false;
    try {
      SimpleType unified = (SimpleType) st1.unify(st2, null);
      return unified.equals(st2);
    }
    catch (UnifyFailure uf) {
    }
    return false;
  }

}
