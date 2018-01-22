package de.dfki.lt.tr.dialogue.cplan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.functions.Function;
import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;

@SuppressWarnings("unchecked")
public class FunCallDagNode extends SpecialDagNode {

  private String _name;
  private Function _function;
  @SuppressWarnings("rawtypes")
  private List _args;

  @SuppressWarnings("rawtypes")
  public FunCallDagNode(String name, List args) throws NoSuchMethodException {
    super();
    _name = name;
    _function = FunctionFactory.get(_name);
    if (_function == null) {
      throw new NoSuchMethodException(_name);
    }
    if (_function.arity() > 0 && args.size() != _function.arity()) {
      throw new IndexOutOfBoundsException("Wrong number of arguments for "
          + _name + ": " + args.size() + " instead of " + _function.arity());
    }
    _args = args;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public DagNode clone(int type) {
    try {
      List args = null;
      if (_args != null) {
        args = new ArrayList(_args.size());
        for (Object arg : _args) {
          if (arg instanceof DagNode) {
            DagNode dagArg = ((DagNode)arg);
            args.add(dagArg.clone(dagArg.getType()));
            //dagArg.cloneFS());
          } else {
            args.add(arg);
          }
        }
      }
      FunCallDagNode result = new FunCallDagNode(_name, args);
      result._typeCode = type;
      return result;
    }
    catch (NoSuchMethodException ex) {
      // will never occur
    }
    return null;
  }

  private String toString(Environment env, Object o) {
    return (o instanceof DagNode)
        ? env.toString((DagNode)o)
            : o.toString();
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void toStringSpecial(Environment env, StringBuilder sb) {
    sb.append(_name).append('(');
    if (_args != null) {
      Iterator it = _args.iterator();
      if (it.hasNext()) {
        sb.append(toString(env, (it.next())));
      }
      while (it.hasNext()) {
        sb.append(", ").append(toString(env, (it.next())));
      }
    }
    sb.append(')');
  }

  /** for all args, create proper dag nodes without variables. */
  @SuppressWarnings("rawtypes")
  public static List<DagNode> getActualParameters(List formalParameters,
                                                  DagNode input,
                                                  Bindings bindings) {
    if (formalParameters == null)
      return null;
    List<DagNode> actualParameters =
      new ArrayList<DagNode>(formalParameters.size());
    for (Object o : formalParameters) {
      DagNode d = (DagNode) o;
      /*
      DagEdge e = d.getEdge(DagNode.PROP_FEAT_ID);
      if (e != null && (e.getValue() instanceof SpecialDagNode)){
        d = ((SpecialDagNode) e.getValue()).expandVars(input, bindings);
      }
      */
      actualParameters.add((d instanceof SpecialDagNode)
                           ? ((SpecialDagNode)d).evaluate(input, bindings)
                           : d);
      /*
      try {
        actualParameters.add(d.expandVars(bindings));
      }
      catch (UnificationException ex) {
        logger.error(ex);
      }
      */
    }
    return actualParameters;
  }

  @Override
  public DagNode evaluate(DagNode input, Bindings bindings) {
    return _function.evaluate(getActualParameters(_args, input, bindings));
  }

}
