package de.dfki.lt.tr.dialogue.cplan;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.functions.Function;
import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.io.DagPrinter;

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

  @Override
  @SuppressWarnings("rawtypes")
  public void toStringRec(DagPrinter p) {
    p.append(_name).append('(');
    if (_args != null) {
      Iterator it = _args.iterator();
      if (it.hasNext()) {
        Object o = it.next();
        if (o instanceof DagNode) {
          p.tsr((DagNode)o);
        } else {
          p.append((it.next()).toString());
        }
      }
      while (it.hasNext()) {
        p.append(", ");
        Object o = it.next();
        if (o instanceof DagNode) {
          p.tsr((DagNode)o);
        } else {
          p.append((it.next()).toString());
        }
      }
    }
    p.append(')');
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
    Object o = _function.apply(getActualParameters(_args, input, bindings));
    DagNode dag =
      ((o instanceof DagNode)
          ? ((DagNode) o)
              : new DagNode(PROP_FEAT_ID, new DagNode(o.toString())));
    return dag;
  }

  /** assign coref numbers to coreferenced nodes. Only nodes that are referred
   *  to more than once get a number greater that zero, all other nodes get
   *  zero.
   *  @return the number of nodes that were referenced more than once.
   */
  public int
  countCorefsLocal(IdentityHashMap<DagNode, Integer> corefs, int nextCorefNo) {
    DagNode here = dereference();
    if (! corefs.containsKey(here)) { // visited for the first time
      corefs.put(here, 0);
      for (Object arg : _args) {
        if (arg instanceof DagNode)
          nextCorefNo = ((DagNode)arg).countCorefsLocal(corefs, nextCorefNo);
      }
    } else {
      int corefNo = corefs.get(here);
      if (corefNo == 0) { // visited for the second time at least
        corefs.put(here, ++nextCorefNo);
      }
    }
    return nextCorefNo;
  }
}
