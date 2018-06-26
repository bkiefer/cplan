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
 *  @field input    a sentence to parse
 *  @field results  a set of DagNodes representing possible semantics results.
 *                  If "*" is in the set, the test will be successful if the
 *                  string was parseable
 *  @field position the location in the batch file where the test item was
 *                  defined
 */
public class ParsingTestItem extends TestItem {
  private static Logger logger = LoggerFactory.getLogger(ParsingTestItem.class);

  public String input;
  public Set<DagNode> results;

  public ParsingTestItem(String i, Set<DagNode> r, Position l) {
    input = i;
    results = r;
    position = l;
  }

  @Override
  public void write(Writer out, ResultItem res, String sep, String nl)
      throws IOException {
    // write result item slots
    out.write(res.itemStatus.toString());
    out.write(sep);
    out.write(input);
    out.write(sep);
    out.write(BatchTest.showSet(results));
    out.write(sep);
    out.write(toString(res.outputLf));
    out.write(sep);
    out.write(res.realized);
    out.write(nl);
  }

  @Override
  public Object input() { return input; }

  @Override
  public Set<?> output() { return results; }

  @Override
  public List<TestItem> readNext(Reader in, Lexer l, LFParser parser)
      throws IOException {
    List<TestItem> result = new ArrayList<>();
    Position pos = l.getStartPos();
    String nextSentence = l.readLine();
    while (nextSentence.isEmpty() && !l.atEOF()) {
      l.getStartPos();
      nextSentence = l.readLine();
    }
    if (nextSentence.isEmpty() && l.atEOF()) {
      return result;
    }
    Set<DagNode> answers = new CopyOnWriteArraySet<DagNode>();

    if (l.peek() == '*') {
      answers.add(BatchTest.ASTERISK);
      l.readLine();
    } else {
      while (l.peek() == '@') {
        parser.reset();
        Position lpos = l.getStartPos();
        boolean good = parser.parse();
        if (!good) {
          logger.warn("Skip wrong LF at " + lpos);
          break;
        } else {
          answers.addAll(parser.getResultLFs());
        }
      }
    }
    result.add(new ParsingTestItem(nextSentence, answers, pos));
    return result;
  }

  @Override
  public ResultItem execute(UtterancePlanner planner, int i, BatchType type) {
    StringWriter sw = new StringWriter();
    String generated = "";
    //Appender sentinel = new WriterAppender(new SimpleLayout(), sw);
    Logger plannerLogger = LoggerFactory.getLogger("UtterancePlanner");
    //plannerLogger.addAppender(sentinel);
    Status resultStatus = Status.GOOD;
    boolean warnings = false;
    DagNode result = null;
    try {
      result = ((CcgUtterancePlanner)planner).analyze(this.input);
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
      // plannerLogger.removeAppender(sentinel);
    } //
    if (result == null) {
      resultStatus = Status.BAD;
    } else {
      DagEdge prop = result.getEdge(DagNode.PROP_FEAT_ID);
      if (prop != null
          && prop.getValue().getTypeName().equals("No parse for sentence")) {
        resultStatus = Status.BAD;
      }
    }
    if (resultStatus == Status.GOOD &&
        ! (this.results.contains(BatchTest.ASTERISK) || this.results.contains(result))) {
      resultStatus = Status.BAD;
    }

    return new ResultItem(i, result, this.input, resultStatus, warnings, type);
  }

}
