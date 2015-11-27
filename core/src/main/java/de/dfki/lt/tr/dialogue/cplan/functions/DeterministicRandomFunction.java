package de.dfki.lt.tr.dialogue.cplan.functions;

import java.util.ArrayList;
import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

/** This class is a replacement for DiscreteRandomFunction to systematically
 *  generate all call sequences of the random function that can occur during
 *  a batch generation of some test item.
 * @author kiefer
 */
public class DeterministicRandomFunction implements Function {

  /** The number of the calls in this round (from start up to the fix point) */
  private int _callNo;

  /** The current list of arities for the different calls */
  private ArrayList<Integer> _arities;

  /** The current list of choice points (one instance of a call sequence) */
  private ArrayList<Integer> _alternatives;

  /** Call this before starting realization, it generates a new call sequence
   *
   * @return true if there is a new call sequence, false otherwise, which means
   *  that the test item has been exhaustively processed.
   */
  public boolean newRound() {
    _callNo = 0;
    if (_alternatives == null) {
      // first call
      _alternatives = new ArrayList<Integer>(10);
      _arities = new ArrayList<Integer>(10);
      return true;
    }
    if (_alternatives.isEmpty()) return false;
    int last = _alternatives.size() - 1;
    do {
      int current = _alternatives.get(last) + 1;
      if (current >= _arities.get(last)) {
        _alternatives.remove(last);
        _arities.remove(last);
        --last;
        if (last < 0) return false;
      } else {
        _alternatives.set(last, current);
        return true;
      }
    } while (true);
  }

  /** Call this when the processing of a new test item starts */
  public void newInput() {
    _alternatives = null;
    _arities = null;
  }

  private static <T> void toString(StringBuilder sb, List<T> l) {
    sb.append("[ ");
    for (T i : l) {
      sb.append(i.toString()).append(" ");
    }
    sb.append(']');
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    //sb.append(_callNo + "\n");
    toString(sb, _alternatives); sb.append(' ');
    toString(sb, _arities); sb.append(' ');
    sb.append(_callNo);
    return sb.toString();
  }

  @Override
  public Object apply(List<DagNode> args) {
    // if this call is not recorded in the current call sequence, add it and
    // take the first alternative
    if (_callNo >= _alternatives.size()) {
      _alternatives.add(0);
      _arities.add(args.size());
    }
    // System.out.println(this);
    int current =_alternatives.get(_callNo++);
    return args.get(current);
  }

  @Override
  public int arity() {
    return -1;
  }

  @Override
  public String name() {
    return "random";
  }

  @Override
  public void register(UtterancePlanner planner) { }

}
