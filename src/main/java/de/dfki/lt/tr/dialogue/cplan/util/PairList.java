package de.dfki.lt.tr.dialogue.cplan.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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

  @Override
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
}
