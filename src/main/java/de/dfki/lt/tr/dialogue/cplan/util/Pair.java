package de.dfki.lt.tr.dialogue.cplan.util;

public class Pair<KEYTYPE, VALUETYPE> {
  private KEYTYPE _first;
  private VALUETYPE _second;

  public Pair(KEYTYPE first, VALUETYPE second) {
    _first = first;
    _second = second;
  }

  public KEYTYPE getFirst() { return _first; }

  public void setFirst(KEYTYPE key) { _first = key; }

  public VALUETYPE getSecond() { return _second; }

  public void setSecond(VALUETYPE val) { _second = val; }

  @Override
  public boolean equals(Object o) {
    if (! (o instanceof Pair)) return false;
    @SuppressWarnings("rawtypes")
    Pair p = (Pair)o;
    return p._first.equals(_first) && p._second.equals(_second);
  }

  @Override
  public int hashCode() {
    return _first.hashCode() * 2053 + _second.hashCode();
  }
}
