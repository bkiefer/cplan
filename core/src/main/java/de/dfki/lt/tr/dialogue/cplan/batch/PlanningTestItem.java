package de.dfki.lt.tr.dialogue.cplan.batch;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.*;
import de.dfki.lt.tr.dialogue.cplan.batch.BatchTest.BatchType;
import de.dfki.lt.tr.dialogue.cplan.batch.BatchTest.Status;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

/** A test item, consisting of:
 *  @field lf       a logical form: the input to the processing
 *  @field answers  a set of strings: the possible answers. If "*" is in the
 *                  set, the test will be successful if a non-empty string
 *                  could be realized.
 *  @field position the location in the batch file where the test item was
 *                  defined
 */
public class PlanningTestItem extends TestItem {

  private static Logger logger = LoggerFactory.getLogger(BatchTest.class);

  public DagNode lf;
  public Set<DagNode> answers;

  public PlanningTestItem(DagNode d, DagNode a, Position l) {
    lf = d;
    answers = new CopyOnWriteArraySet<DagNode>();
    answers.add(a);
    position = l;
  }

  @Override
  public void write(Writer out, ResultItem res, String sep, String nl)
      throws IOException {
    // write result item slots
    out.write(res.itemStatus.toString());
    out.write(sep);
    out.write(toString(lf));
    out.write(sep);
    out.write(BatchTest.showSet(answers));
    out.write(sep);
    out.write(toString(res.outputLf));
    out.write(sep);
    out.write(res.realized);
    out.write(nl);
  }

  @Override
  public Object input() { return lf; }

  @Override
  public Set<?> output() { return answers; }

  @Override
  public List<TestItem> readNext(Reader in, Lexer l, LFParser parser)
      throws IOException {
    List<TestItem> result = new ArrayList<>();
    parser.reset();
    boolean good = parser.parse();
    Position pos = l.getStartPos();
    if (!good) {
      logger.warn("Skip wrong LF at " + pos);
      return result;
    }
    List<DagNode> nextLfs = parser.getResultLFs();
    int nextChar = l.peek();
    if (nextChar == '*') {
      for (DagNode nextLf : nextLfs)
        result.add(new PlanningTestItem(nextLf, null, pos));
      l.readLine();
      return result;
    }
    pos = l.getStartPos();
    good = parser.parse();
    if (!good) {
      logger.warn("Skip wrong LF at " + pos);
      return result;
    }
    List<DagNode> answerLfs = parser.getResultLFs();
    for (DagNode nextLf : nextLfs)
      for (DagNode answerLf : answerLfs)
        result.add(new PlanningTestItem(nextLf, answerLf, pos));
    return result;
  }

  @Override
  public ResultItem execute(UtterancePlanner planner, int i, BatchType type) {
    String generated = "";
    StringWriter sw = new StringWriter();
    //Appender sentinel = new WriterAppender(new SimpleLayout(), sw);
    Logger plannerLogger = LoggerFactory.getLogger("UtterancePlanner");
    //plannerLogger.addAppender(sentinel);
    Status resultStatus = Status.GOOD;
    boolean warnings = false;
    DagNode result = null;
    try {
      result = planner.process(this.lf);
    }
    catch (NullPointerException ex) {
      generated = "**** FAILURE ****";
      resultStatus = Status.BAD;
    }
    catch (PlanningException ex) {
      generated = "**** PLANEXCEPTION ****";
      resultStatus = Status.BAD;
    }
    finally {
      try {
        sw.close();
      } catch (IOException e) { // will never happen
      }
      warnings = ! sw.toString().isEmpty();
      //plannerLogger.removeAppender(sentinel);
    } //
    Object expected = this.answers.iterator().next();
    if (expected != null && (result == null || ! expected.equals(result))) {
      resultStatus = Status.BAD;
    }

    return new ResultItem(i, result, generated, resultStatus, warnings, type);
  }
}