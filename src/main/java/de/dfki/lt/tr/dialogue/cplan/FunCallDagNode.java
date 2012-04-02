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

  @Override
  @SuppressWarnings("rawtypes")
  public void toStringSpecial(StringBuilder sb) {
    sb.append(_name).append('(');
    if (_args != null) {
      Iterator it = _args.iterator();
      if (it.hasNext()) {
        sb.append((it.next()).toString());
      }
      while (it.hasNext()) {
        sb.append(", ").append((it.next()).toString());
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
      actualParameters.add((o instanceof SpecialDagNode)
                           ? ((SpecialDagNode)o).evaluate(input, bindings)
                           : ((DagNode) o));
    }
    return actualParameters;
  }

  @Override
  public DagNode evaluate(DagNode input, Bindings bindings) {
    Object o = _function.apply(getActualParameters(_args, input, bindings));
    DagNode dag =
      ((o instanceof DagNode)
          ? ((DagNode) o)
              : new DagNode(PROP_FEAT_ID, new DagNode(o.toString())));
    return dag;
  }

}
