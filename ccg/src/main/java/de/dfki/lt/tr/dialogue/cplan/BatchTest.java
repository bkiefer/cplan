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
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.util.Position;

/** A class performing a batch test on a planner, reading in batch files,
 *  performing the test run, and storing the input and result data.
 */
public class BatchTest {

  private static Logger logger = LoggerFactory.getLogger(BatchTest.class);

  public enum Status { BAD , GOOD };

  /** PLANNING_ALL generates all variants, not attempting CCG generation,
   *  GENERATION_ALL generates all variants with CCG generation.
   *  The other three do one-shot batch processing
   */
  public enum BatchType {
    PARSING, GENERATION, PLANNING, GENERATION_ALL, PLANNING_ALL, NONE
  };

  private BatchType _realizationTest = BatchType.GENERATION;

  private static final DagNode ASTERISK = new DagNode("*");

  public static abstract class TestItem {
    public Position position;

    public abstract void write(Writer out, ResultItem res, String sep, String nl)
        throws IOException;

    public abstract Object input();

    public abstract Set<?> output();

    protected String toString(DagNode dag) {
      return dag == null ? "null" : dag.toString();
    }
  }


  /** A test item, consisting of:
   *  @field lf       a logical form: the input to the processing
   *  @field answers  a set of strings: the possible answers. If "*" is in the
   *                  set, the test will be successful if a non-empty string
   *                  could be realized.
   *  @field position the location in the batch file where the test item was
   *                  defined
   */
  public static class RealizationTestItem extends TestItem {
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
      out.write(showSet(answers));
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
  }

  /** A test item, consisting of:
   *  @field input    a sentence to parse
   *  @field results  a set of DagNodes representing possible semantics results.
   *                  If "*" is in the set, the test will be successful if the
   *                  string was parseable
   *  @field position the location in the batch file where the test item was
   *                  defined
   */
  public static class ParsingTestItem extends TestItem {
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
      out.write(showSet(results));
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
  }

  /** A test item, consisting of:
   *  @field lf       a logical form: the input to the processing
   *  @field answers  a set of strings: the possible answers. If "*" is in the
   *                  set, the test will be successful if a non-empty string
   *                  could be realized.
   *  @field position the location in the batch file where the test item was
   *                  defined
   */
  public static class PlanningTestItem extends TestItem {
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
      out.write(showSet(answers));
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
    public BatchType realizationResult;

    public ResultItem(int t, DagNode out, String r, Status s,
      boolean w, BatchType real) {
      testItemIndex = t;
      outputLf = out;
      realized = r;
      itemStatus = s;
      warning = w;
      realizationResult = real;
    }
  }

  /** The planner to test */
  private CcgUtterancePlanner _planner;

  /** The file to read test items from */
  private File _batchFile;

  private ProgressListener _progressListener;

  private List<TestItem> _items = new ArrayList<TestItem>();

  private List<ResultItem> _bad = new ArrayList<ResultItem>();
  private List<ResultItem> _good = new ArrayList<ResultItem>();

  public BatchTest(CcgUtterancePlanner planner, BatchType realizationTest) {
    _planner = planner;
    _realizationTest = realizationTest;
  }

