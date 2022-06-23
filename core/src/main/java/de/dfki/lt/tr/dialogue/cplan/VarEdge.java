package de.dfki.lt.tr.dialogue.cplan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VarEdge extends DagEdge {

  public static Logger logger = LoggerFactory.getLogger("VarEdge");

  String _varName;
  Path _path;
  int _binding;

  public VarEdge(String varName, int bindingType, DagNode value) {
    super((short)-1, value);
    _binding = bindingType;
    _varName = varName;
    _path = null;
  }

  public VarEdge(String varName, Path path, DagNode value) {
    super((short)-1, value);
    _binding = Bindings.GLOBAL;
    _varName = varName;
    _path = path;
  }

  @Override
  public DagEdge evaluate(Bindings bindings) {
    // determine the current feature ID
    DagEdge current = bindings.getBinding(_varName, _binding);
    if (current == null) {
      logger.warn("variable not bound and used as lval " + _varName);
    }
    if (_binding == Bindings.GLOBAL) {
      if (_path != null) {
        // make sure the path exists and return the last edge
        current = current.walkOrCreatePath(_path);
        if (current == null) {
          logger.warn("path {} does not exist for global variable {}", _varName, _path);
        }
      }
    }
    if (current != null) {
      String name = current.getValue().asString();
      // TODO: maybe check if this is a valid string according to ID rules
      _feature = DagNode.getFeatureId(name);
    }
    return this;
  }

  @Override
  public DagEdge copySafely(DagNode newValue) {
    VarEdge result = new VarEdge(_varName, _binding, newValue);
    result._feature = _feature;
    return result;
  }
}
