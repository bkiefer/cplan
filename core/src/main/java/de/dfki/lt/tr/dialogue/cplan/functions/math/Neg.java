package de.dfki.lt.tr.dialogue.cplan.functions.math;

public class Neg extends UnaryOp {
  @Override
  public double mathApply(double x) { return - x; }

  @Override
  public String name() { return "neg"; }
}
