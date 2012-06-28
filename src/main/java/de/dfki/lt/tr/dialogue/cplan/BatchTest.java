package de.dfki.lt.tr.dialogue.cplan;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;

import de.dfki.lt.tr.dialogue.cplan.util.Position;

/** A class performing a batch test on a planner, reading in batch files,
 *  performing the test run, and storing the input and result data.
 */
public class BatchTest {

  private static Logger logger = Logger.getLogger(BatchTest.class);

  public enum Status { BAD , GOOD };

  /** A test item, consisting of:
   *  @field lf       a logical form: the input to the processing
   *  @field answers  a set of strings: the possible answers. If "*" is in the
   *                  set, the test will be successful if a non-empty string
   *                  could be realized.
   *  @field position the location in the batch file where the test item was
   *                  defined
   *
   */
  public static class TestItem {
    public DagNode lf;
    public Set<String> answers;
    public Position position;

    public TestItem(DagNode d, Set<String> a, Position l) {
      lf = d;
      answers = a;
      position = l;
    }
  }

  /** An item representing a failed test, consisting of:
   *  @field itemStatus     if this item failed or succeeded, with or without
   *                        warning
   *  @field testItemIndex  the number of the test item
   *  @field outputLf       the logical form resulting from processing
   *  @field realized       the string resulting from passing the ouput logical
   *                        form to the CCG realizer
   */
  public static class ResultItem {
    public Status itemStatus;
    public boolean warning;
    public int testItemIndex;
    public DagNode outputLf;
    public String realized;

    public ResultItem(int t, DagNode out, String r, Status s, boolean w) {
      testItemIndex = t;
      outputLf = out;
      realized = r;
      itemStatus = s;
      warning = w;
    }
  }

  /** The planner to test */
  private CcgUtterancePlanner _planner;

  /** The file to read test items from */
  private File _batchFile;

  private List<TestItem> _items = new ArrayList<TestItem>();

  private List<ResultItem> _bad = new ArrayList<ResultItem>();
  private List<ResultItem> _good = new ArrayList<ResultItem>();

  public BatchTest(CcgUtterancePlanner planner) {
    _planner = planner;
  }

  public int itemSize() {
    return _items.size();
  }

  public TestItem getItem(int index) {
    return _items.get(index);
  }

  public int badSize() {
    return _bad.size();
  }

  public ResultItem getBad(int index) {
    return _bad.get(index);
  }

  public int goodSize() {
    return _good.size();
  }

  public ResultItem getGood(int index) {
    return _good.get(index);
  }

  public String percentageGood() {
    int good = _items.size() - _bad.size();
    return "Passed: " + good + "/" + _items.size() + " (" +
    (int)((100.0 * good) / _items.size()) + "%)";
  }

  public boolean totalSuccess() {
    return _bad.isEmpty();
  }

  public static String showSet(Set<String> strings) {
    if (strings.isEmpty())
      return "[]";
    Iterator<String> it = strings.iterator();
    StringBuilder sb = new StringBuilder();
    String first = it.next();
    if (it.hasNext()) {
      sb.append("[ ").append(first);
      while (it.hasNext()) {
        sb.append(" | ").append(it.next());
      }
      sb.append(" ]");
      return sb.toString();
    }
    return first;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void save(Writer out) throws IOException {
    String nl = System.getProperty("line.separator");
    String sep = "|";
    List[] resitems = { _good, _bad };
    out.write("// Status|Input|Answers|OutputLF|OutputGeneration"+nl);
    for (List items : resitems) {
      for (ResultItem res : (List<ResultItem>)items) {
        // write test item slots
        TestItem testitem = getItem(res.testItemIndex);
        // write result item slots
        out.write(res.itemStatus.toString());
        out.write(sep);
        out.write(testitem.lf.toString());
        out.write(sep);
        out.write(showSet(testitem.answers));
        out.write(sep);
        out.write(res.outputLf.toString());
        out.write(sep);
        out.write(res.realized);
        out.write(nl);
        out.flush();
      }
    }
    out.close();
  }

  public void init(File batchFile) throws IOException {
    _batchFile = batchFile;
    reload();
  }

  public void reload() throws IOException {
    _items.clear();
    Reader in = new FileReader(_batchFile);
    Lexer l = new Lexer(_batchFile.getCanonicalPath(), in);
    ExtLFParser parser = new ExtLFParser(l);
    do {
      parser.reset();
      boolean good = parser.parse();
      Position pos = l.getStartPos();
      if (!good) {
        logger.warn("Skip wrong LF at " + pos);
        continue;
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
        _items.add(new TestItem(nextLf, answers, pos));
      }
    } while (!l.atEOF());
  }

  public ResultItem runOneItem(TestItem item, int i) {
    DagNode result = _planner.process(item.lf);
    String generated = "";
    StringWriter sw = new StringWriter();
    Appender sentinel = new WriterAppender(new SimpleLayout(), sw);
    Logger plannerLogger = Logger.getLogger("UtterancePlanner");
    plannerLogger.addAppender(sentinel);
    Status resultStatus = Status.GOOD;
    boolean warnings = false;
    try {
      generated = _planner.doRealization(result);
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
      plannerLogger.removeAppender(sentinel);
    }
    if (resultStatus == Status.GOOD &&
        ! ((item.answers.contains("*") && ! generated.isEmpty())
           || item.answers.contains(generated))) {
      resultStatus = Status.BAD;
    }

    return new ResultItem(i, result, generated, resultStatus, warnings);
  }

  public void run() {
    _bad.clear();
    _good.clear();
    int i = -1;
    for (TestItem item : _items) {
      ResultItem res = runOneItem(item, ++i);
      if (res.itemStatus == Status.BAD) {
        _bad.add(res);
      } else {
        _good.add(res);
      }
    }
  }

}
