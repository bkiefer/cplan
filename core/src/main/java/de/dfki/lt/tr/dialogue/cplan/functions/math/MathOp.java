package de.dfki.lt.tr.dialogue.cplan.functions.math;

import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public abstract class MathOp extends AbstractFunction {

  protected String toString(double value){
    if(java.lang.Math.floor(value) == value){
      return Integer.toString((int) value);
    } else return Double.toString(value);
  }

}
