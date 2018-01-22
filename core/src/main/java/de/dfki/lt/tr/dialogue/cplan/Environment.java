package de.dfki.lt.tr.dialogue.cplan;

import java.io.IOException;
import java.io.StringReader;
import java.util.IdentityHashMap;

import de.dfki.lt.tr.dialogue.cplan.io.DagPrinter;
import de.dfki.lt.tr.dialogue.cplan.io.LFDagPrinter;
import de.dfki.lt.tr.dialogue.cplan.io.LFDebugPrinter;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public class Environment {

  public static Environment lastEnvironment;

  /** The lexer to use in logical form parsing */
  private final Lexer _lfLexer;
  /** The parser for logical forms. */
  private final LFParser _lfParser;


  public final String TOP_TYPE = "top";
  public int TOP_ID = 0;
  public final int BOTTOM_ID = -1;

  /** Special feature name for the id of a nominal */
  public short ID_FEAT_ID = -1;

  /** Special feature name for the preposition of a nominal */
  public short PROP_FEAT_ID = -1;

  /** Special feature name for the type of a nominal */
  public short TYPE_FEAT_ID = -1;

  private static final String[] featureOrder =
    { "__ID", "__TYPE", "__PROP", "Cop-Restr", "Cop-Scope" };

  /** A printer used to change the default toString method to print the right
   *  format
   */
  public DagPrinter DEFAULT_PRINTER = null;

  /** Use internal codes or external names for printing */
  public static boolean PRINT_READABLE = true;

  /** An object that performs subsumption checks on types */
  Hierarchy _types = null;

  public Environment() {
    /** The lexer to use in logical form parsing */
    _lfLexer = new Lexer();
    /** The parser for logical forms. */
    _lfParser = new LFParser(_lfLexer, this);
    _lfParser.setErrorVerbose(true);
    lastEnvironment = this;
  }

  public void init(Hierarchy h) {
    _types = h;
    // reserve the mapping to zero for the "top" type
    _types.getTypeId(TOP_TYPE);
    assert(TOP_ID == getTypeId(TOP_TYPE));
    for (String feat : featureOrder) {
      getFeatureId(feat);
    }
    ID_FEAT_ID = getFeatureId(featureOrder[0]);
    assert(ID_FEAT_ID == 0);
    TYPE_FEAT_ID = getFeatureId(featureOrder[1]);
    assert(TYPE_FEAT_ID == 1);
    PROP_FEAT_ID = getFeatureId(featureOrder[2]);
    assert(PROP_FEAT_ID == 2);
    /*
    switch (_useLfPrinter) {
    case 1: usePrettyPrinter(); break;
    case 2: useDebugPrinter(); break;
    }
    */
  }

  public boolean isInitialized() {
    return _types != null;
  }

  public Hierarchy getHierarchy() {
    return _types;
  }

  public void usePrettyPrinter() {
    registerPrinter(new LFDagPrinter());
  }

  public void useDebugPrinter() {
    registerPrinter(new LFDebugPrinter());
  }

  // *************************************************************************
  // Grammar interface
  // *************************************************************************

  public short getFeatureId(String name) {
    return _types.getFeatureId(name);
  }

  public String getFeatureName(short id) {
    return _types.getFeatureName(id);
  }

  public int getTypeId(String name) {
    return _types.getTypeId(name);
  }

  public String getTypeName(int id) {
    return _types.getTypeName(id);
  }

  protected int unifyTypes(int type1, int type2) {
    // TODO replace by type unification proper. We currently don't need this.
    return (type1 == type2 ? type1 : BOTTOM_ID);
  }

  /** Convert the given input string, which contains a (partial) logical form,
   *  into an internal data structure for processing.
   */
  public DagNode parseLfString(String input) {
    input = input.trim();
    if (input.isEmpty())
      return null;
    StringReader sr = new StringReader(input);
    _lfParser.reset("Console", sr);
    try {
      if (_lfParser.parse() && _lfParser.correct()) {
        return _lfParser.getResultLF();
      }
    }
    catch (IOException ioex) {
      // this will hardly ever been thrown
      ioex.printStackTrace();
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      // may occur during parsing of LF in LFParser. Just die silently.
    }
    return null;
  }

  public final String toString(DagNode node, boolean readable) {
    IdentityHashMap<DagNode, Integer> corefMap =
      new IdentityHashMap<DagNode, Integer>();
    int corefs = 0;
    corefs = node.countCorefsLocal(corefMap, corefs);
    StringBuilder sb = new StringBuilder();
    if (node instanceof SpecialDagNode) {
      ((SpecialDagNode)node).toStringSpecial(this, sb);
    }
    else {
      if (DEFAULT_PRINTER != null) {
        synchronized (DEFAULT_PRINTER) {
          DEFAULT_PRINTER.getCorefs(node);
          DEFAULT_PRINTER.toStringRec(this, node, readable, sb);
        }
      }
      else {
        node.toStringRec(readable, sb, corefMap, this);
      }
    }
    return sb.toString();
  }
  /** Get the error position of the last LF parse, or null, if there is no
   *  such error.
   */
  public Position getLastLFErrorPosition() {
    return _lfLexer.getLastErrorPosition();
  }

  /** does typeId1 subsumes (is more general than or equal to) typeId2 */
  public boolean subsumesType(int typeId1, int typeId2) {
    if (typeId1 == typeId2 || typeId1 == TOP_ID) {
      return true;
    }
    return (_types != null && _types.subsumes(typeId1, typeId2));
  }

  public void registerPrinter(DagPrinter printer) {
    DEFAULT_PRINTER = printer;
  }

  /** print fs in jxchg format */
  public final String toString(DagNode node) {
    return toString(node, PRINT_READABLE);
  }

  public DagNode getDagNode(String string, DagNode dagNode) {
    DagNode result = new DagNode();
    result.addEdge(new DagEdge(getFeatureId(string), dagNode));
    return result;
  }

  public DagNode getDagNode(String type) {
    return new DagNode(getTypeId(type));
  }

  public String getTypeName(DagNode dag) {
    return getTypeName(dag.getType());
  }

  public String getName(DagEdge edge) {
    return getFeatureName(edge.getFeature());
  }

  public String toString(DagEdge edge) {
    return '<' + getName(edge) + '>' + toString(edge.getValue());
  }
}
