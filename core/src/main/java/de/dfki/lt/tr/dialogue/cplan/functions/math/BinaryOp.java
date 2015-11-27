package de.dfki.lt.tr.dialogue.cplan.functions.math;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;

public abstract class BinaryOp extends MathOp {

  @Override
  public int arity() { return 2; }

  @Override
  public Object apply(List<DagNode> args) {
    double result;
    try {
      result = mathApply(Double.parseDouble(args.get(0).toString(false)),
                         Double.parseDouble(args.get(1).toString(false)));
    }
    catch (NumberFormatException nex) {
      return "NaN";
    }

    return toString(result);
  }

  protected abstract double mathApply(double x, double y);

public String name() {
	// TODO Auto-generated method stub
	return null;
}
}
