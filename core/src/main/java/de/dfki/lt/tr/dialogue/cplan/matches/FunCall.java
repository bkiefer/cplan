package de.dfki.lt.tr.dialogue.cplan.matches;

import java.util.Iterator;
import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.*;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;
import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;

/** A syntax tree node representing a function application of some custom
 *  (internal or external) function
 */
public class FunCall extends Match implements MatchLVal {

  /** The name of the function. */
  private String _name;
  /** The function object itself. */
  private Function _fn;

  /** the list of arguments to the function call */
  @SuppressWarnings("rawtypes")
  private List _args;

  /** Create the function call object, giving its name and arguments */
  @SuppressWarnings("rawtypes")
  public FunCall(Environment env, String name, List args) throws NoSuchMethodException {
    _env = env;
    _name = name;
    _fn = FunctionFactory.get(_name);
    if (_fn == null) {
      throw new NoSuchMethodException(_name);
    }
    if (_fn.arity() > 0 && args.size() != _fn.arity()) {
      throw new IndexOutOfBoundsException("Wrong number of arguments for "
          + _name + ": " + args.size() + " instead of " + _fn.arity());
    }
    _args = args;
  }

  private String toString(Object o) {
    return (o instanceof DagNode)
        ? _env.toString((DagNode)o)
            : o.toString();
  }

  @SuppressWarnings("rawtypes")
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_name).append("(");
    if (_args != null && ! _args.isEmpty()) {
      sb.append(toString(_args.get(0)));
      Iterator it = _args.iterator();
      it.next();
      while (it.hasNext()) {
        sb.append(", ").append(toString(it.next()));
      }
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public String toStringBare() {
    return toString();
  }

  /* We don't support this until somebody REALLY needs it */

  /** Applying a function on the left hand side means that it has to be a
   *  function returning a boolean value, otherwise, a run time error will
   *  result and throw a ClassCastException
   *
  @Override
  public boolean match(DagEdge input, Bindings bindings) {
    Object result =
      _fn.apply(FunCallDagNode.getActualParameters(_args, null, bindings));
    if (result instanceof Boolean) {
      return (Boolean) result;
    }
    if (result instanceof DagNode) {
      // TODO match result against input: does result subsume input
      throw new UnsupportedOperationException("Subsumption of logical forms" +
                                              " not  implemented yet");
    }
    return false;
  }
  */

  @Override
  public boolean match(DagEdge input, Bindings bindings) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  /*
  @Override
  public boolean match(DagEdge input, Bindings bindings) {
    // throw new UnsupportedOperationException("Not yet implemented");
    Object result =
        _fn.apply(FunCallDagNode.getActualParameters(_args, null, bindings));
    if (result != null) {
      if (result instanceof Boolean) return (Boolean)result;
      if (result instanceof String)
        return "true".equals(((String) result).toLowerCase());
      if (result instanceof Double)
        return (Double) result != 0;
      return true;
    }
    return false;
  }
  */

  /** The implementation of MatchLVal */
  public DagEdge getBinding(DagEdge input, Bindings bindings) {
    Object result =
      _fn.apply(FunCallDagNode.getActualParameters(_args, null, bindings));
    DagNode subRes = (result instanceof DagNode)
                     ? (DagNode) result
                     : _env.getDagNode(result.toString());
    return new DagEdge((short)-1,
        (subRes.edgesAreEmpty()
         ? new DagNode(_env.PROP_FEAT_ID, subRes)
         : subRes));

  }

  @Override
  public Match deepCopy() {
    try {
      return this.copy(new FunCall(_env, _name, _args));
    }
    catch (NoSuchMethodException ex) {
      assert(false);  // will not happen, has been checked before.
    }
    return null;
  }
}
