package de.dfki.lt.tr.dialogue.cplan.matches;

import java.util.Iterator;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;

/** A syntax tree node representing a feature value pair */
public class FeatVal extends Match {

  private short _feature;
  private LocalVar _featureVar;
  private Match _value;

  public FeatVal(String feature, Match value) {
    _featureVar = null;
    _feature = DagNode.getFeatureId(feature);
    _value = value;
  }

  public FeatVal(short featureId, Match value) {
    _featureVar = null;
    _feature = featureId;
    _value = value;
  }

  public FeatVal(Match feature, Match value) {
    _featureVar = null;
    _feature = -1;

    _value = value;
    if (feature instanceof Atom) {
      _feature = DagNode.getFeatureId(((Atom)feature)._value);
      return;
    }
    /* not supported: variables binding feature names
    if (feature instanceof LocalVar) {
      _featureVar = (LocalVar) feature;
      return;
    }
    */
    // this is an error that should not occur
    assert(false);
  }

  @Override
  public String toString() {
    String sub;
    if (_feature == -1) {
      sub =  "<" + _featureVar + "> " + _value;
    } else if (_feature == DagNode.TYPE_FEAT_ID) {
      if (_value == null) {
        sub = "<__TYPE>";
      } else {
        sub = _value.toStringBare();
        sub = ":" + sub;
      }
    } else if (_feature == DagNode.PROP_FEAT_ID) {
      sub = _value == null ? "<__PROP>" : _value.toStringBare();
      //if (sub.endsWith(":")) {
      //  sub = sub.substring(0, sub.length() - 1);
      //}
    } else if (_feature == DagNode.ID_FEAT_ID) {
      sub = _value + ":";
    } else if (_value == null) {
      sub = "<" + DagNode.getFeatureName(_feature) + "> ";
    } else {
      sub = "<" + DagNode.getFeatureName(_feature) + "> " + _value;
    }
    return super.toString() + sub;
  }

  @Override
  protected boolean match(DagEdge input, Bindings bindings) {
    if (_feature != -1) {
      // look for the feature in current input
      Iterator<DagEdge> subEdges = input.getEdges(_feature);
      if (subEdges == null || ! subEdges.hasNext()) return false;
      // _value == null tests only for feature existence
      if (_value == null) return true;
      while (subEdges.hasNext()) {
        if (_value.matches(subEdges.next(), bindings)) return true;
      }
      return false;
    }
    // TODO variable matching feature not implemented: massive disjunction
    throw new UnsupportedOperationException("Matching features against vars" +
    		" not implemented yet");
  }

  @Override
  void normalForm() {
    _value.normalForm();
  }

  @Override
  public Match deepCopy() {
    FeatVal newMatch = new FeatVal(_feature,
        _value == null ? null : _value.deepCopy());
    return copy(newMatch);
  }
}

