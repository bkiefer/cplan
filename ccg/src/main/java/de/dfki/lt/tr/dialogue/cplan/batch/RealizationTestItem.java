package de.dfki.lt.tr.dialogue.cplan.batch;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class RealizationTestItem extends TestItem {
  private static Logger logger = LoggerFactory.getLogger(RealizationTestItem.class);

  public DagNode lf;
  public Set<String> answers;

  public RealizationTestItem(DagNode d, Set<String> a, Position l) {
    lf = d;
    answers = a;
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
    Set<String> answers = new HashSet<String>();
    do {
      String nextSentence = l.readLine();
      if (nextSentence.isEmpty())
        break;
      answers.add(nextSentence);
    } while (true);
    List<DagNode> nextLfs = parser.getResultLFs();
    for (DagNode nextLf : nextLfs) {
      result.add(new RealizationTestItem(nextLf, answers, pos));
    }
    return result;
  }


  public boolean validGenericAnswer(String in) {
    String red = in.replaceAll("[:;,?. ]", "");
    return ! (red.isEmpty() || "and".equals(red));
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
      generated = ((CcgUtterancePlanner)planner).doRealization(result);
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
    }
    if (resultStatus == Status.GOOD &&
        ! ((this.answers.contains("*") && validGenericAnswer(generated))
           || this.answers.contains(generated))) {
      resultStatus = Status.BAD;
    }

    return new ResultItem(i, result, generated, resultStatus, warnings, type);
  }

}