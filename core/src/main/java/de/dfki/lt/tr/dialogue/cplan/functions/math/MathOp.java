package de.dfki.lt.tr.dialogue.cplan.functions.math;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public abstract class MathOp implements Function {

  protected String toString(double value){
    if(java.lang.Math.floor(value) == value){
      return Integer.toString((int) value);
    } else return Double.toString(value);
  }

  public void register(UtterancePlanner planner) { }

public Object apply(List<DagNode> args) {
	// TODO Auto-generated method stub
	return null;
}

public int arity() {
	// TODO Auto-generated method stub
	return 0;
}

public String name() {
	// TODO Auto-generated method stub
	return null;
}
}
