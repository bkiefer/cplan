package de.dfki.lt.tr.dialogue.cplan.functions.math;

public class Gt extends BinaryOp {
  @Override
  public double mathApply(double x, double y) { return x > y ? 1 : 0; }

  public String name() { return "gt"; }
}
