package de.dfki.lt.tr.dialogue.cplan;

import static de.dfki.lt.tr.dialogue.cplan.CcgPlannerConstants.KEY_CCG_GRAMMAR;
import static de.dfki.lt.tr.dialogue.cplan.Constants.SECTION_SETTINGS;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import de.dfki.lt.tr.dialogue.cplan.BatchTest.BatchType;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.RealizationTestItem;
import de.dfki.lt.tr.dialogue.cplan.BatchTest.ResultItem;
import de.dfki.lt.tr.dialogue.cplan.functions.DeterministicRandomFunction;
import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.util.PairList;
import opennlp.ccg.grammar.Grammar;
import opennlp.ccg.hylo.HyloHelper;
import opennlp.ccg.hylo.Nominal;
import opennlp.ccg.parse.ParseException;
import opennlp.ccg.parse.Parser;
import opennlp.ccg.realize.Edge;
import opennlp.ccg.realize.Realizer;
import opennlp.ccg.synsem.Category;
import opennlp.ccg.synsem.LF;
import opennlp.ccg.synsem.Sign;

public class CcgUtterancePlanner extends UtterancePlanner {

  /** The CCG grammar, if specified and loadable */
  protected Grammar _grammar;

  /** The File the CCG grammar was loaded from, to avoid reloading if this has
   *  not changed.
   */
  protected File _lastGrammarLocation;

  /** A CCG realizer instance, if a grammar has been loaded */
  protected Realizer _realizer = null;

  /** A CCG parser instance, if a grammar has been loaded */
  protected Parser _parser = null;

  public CcgUtterancePlanner() { super(); }

  public CcgUtterancePlanner(CcgUtterancePlanner toClone)
      throws FileNotFoundException, IOException {
    super();
    readProjectFile(toClone._projectFile);
  }

  private int _dUnitTypeId;
  // , _markerTypeId;
  private short _firstFeatId, _nextFeatId, _modeFeatId;

  private static HashMap<String, String> _markers;
  private static HashMap<String, String> _modes;

  static {
    _markers = new HashMap<String, String>();
    String[][] mrks = {
        {"dot", "."}, {"comma", ","}, {"question", "?"}, {"exclamation", "!"}
    };
    for (String[] m : mrks) _markers.put(m[0], m[1]+" ");
    _modes = new HashMap<String, String>();
    String[][] mds = {
        {"affirmative", "."}, {"coordinative", ","}, {"interrogative", "?"},
        {"exclamative", "!"}, {"explicative", ":"}
    };
    for (String[] m : mds) _modes.put(m[0], m[1]);
  }

  private void readCCGGrammar(File grammarFile) throws IOException {
    if (_lastGrammarLocation == null ||
        ! _lastGrammarLocation.equals(grammarFile)) {
      try {
        _grammar = new Grammar(grammarFile.getCanonicalPath());
        DagNode.setHierarchy(new CcgHierarchy(_grammar));
        _lastGrammarLocation = grammarFile;
      } catch (IOException e) {
        _lastGrammarLocation = null;
        _grammar = null;
        logger.error("CCG grammar could not be loaded: " + e);
        throw e;
      }
    }
  }

  protected void finishProject(
      File projectFile,
      PairList<String, PairList<String, String>> project,
      HashMap<String, String> settings,
      PairList<String, List<File>> ruleSections)
          throws FileNotFoundException, IOException {
    PairList<String, String> localSettings = project.find(SECTION_SETTINGS);
    if (localSettings != null) {
      String grammarFile = localSettings.find(KEY_CCG_GRAMMAR);
      if (grammarFile != null) {
        settings.put(KEY_CCG_GRAMMAR, grammarFile);
        File ccgPath = resolvePath(projectFile, grammarFile);
        readCCGGrammar(ccgPath);
        if (_grammar != null) {
          _realizer = new Realizer(_grammar);
          _parser = new Parser(_grammar);
        }
      }
    }
    super.finishProject(projectFile, project, settings, ruleSections);
    _dUnitTypeId = DagNode.getTypeId("d-units");
    // _markerTypeId = DagNode.getTypeId("marker");
    _firstFeatId = DagNode.getFeatureId("First");
    _nextFeatId = DagNode.getFeatureId("Next");
    _modeFeatId = DagNode.getFeatureId("Mode");
    }

