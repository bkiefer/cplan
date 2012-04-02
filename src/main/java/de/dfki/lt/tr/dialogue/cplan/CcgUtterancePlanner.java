package de.dfki.lt.tr.dialogue.cplan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class CcgUtterancePlanner extends UtterancePlanner {

  /** The CCG grammar, if specified and loadable */
  private Grammar _grammar;

  /** The File the CCG grammar was loaded from, to avoid reloading if this has
   *  not changed.
   */
  private File _lastGrammarLocation;

  /** A CCG realizer instance, if a grammar has been loaded */
  private Realizer _realizer = null;

  /** A CCG parser instance, if a grammar has been loaded */
  private Parser _parser = null;

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

    if (_settings.containsKey("ccg_grammar")) {
      File ccgPath = resolveProjectFile(_settings.get("ccg_grammar"));
      readCCGGrammar(ccgPath);
      if (_grammar != null) {
        _realizer = new Realizer(_grammar);
        _parser = new Parser(_grammar);
      }
    }
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
      System.out.println(lf);
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

  /** Run a batch planning + generation test */
  public BatchTest batchProcess(File batchFile) throws IOException {
    BatchTest bt = new BatchTest(this);
    bt.init(batchFile);
    bt.run();
    return bt;
  }

}
