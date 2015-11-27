package de.dfki.lt.tr.dialogue.cplan;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

/** This tracer only works if the actions are performed in the same order as
 *  the matches.
 */
public class CollectEventsTracer extends SuspendableTracer {

  private List<TraceEvent> _events;

  int _last = 0;

  /** Initialize a new tracer with internal event storage */
  public CollectEventsTracer() {
    this(new ArrayList<TraceEvent>());
  }

  /** Initialize a new tracer with external event storage */
  public CollectEventsTracer(List<TraceEvent> coll) {
    _events = coll;
  }

  public List<TraceEvent> getEvents() {
    return _events;
  }

  public void traceAfterApplication(DagEdge root, DagEdge current, BasicRule r,
      Bindings bindings) {
    TraceEvent curr = _events.get(_last);
    // we copy the intermediate dag, and get a mapping from original
    // to the copied edges
    IdentityHashMap<Object, Object> origToCopy =
      new IdentityHashMap<Object, Object>();
    curr.curr = root.copyIntermediate(origToCopy).getValue();
    curr.bindings = bindings.copy(origToCopy);
    curr.appPoint = (DagEdge) origToCopy.get(current); // or arg: curr.appPoint??
    ++_last;
  }

  public void traceMatch(DagEdge root, DagEdge current, BasicRule r,
      Bindings bindings) {
    checkSuspended();
    TraceEvent curr = new TraceEvent();
    curr.rule = r;
    IdentityHashMap<Object, Object> origToCopy =
      new IdentityHashMap<Object, Object>();
    curr.lastMatch = root.copyIntermediate(origToCopy).getValue();
    curr.matchPoint = (DagEdge) origToCopy.get(current);
    _events.add(curr);
  }
}
