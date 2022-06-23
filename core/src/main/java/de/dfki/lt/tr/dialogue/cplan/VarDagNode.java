package de.dfki.lt.tr.dialogue.cplan;

import de.dfki.lt.tr.dialogue.cplan.io.DagPrinter;

public class VarDagNode extends SpecialDagNode {

  private String _varName;
  private Path _path;

  public VarDagNode(String string, int status) {
    this(string, status, null);
  }

  public VarDagNode(String string, Path path) {
    this(string, Bindings.GLOBAL, path);
  }

  private VarDagNode(String string, int status, Path path) {
    super(status);
    _varName = string;
    _path = path;
  }

  @Override
  public DagNode clone(int type) {
    return new VarDagNode(_varName, type, _path);
  }

  /** Return the binding associated with this variable, if there is any. This
   *  method is only to be used to determine the lval of an \c Action
   */
  public DagEdge getLvalBinding(DagEdge input, Bindings bindings) {
    // varName "#" is reserved and always bound to the current node
    DagEdge current = bindings.getBinding(_varName, getType());
    if (current == null &&
        (getType() == Bindings.GLOBAL || getType() == Bindings.RIGHTLOCAL)) {
      // create a new global or right local variable
      current = new DagEdge((short)-1, new DagNode());
      bindings.bind(_varName, current, getType());
    }
    if (getType() == Bindings.GLOBAL) {
      if (_path != null) {
        // make sure the path exists and return the last edge
        current = current.walkOrCreatePath(_path);
      }
    }
    if (current == null) {
      logger.warn("local variable not bound and used as lval " + _varName);
    }
    return current;
  }

  /** if _varname is in the bindings, return the value of the binding. This is
   *  is a read-only operation to expand variables or function calls to their
   *  values in the rval of an action on the right hand side of a rule.
   *
   *  One exception are right-local variables, which may be used to establish
   *  coreferences on the right hand side.
   */
  @Override
  protected DagNode evaluate(DagNode parent, Bindings bindings) {
    // type acts as storage for the status
    DagEdge bound = bindings.getBinding(_varName, getType());
    if (bound == null && getType() == Bindings.RIGHTLOCAL) {
      // first occurence of this var, bind the variable name
      // to the given node. This is on the application side, where new bindings
      // are only relevant in expandVars, which is why the DagEdge is not
      // important, but the node, to establish coreferences.
      bound = new DagEdge((short) -1, parent);
      bindings.bind(_varName, bound, getType());
    }
    if (bound != null && _path != null && getType() == Bindings.GLOBAL) {
      bound = bound.walkPath(_path);
    }
    if (bound == null) {
      logger.warn("Unknown binding during application of rule: " + this);
      return parent;
    }
    // Avoid unwanted coreferences for atomic nodes
    if (bound.getValue().edgesAreEmpty()
        && bound.getValue().getType() != DagNode.TOP_ID) {
      return bound.getValue().cloneFS();
    }
    return bound.getValue();
  }

  /*
  public DagNode getBoundNode(Bindings bindings) {
    DagEdge bound = bindings.getBinding(_varName, getType());
    assert(bound != null);
    return bound.getValue();
  }
  */

  @Override
  public void toStringRec(DagPrinter p) {
    switch (getType()) {
    case Bindings.ABSOLUTE:
      p.append(_varName).append(':');
      break;
    case Bindings.LOCAL:
      p.append('#');
      if (_varName != null && _varName.charAt(0) != '#') p.append(_varName);
      break;
    case Bindings.GLOBAL:
      p.append("##").append(_varName);
      if (_path != null) {
        p.append(_path.toString());
      }
      break;
    case Bindings.RIGHTLOCAL:
      p.append("###").append(_varName);
      break;
    }
  }
}
