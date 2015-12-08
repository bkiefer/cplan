package de.dfki.lt.tr.dialogue.cplan.functions.math;

import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public abstract class MathOp implements Function {

  protected String toString(double value){
    if(java.lang.Math.floor(value) == value){
      return Integer.toString((int) value);
    } else return Double.toString(value);
  }

  public void register(UtterancePlanner planner) { }

}
