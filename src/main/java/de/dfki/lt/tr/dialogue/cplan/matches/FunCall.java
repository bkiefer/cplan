package de.dfki.lt.tr.dialogue.cplan.matches;

import java.util.Iterator;
import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.Bindings;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.FunCallDagNode;
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
  public FunCall(String name, List args) throws NoSuchMethodException {
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

  @SuppressWarnings("rawtypes")
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_name).append("(");
    if (_args != null && ! _args.isEmpty()) {
      sb.append(_args.get(0));
      Iterator it = _args.iterator();
      it.next();
      while (it.hasNext()) {
        sb.append(", ").append(it.next());
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

  /** The implementation of MatchLVal */
  @Override
  public DagEdge getBinding(DagEdge input, Bindings bindings) {
    Object result =
      _fn.apply(FunCallDagNode.getActualParameters(_args, null, bindings));
    DagNode subRes = (result instanceof DagNode)
                     ? (DagNode) result
                     : new DagNode(result.toString());
    return new DagEdge((short)-1,
        (subRes.edgesAreEmpty()
         ? new DagNode(DagNode.PROP_FEAT_ID, subRes)
         : subRes));

  }

  @Override
  public Match deepCopy() {
    try {
      return this.copy(new FunCall(_name, _args));
    }
    catch (NoSuchMethodException ex) {
      assert(false);  // will not happen, has been checked before.
    }
    return null;
  }
}