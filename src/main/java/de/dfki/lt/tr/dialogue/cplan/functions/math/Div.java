package de.dfki.lt.tr.dialogue.cplan.functions.math;

public class Div extends BinaryOp {
  @Override
  public double mathApply(double x, double y) { return x / y; }

  @Override
  public String name() { return "div"; }
}
