package de.dfki.lt.tr.dialogue.cplan;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path implements Iterable<Short> {
  private List<Short> _impl = new LinkedList<Short>();

  private Environment _env;

  public Path(Environment env) {
    _env = env;
  }

  Path addToFront(String name) {
    _impl.add(0, _env.getFeatureId(name));
    return this;
  }

  public Iterator<Short> iterator() {
    return _impl.iterator();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for(short featId : _impl) {
      sb.append('<').append(_env.getFeatureName(featId)).append('>');
    }
    return sb.toString();
  }

}
