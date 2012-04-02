package plug;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

public class TestFunction implements de.dfki.lt.tr.dialogue.cplan.functions.Function {

  @SuppressWarnings("unchecked")
  @Override
  public Object apply(List args) {
    return (args.size() >= 2 ? args.get(1) : null);
  }

  @Override
  public String name() {
    return "test";
  }

  @Override
  public int arity() {
    return -1;
  }
  
  @Override
  public void register(UtterancePlanner planner) { }
}