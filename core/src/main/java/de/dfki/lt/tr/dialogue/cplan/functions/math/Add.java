package de.dfki.lt.tr.dialogue.cplan.functions.math;

public class Add extends BinaryOp {
  @Override
  public double mathApply(double x, double y) { return x + y; }

  public String name() { return "add"; }
}
