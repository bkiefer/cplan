package de.dfki.lt.tr.dialogue.cplan.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PairList<K, V> implements Iterable<Pair<K, V>> {
  private List<Pair<K, V>> _impl = new ArrayList<Pair<K, V>>();

  public void add(K key, V value) {
    _impl.add(new Pair<K, V>(key, value));
  }

  public void clear() {
    _impl.clear();
  }

  public Iterator<Pair<K, V>> iterator() {
    return _impl.iterator();
  }

  public V find(K key) {
    for(Pair<K, V> e : _impl) {
      if (e.getFirst().equals(key)) return e.getSecond();
    }
    return null;
  }

  public int size() {
    return _impl.size();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    boolean first = true;
    for(Pair p : _impl) {
      if (! first) sb.append(", ");
      else first = false;
      sb.append(p);
    }
    sb.append(']');
    return sb.toString();
  }
}