  public void setProgressListener(ProgressListener l) {
    _progressListener = l;
    _progressListener.setMaximum(itemSize());
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

  public static String showSet(Set<?> strings) {
    if (strings.isEmpty())
      return "[]";
    Iterator<?> it = strings.iterator();
    StringBuilder sb = new StringBuilder();
    Object first = it.next();
    if (it.hasNext()) {
      sb.append("[ ").append(first);
      while (it.hasNext()) {
        sb.append(" | ").append(it.next());
      }
      sb.append(" ]");
      return sb.toString();
    }
    return first.toString();
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
        testitem.write(out, res, sep, nl);
        out.flush();
      }
    }
    out.close();
  }

  public void init(File batchFile) throws IOException {
    _batchFile = batchFile;
    reload();
  }

  void readNextRealizationItem(Reader in, Lexer l, LFParser parser)
      throws IOException {
    parser.reset();
    boolean good = parser.parse();
    Position pos = l.getStartPos();
    if (!good) {
      logger.warn("Skip wrong LF at " + pos);
      return;
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
      _items.add(new RealizationTestItem(nextLf, answers, pos));
    }
  }

  void readNextParsingItem(Reader in, Lexer l, LFParser parser)
      throws IOException {
    Position pos = l.getStartPos();
    String nextSentence = l.readLine();
    while (nextSentence.isEmpty() && !l.atEOF()) {
      l.getStartPos();
      nextSentence = l.readLine();
    }
    if (nextSentence.isEmpty() && l.atEOF()) {
      return;
    }
    Set<DagNode> answers = new CopyOnWriteArraySet<DagNode>();

    if (l.peek() == '*') {
      answers.add(ASTERISK);
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
    _items.add(new ParsingTestItem(nextSentence, answers, pos));
  }

  void readNextPlanningItem(Reader in, Lexer l, LFParser parser)
      throws IOException {
    parser.reset();
    boolean good = parser.parse();
    Position pos = l.getStartPos();
    if (!good) {
      logger.warn("Skip wrong LF at " + pos);
      return;
    }
    List<DagNode> nextLfs = parser.getResultLFs();
    pos = l.getStartPos();
    good = parser.parse();
    if (!good) {
      logger.warn("Skip wrong LF at " + pos);
      return;
    }
    List<DagNode> answerLfs = parser.getResultLFs();
    for (DagNode nextLf : nextLfs)
      for (DagNode answerLf : answerLfs)
        _items.add(new PlanningTestItem(nextLf, answerLf, pos));
  }

  public void reload() throws IOException {
    _items.clear();
    Reader in = new FileReader(_batchFile);
    Lexer l = new Lexer(_batchFile.getCanonicalPath(), in);
    LFParser parser = new LFParser(l);
    parser.setExtMode(true);
    do {
      switch (_realizationTest) {
      case GENERATION: readNextRealizationItem(in, l, parser); break;
      case PARSING:    readNextParsingItem(in, l, parser); break;
      case PLANNING:   readNextPlanningItem(in, l, parser); break;
      }
    } while (!l.atEOF());
  }


  public boolean validGenericAnswer(String in) {
    String red = in.replaceAll("[:;,?. ]", "");
    return ! (red.isEmpty() || "and".equals(red));
  }

  public ResultItem realizeOneItem(RealizationTestItem item, int i) {
    DagNode result = _planner.process(item.lf);
    String generated = "";
    StringWriter sw = new StringWriter();
    //Appender sentinel = new WriterAppender(new SimpleLayout(), sw);
    Logger plannerLogger = LoggerFactory.getLogger("UtterancePlanner");
    //plannerLogger.addAppender(sentinel);
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
      //plannerLogger.removeAppender(sentinel);
    }
    if (resultStatus == Status.GOOD &&
        ! ((item.answers.contains("*") && validGenericAnswer(generated))
           || item.answers.contains(generated))) {
      resultStatus = Status.BAD;
    }

    if (_progressListener != null) {
      _progressListener.progress(i);
    }

    return new ResultItem(i, result, generated, resultStatus, warnings,
        _realizationTest);
  }

  public ResultItem parseOneItem(ParsingTestItem item, int i) {
    StringWriter sw = new StringWriter();
    String generated = "";
    //Appender sentinel = new WriterAppender(new SimpleLayout(), sw);
    Logger plannerLogger = LoggerFactory.getLogger("UtterancePlanner");
    //plannerLogger.addAppender(sentinel);
    Status resultStatus = Status.GOOD;
    boolean warnings = false;
    DagNode result = null;
    try {
      result = _planner.analyze(item.input);
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
    }	//
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
        ! (item.results.contains(ASTERISK) || item.results.contains(result))) {
      resultStatus = Status.BAD;
    }

    if (_progressListener != null) {
      _progressListener.progress(i);
    }

    return new ResultItem(i, result, item.input, resultStatus, warnings,
        _realizationTest);
  }

  public ResultItem planOneItem(PlanningTestItem item, int i) {
    String generated = "";
    StringWriter sw = new StringWriter();
    //Appender sentinel = new WriterAppender(new SimpleLayout(), sw);
    Logger plannerLogger = LoggerFactory.getLogger("UtterancePlanner");
    //plannerLogger.addAppender(sentinel);
    Status resultStatus = Status.GOOD;
    boolean warnings = false;
    DagNode result = null;
    try {
      result = _planner.process(item.lf);
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
    if (result == null || ! item.answers.iterator().next().equals(result)) {
      resultStatus = Status.BAD;
    }

    if (_progressListener != null) {
      _progressListener.progress(i);
    }

    return new ResultItem(i, result, generated, resultStatus, warnings,
        _realizationTest);
  }


  public void runBatch() {
    _bad.clear();
    _good.clear();
    int i = 0;
    for (TestItem item : _items) {
      ResultItem res = null;
      switch(_realizationTest) {
      case GENERATION: res = realizeOneItem((RealizationTestItem)item, i); break;
      case PARSING: res = parseOneItem((ParsingTestItem)item, i); break;
      case PLANNING: res = planOneItem((PlanningTestItem)item, i); break;
      }
      ++i;
      if (res.itemStatus == Status.BAD) {
        _bad.add(res);
      } else {
        _good.add(res);
      }
    }
  }

}
