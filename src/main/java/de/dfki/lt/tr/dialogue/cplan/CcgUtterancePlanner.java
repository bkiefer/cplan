package de.dfki.lt.tr.dialogue.cplan;

import static de.dfki.lt.tr.dialogue.cplan.Constants.SECTION_SETTINGS;
import static de.dfki.lt.tr.dialogue.cplan.CcgPlannerConstants.KEY_CCG_GRAMMAR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
import de.dfki.lt.tr.dialogue.cplan.util.PairList;

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
    /*
    String grammarFile = _settings.get(KEY_CCG_GRAMMAR);
    if (grammarFile != null) {
      File ccgPath = resolvePath(projectFile, grammarFile);
      readCCGGrammar(ccgPath);
      if (_grammar != null) {
        _realizer = new Realizer(_grammar);
        _parser = new Parser(_grammar);
      }
    }
    */
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

  public String realize(DagNode dagLf) {
    String result = "";
    if (_realizer != null && dagLf != null) {
      DagEdge content = dagLf.getEdge(DagNode.getFeatureId("Content"));
      if (false && content != null) {
        dagLf = content.getValue();
      }
      LF lf = DagToLF.convertToLF(dagLf);
      // System.out.println(lf);
      Edge resEdge = _realizer.realize(lf);
      result = resEdge.getSign().getOrthography();
    }
    return result;
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

      @Override
      public void setMaximum(int max) {
        _max = max;
        logger.info(_lastPercentage + "% processed");
      }

      @Override
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

}