  /** Read the project file for this content planner. In addition to the super
   *  class' method, read the specified CCG grammar, if any.
   *
   *  @see UtterancePlanner
   */
  @Override
  public void readProjectFile(File projectFile)
  throws FileNotFoundException, IOException {
    // reset global status
    _grammar = null;
    super.readProjectFile(projectFile);
  }

  /** Return true if this utterance planner has a valid grammar, and should
   *  therefore be able to realize properly.
   */
  public boolean hasValidGrammar() {
    return _grammar != null;
  }

  public String doRealization(DagNode dag) {
    String result;
    // handle "canned text" output.
    DagEdge edge = dag.getEdge(DagNode.TYPE_FEAT_ID);
    if (edge != null && edge.getValue().getTypeName().equals("canned")){
      edge = dag.getEdge(DagNode.getFeatureId("string"))
      .getValue().getEdge(DagNode.PROP_FEAT_ID);
      result = edge.getValue().getTypeName();
    } else {
      result = realize(dag);
    }
    return result;
  }

  /** traverse the d-lists structure in preorder and collect all base element */
  void collectDUnits(DagNode dag, List<DagNode> units) {
    if (dag == null) return;
    DagNode e = dag.getValue(DagNode.TYPE_FEAT_ID);
    if (e != null) {
      int type = e.getType();
      if (type == _dUnitTypeId) {
        collectDUnits(dag.getValue(_firstFeatId), units);
        collectDUnits(dag.getValue(_nextFeatId), units);
      } else {
        units.add(dag);
      }
    } else {
      logger.warn("Dag without type edge: " + dag);
      units.add(dag);
    }
  }


  public String realizeOld(DagNode dagLf) {
    String result = "";
    if (_realizer != null && dagLf != null) {
      /*
      DagEdge content = dagLf.getEdge(DagNode.getFeatureId("Content"));
      if (content != null) {
        dagLf = content.getValue();
      }
      */
      LF lf = DagToLF.convertToLF(dagLf);
      Edge resEdge = _realizer.realize(lf);
      result = resEdge.getSign().getOrthography();
    }
    return result;
  }

  public String realize(DagNode dagLf) {
    StringBuilder result = new StringBuilder();
    if (_realizer != null && dagLf != null) {
      List<DagNode> units = new ArrayList<DagNode>(5);
      collectDUnits(dagLf, units);
      for (DagNode d : units) {
        DagNode e = d.getValue(DagNode.TYPE_FEAT_ID);
        if (e != null && e.getTypeName().equals("canned")) {
             /*{
          if (e.getType() == _markerTypeId) {
            String what = d.getValue(DagNode.PROP_FEAT_ID).getTypeName();
            String marker = _markers.get(what);
            if (marker == null) {
              logger.warn("Unknown marker: " + what);
            } else {
              sub = marker;
            }
          } else {*/
          DagEdge edge = d.getEdge(DagNode.getFeatureId("string"))
          .getValue().getEdge(DagNode.PROP_FEAT_ID);
          result.append(edge.getValue().getTypeName());
        } else {
          LF lf = DagToLF.convertToLF(d);
          // System.out.println(lf);
          Edge resEdge = _realizer.realize(lf);
          result.append(resEdge.getSign().getOrthography());
        }

        DagNode mode = d.getValue(_modeFeatId);
        if (mode != null) {
          String what = mode.getValue(DagNode.PROP_FEAT_ID).getTypeName();
          String marker = _modes.get(what);
          if (marker == null) {
            logger.warn("Unknown marker: " + what);
          } else {
            result.append(marker);
          }
        }
        result.append(" ");
      }
    }
    return result.toString();
  }

