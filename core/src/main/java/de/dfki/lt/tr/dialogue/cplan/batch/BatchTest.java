package de.dfki.lt.tr.dialogue.cplan.batch;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.LFParser;
import de.dfki.lt.tr.dialogue.cplan.Lexer;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;

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

  public static final DagNode ASTERISK = new DagNode("*");


  /** The planner to test */
  private UtterancePlanner _planner;

  /** The file to read test items from */
  private File _batchFile;

  private ProgressListener _progressListener;

  private List<TestItem> _items = new ArrayList<TestItem>();

  private List<ResultItem> _bad = new ArrayList<ResultItem>();
  private List<ResultItem> _good = new ArrayList<ResultItem>();

  public BatchTest(UtterancePlanner planner, BatchType realizationTest) {
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
    return first == null ? "*" : first.toString();
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

  public void reload() throws IOException {
    _items.clear();
    Reader in = new FileReader(_batchFile);
    Lexer l = new Lexer(_batchFile.getCanonicalPath(), in);
    LFParser parser = new LFParser(l);
    parser.setExtMode(true);
    // A factory method returning the right type to read in the items.
    TestItem ti = _planner.newTestItem(_realizationTest);
    do {
      _items.addAll(ti.readNext(in, l, parser));
    } while (!l.atEOF());
  }


  public void runBatch() {
    _bad.clear();
    _good.clear();
    int i = 0;
    UtterancePlanner planner;
    try {
      planner = _planner.copy();
    } catch (IOException ioex) {
      logger.error("Can not initialize new processor for batch processing");
      return;
    }

    for (TestItem item : _items) {
      ResultItem res = item.execute(planner, i, _realizationTest);
      if (_progressListener != null) {
        _progressListener.progress(i);
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
