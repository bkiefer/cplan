package de.dfki.lt.tr.dialogue.cplan;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

/** This class stores bindings of global and local variables during matching
 *  and modification phases of the graph transformation algorithm
 */
public class Bindings {

  private class Binding {
    int status;
    String name;
    DagEdge edge;

    public Binding(int s, String n, DagEdge value) {
      status = s;
      name = n;
      edge = value;
    }

    @Override
    public String toString() {
      return "{" + status + ", " + name + ", " + edge + "}";
    }
  }


  private Stack<Binding> _bindings;

  /** to count the global bindings stored in this structure */
  private boolean _globalBindingsChanged;

  /** LOCAL bindings are only valid during one rule match/application */
  public static final int LOCAL = 0;
  /** RIGHTLOCAL bindings are only valid during one rule match/application AND
   *  are only allowed on the right hand side. They are mostly used to make
   *  feeding complex arguments into functions more nicely.
   */
  public static final int RIGHTLOCAL = 2;
  /** GLOBAL bindings are valid from the moment they are established until the
   *  end of the fixpoint computation.
   */
  public static final int GLOBAL = 1;
  /** Absolute bindings are nominal names, not variables at all */
  public static final int ABSOLUTE = 3;


  public Bindings() {
    _bindings = new Stack<Binding>();
    _globalBindingsChanged = false;
  }

  public Binding findBinding(String name, int status) {
    for (Binding triple : _bindings) {
      if ((triple.status == status)
          && (triple.name).equals(name))
        return triple;
    }
    return null;
  }

  /** Establish a binding between a variable and a value */
  public void bind(String name, DagEdge value, int status) {
    Binding res = findBinding(name, status);
    if (res == null) {
      if (status == GLOBAL) {
        _globalBindingsChanged = true;
      }
      Binding newTriple = new Binding(status, name, value);
      _bindings.push(newTriple);
    } else {
      // only global variables may be overwritten
      if (status == GLOBAL) {
        if (! res.edge.equals(value)) {
          _globalBindingsChanged = true;
          res.edge = value;
        }
      } else {
        throw new IllegalAccessError("Local Variable " + name + " already bound");
      }
    }
  }

  /** Return the bound value of variable \p name, if there is one, or \c null */
  public DagEdge getBinding(String name, int status) {
    Binding triple = findBinding(name, status);
    return (triple == null ? null : triple.edge);
  }

  /** Reset the local (left and right) variable bindings. This function must be
   *  called before processing a rule to remove old local bindings.
   */
  public void resetLocalBindings() {
    Iterator<Binding> it = _bindings.iterator();
    while (it.hasNext()) {
      Binding triple = it.next();
      int status = triple.status;
      if (status == LOCAL || status == RIGHTLOCAL) {
        it.remove();
      }
    }
  }

  /** Copy all local bindings from this to target
   *
   *  This method does not care about RIGHTLOCAL bindings because they don't
   *  have to be transferred from the left to the right side.
   */
  private void copyLocalBindingsTo(Bindings target) {
    for (Binding triple : _bindings) {
      if (triple.status == LOCAL) {
        target._bindings.push(triple);
      }
    }
  }

  /** Transfer the local bindings of this Bindings object into a new Bindings,
   *  and delete the current local bindings
   *
   *  This method does not care about RIGHTLOCAL bindings because they don't
   *  have to be transferred from the left to the right side.
   */
  public Bindings transferLocalBindings() {
    Bindings result = new Bindings();
    copyLocalBindingsTo(result);
    resetLocalBindings();
    return result;
  }

  /** Overwrite the local bindings of \c this with those of \a bindings
   *
   *  This method does not care about RIGHTLOCAL bindings because they don't
   *  have to be transferred from the left to the right side.
   */
  public void restoreLocalBindings(Bindings bindings) {
    resetLocalBindings();
    bindings.copyLocalBindingsTo(this);
  }

  /** Return the number of global bindings in this structure */
  public boolean globalBindingsChanged() {
    boolean result = _globalBindingsChanged;
    _globalBindingsChanged = false;
    return result;
  }

  /** Return a List View of all (global) bindings */
  public SortedMap<String, DagEdge> getGlobalBindings() {
    final String[] suff = { "#", "##", "###" } ;
    SortedMap<String, DagEdge> result = new TreeMap<String, DagEdge>();
    for (Binding triple : _bindings) {
      if (! triple.name.equals("#")) {
      //if ((Integer)triple[0] == GLOBAL) {
        result.put(suff[triple.status] + triple.name ,
            triple.edge);
      //}
      }
    }
    return result;
  }

  /** Get a pointer that points past the last binding */
  public int getLevel() {
    return _bindings.size();
  }

  /** This function is called when a local disjunction match branch failed.
   *  Since no global bindings can be established on the left hand side, only
   *  local variables can be retracted here.
   */
  public void retractToLevel(int level) {
    while (_bindings.size() > level) {
      Binding triple = _bindings.pop();
      assert(triple.status != GLOBAL);
    }
  }

  /** Do a deep copy of this Bindings object */
  public Bindings copy(IdentityHashMap<Object, Object> origToCopy) {
    Bindings result = new Bindings();
    for (Binding triple : _bindings) {
      DagEdge edge = (DagEdge) origToCopy.get(triple.edge);
      // if the edge is not part of the input feature structure
      if (edge == null) {
        edge = triple.edge.copyIntermediate(origToCopy);
      }
      Binding newTriple = new Binding(triple.status, triple.name, edge);
      result._bindings.push(newTriple);
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_globalBindingsChanged ? "[*" : "[");
    for (Binding b : _bindings) {
      sb.append("\n ").append(b);
    }
    sb.append("]");
    return sb.toString();
  }
}
