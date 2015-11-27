package de.dfki.lt.tr.dialogue.cplan;

public interface ProgressListener {

  public void setMaximum(int max);

  public void progress(int count);
}
