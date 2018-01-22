package plug;

import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public class TestFunction extends AbstractFunction {

  public Object apply(@SuppressWarnings("rawtypes") List args) {
    return (args.size() >= 2 ? args.get(1) : null);
  }

  public String name() {
    return "test";
  }

  public int arity() {
    return -1;
  }
}