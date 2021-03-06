package de.dfki.lt.tr.dialogue.cplan.functions.math;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;

public abstract class BinaryOp extends MathOp {

  public int arity() { return 2; }

  public Object apply(List<DagNode> args) {
    double result;
    try {
      result = mathApply(Double.parseDouble(args.get(0).asString()),
                         Double.parseDouble(args.get(1).asString()));
    }
    catch (NumberFormatException nex) {
      return "NaN";
    }

    return toString(result);
  }

  protected abstract double mathApply(double x, double y);

}
