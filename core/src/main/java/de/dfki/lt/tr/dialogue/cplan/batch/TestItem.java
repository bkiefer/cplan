package de.dfki.lt.tr.dialogue.cplan.batch;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.LFParser;
import de.dfki.lt.tr.dialogue.cplan.Lexer;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.batch.BatchTest.BatchType;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public abstract class TestItem {
  public Position position;

  public abstract void write(Writer out, ResultItem res, String sep, String nl)
      throws IOException;

  public abstract Object input();

  public abstract Set<?> output();

  protected String toString(DagNode dag) {
    return dag == null ? "null" : dag.toString();
  }

  public abstract List<TestItem> readNext(Reader in, Lexer l, LFParser parser)
      throws IOException;

  public abstract ResultItem execute(UtterancePlanner planner, int i, BatchType type);
}