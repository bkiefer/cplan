package de.dfki.lt.tr.dialogue.cplan;

public class PlanningException extends RuntimeException {
  private static final long serialVersionUID = 2294221678771199866L;

  public PlanningException() {
    super();
  }
  
  public PlanningException(String msg) {
    super(msg);
  }
}