  public DagNode analyze(String input) {
    DagNode result = null;
    if (_parser != null && ! input.isEmpty()) {
      try {
        List<Sign> res = analyzeGetSigns(input);
        if (! res.isEmpty()) {
          Category cat = res.get(0).getCategory();
          LF convertedLF = null;
          if (cat.getLF() != null) {
            cat = cat.copy();
            Nominal index = cat.getIndexNominal();
            convertedLF = HyloHelper.compactAndConvertNominals(cat.getLF(), index);
          }

          //if (convertedLF != null) String s = convertedLF.prettyPrint(" ");

          result = DagToLF.convertToDag(convertedLF);
        }
      }
      catch (ParseException pex) {
        result = new DagNode(DagNode.TOP_ID);
        result.addEdge(DagNode.PROP_FEAT_ID,
            new DagNode("No parse for sentence"));
      }
    }
    return result;
  }

  public List<Sign> analyzeGetSigns(String input) throws ParseException {
    List<Sign> res = null;
    if (_parser != null && ! input.isEmpty()) {
      _parser.parse(input);
      res = _parser.getResult();
    }
    return res;
  }

  /* Load a batch test file for further processing */
  public BatchTest loadBatch(File batchFile, BatchTest.BatchType realizationTest)
      throws IOException {
    BatchTest bt = new BatchTest(this, realizationTest);
    bt.init(batchFile);
    return bt;
  }

  /** Run a batch planning + generation test */
  public BatchTest batchProcess(File batchFile,
      BatchTest.BatchType realizationTest)
      throws IOException {
    BatchTest bt = loadBatch(batchFile, realizationTest);
    bt.setProgressListener(new ProgressListener() {
      private int _lastPercentage = 0;
      private int _max;
      private static final int step = 10;

      public void setMaximum(int max) {
        _max = max;
        logger.info(_lastPercentage + "% processed");
      }

      public void progress(int count) {
        int newPercentage = (int) ((100.0 * count) / _max);
        if (newPercentage >= _lastPercentage + step) {
          _lastPercentage = newPercentage;
          logger.info(_lastPercentage + "% processed");
        }
      }
    });
    bt.runBatch();
    return bt;
  }


  private static final int MAX_EQUAL_TURNS = 10000;


  /** Systematically generate all possibilities for the test items in batchFile,
   *  writing the results to out.
   */
  public void batchGenerateAllPossibilities(File batchFile,
      Writer out, boolean useCcgRealizer)
  throws IOException {
    final String nl = System.getProperty("line.separator");
    Pattern punctRegex = Pattern.compile("\\s*(?:[;:,.?]\\s*)*([;:,.?])");
    Pattern spaceRegex = Pattern.compile("\\s+");

    DeterministicRandomFunction drf = new DeterministicRandomFunction();
    FunctionFactory.register(drf, this);

    // this.setTracing(new MiniLogTracer(RuleTracer.ALL));
    Realizer realizerSave = _realizer;
    if (! useCcgRealizer) {
      _realizer = null; // Don't attempt to do CCG realization
    }

    BatchTest bt = this.loadBatch(batchFile, BatchType.GENERATION);
    for (int i = 0; i < bt.itemSize(); ++i) {
      drf.newInput();
      RealizationTestItem item = (RealizationTestItem) bt.getItem(i);
      HashSet<String> sents = new HashSet<String>();
      out.append("### ").append(item.lf.toString()).append(nl);
      out.flush();
      int equalTurns = 0;
      while (drf.newRound() && ++equalTurns < MAX_EQUAL_TURNS) {
        ResultItem res = bt.realizeOneItem(item, i);
        if (res.itemStatus == BatchTest.Status.GOOD) {
          String result = punctRegex.matcher(res.realized).replaceAll("$1");
          result = spaceRegex.matcher(result).replaceAll(" ");
          if (result.isEmpty()) break;
          if (! sents.contains(result)) {
            equalTurns = 0;
            sents.add(result);
            out.append(result).append(nl);
            out.flush();
          }
        }
      }
      System.err.println(sents.size()
          + ((equalTurns >= MAX_EQUAL_TURNS)?" *":""));
    }
    _realizer = realizerSave;
  }

}
