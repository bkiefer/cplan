package de.dfki.lt.tr.dialogue.cplan.functions.math;

public class LtEq extends BinaryOp {
  @Override
  public double mathApply(double x, double y) { return x <= y ? 1 : 0; }

  @Override
  public String name() { return "lteq"; }
}
