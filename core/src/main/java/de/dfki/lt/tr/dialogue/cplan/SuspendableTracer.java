package de.dfki.lt.tr.dialogue.cplan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public abstract class SuspendableTracer implements RuleTracer {
  public static final int MATCHING = 1;
  public static final int MODIFICATION = MATCHING << 1;
  public static final int ALL = MATCHING | MODIFICATION;

  public interface SuspendListener {
    public abstract void suspended(boolean isSuspended);
  }

  private List<SuspendListener> _listeners = new ArrayList<SuspendListener>();

  private  Semaphore _suspendSemaphore = new Semaphore(0);

  private boolean _suspended = false;

  public void addListener(SuspendListener l) {
    _listeners.add(l);
  }

  public void removeListener(SuspendListener l) {
    _listeners.remove(l);
  }

  private void informListeners(boolean value) {
    for(SuspendListener l : _listeners) {
      l.suspended(value);
    }
  }

  public void setTracing(int bitmask) { }

  public void traceBeforeApplication(DagEdge root, DagEdge current,
      BasicRule r, Bindings bindings) {}

  public void suspendProcessing() {
    _suspended = true;
  }

  public void continueProcessing() {
    _suspendSemaphore.release();
    _suspended = false;
  }

  public boolean isSuspended() {
    return _suspended;
  }

  protected void checkSuspended() {
    if (_suspended) {
      informListeners(true);
      try {
        _suspendSemaphore.acquire();
      }
      catch (InterruptedException ex) {
      }
      finally {
        informListeners(false);
      }
    }
  }
}
