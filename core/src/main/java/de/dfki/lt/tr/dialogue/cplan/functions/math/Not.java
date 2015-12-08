package de.dfki.lt.tr.dialogue.cplan.functions.math;

public class Not extends UnaryOp {
  @Override
  public double mathApply(double x) { return x == 0 ? 1 : 0; }

  public String name() { return "not"; }
}
