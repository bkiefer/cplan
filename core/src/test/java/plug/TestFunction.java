package plug;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class TestFunction implements de.dfki.lt.tr.dialogue.cplan.functions.Function {

  public Object apply(@SuppressWarnings("rawtypes") List args) {
    return (args.size() >= 2 ? args.get(1) : null);
  }

  public String name() {
    return "test";
  }

  public int arity() {
    return -1;
  }

  public void register(UtterancePlanner planner) { }
}