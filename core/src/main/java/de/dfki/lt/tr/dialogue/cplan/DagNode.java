package de.dfki.lt.tr.dialogue.cplan;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.io.DagPrinter;
import de.dfki.lt.tr.dialogue.cplan.io.LFDagPrinter;
import de.dfki.lt.tr.dialogue.cplan.io.LFDebugPrinter;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public class DagNode {
  /** The lexer to use in logical form parsing */
  private static final Lexer _lfLexer;
  /** The parser for logical forms. */
  private static final LFParser _lfParser;

  static {
    /** The lexer to use in logical form parsing */
    _lfLexer = new Lexer();
    /** The parser for logical forms. */
    _lfParser = new LFParser(_lfLexer);
    _lfParser.setErrorVerbose(true);
  }

  public static Logger logger = LoggerFactory.getLogger("DagNode");

  public static Map<DagNode, FailType> forwardFailures =
    new HashMap<DagNode, FailType>();
  public static Map<DagNode, FailType> backwardFailures =
    new HashMap<DagNode, FailType>();

  public static final int THIS_MORE_GENERAL = 1;
  public static final int ARG_MORE_GENERAL = 2;

  protected static boolean recordFailures = false;

  protected static final int THIS_MORE_GENERAL_MASK = ~ THIS_MORE_GENERAL;
  protected static final int ARG_MORE_GENERAL_MASK = ~ ARG_MORE_GENERAL;

  protected static short NO_FEAT = Short.MAX_VALUE;

  public static final String TOP_TYPE = "top";
  public static int TOP_ID = 0;
  public static final int BOTTOM_ID = -1;

  // We decided to overwrite instead of unify, which has the nice side effect
  // that there are no failures.
  private static final boolean UNIFY_TYPES = false;

  /** Special feature name for the id of a nominal */
  public static short ID_FEAT_ID = -1;

  /** Special feature name for the preposition of a nominal */
  public static short PROP_FEAT_ID = -1;

  /** Special feature name for the type of a nominal */
  public static short TYPE_FEAT_ID = -1;

  private static final String[] featureOrder =
    { "__ID", "__TYPE", "__PROP", "Cop-Restr", "Cop-Scope" };

  private static final int _useLfPrinter = 1;

  public static void init(Hierarchy h) {
    _types = h;
    assert(TOP_ID == getTypeId(TOP_TYPE));
    for (String feat : featureOrder) {
      getFeatureId(feat);
    }
    ID_FEAT_ID = getFeatureId(featureOrder[0]);
    TYPE_FEAT_ID = getFeatureId(featureOrder[1]);
    PROP_FEAT_ID = getFeatureId(featureOrder[2]);
    switch (_useLfPrinter) {
    case 1: usePrettyPrinter(); break;
    case 2: useDebugPrinter(); break;
    }
  }

  public static void reset() {
    _types = null;
    ID_FEAT_ID = -1;
    TYPE_FEAT_ID = -1;
    PROP_FEAT_ID = -1;
    totalNoNodes = totalNoArcs = 0;
    copyGeneration = currentGeneration = 1;
  }

  public static boolean isInitialized() {
    return _types != null;
  }

  public static Hierarchy getHierarchy() {
    return _types;
  }

  public static final DagPrinter[] printers = {
    new LFDagPrinter(), new LFDebugPrinter()
  };

  public static void usePrettyPrinter() {
    registerPrinter(printers[0]);
  }

  public static void useDebugPrinter() {
    registerPrinter(printers[1]);
  }

  public static int totalNoNodes = 0, totalNoArcs = 0;

  static int copyGeneration = 1;
  static int currentGeneration = 1;

  /** A printer used to change the default toString method to print the right
   *  format
   */
  private static DagPrinter _DEFAULT_PRINTER = null;

  /** An object that performs subsumption checks on types */
  static Hierarchy _types = null;

  /* ******************** PUBLIC CONSTANTS ******************** */

  /** Use internal codes or external names for printing */
  public static boolean PRINT_READABLE = true;

  /** Which type of failure occured?
   *  unification failures: Type clash, cycle, wellformedness unification
   *  subsumption failures: type mismatch, missing feature, missing variable
   */
  public enum FailType { SUCCESS, TYPE, CYCLE, FEATURE, VARIABLE, WELLFORMED }

  /** Restrictor constants */
  public enum RESTRICT { NO, KEEP, REMOVE, DELETE, FULL }

  /* ******************** PRIVATE FIELDS AND CLASSES ******************** */

  // The type of this node
  protected int _typeCode;
  // The feature-value list
  ArrayList<DagEdge> _outedges;
  // is this node a complex node or just a prop/type
  private boolean _isNominal;

  // a generation counter to (in)validate the following scratch fields
  int _generation;
  int _copyGeneration;
  // these are the generation-protected scratch slots
  private DagNode _forward;
  private DagNode _copy;

  /** so that we don't have to return null when the edges list is empty */
  private static Iterator<DagEdge> emptyEdges =
    new Iterator<DagEdge>() {
    public boolean hasNext() { return false; }
    public DagEdge next() { throw new NoSuchElementException(); }
    public void remove() {
      throw new UnsupportedOperationException();
    }
  };

  /** This iterator iterates over the edges of a node correctly */
  public class EdgeIterator {
    private int cursorArcs;

    EdgeIterator() {
      if (_outedges != null && _outedges.isEmpty()) {
        _outedges = null;
      }
      cursorArcs = 0;
    }

    public boolean hasNext() {
      return _outedges != null && cursorArcs < _outedges.size();
    }

    /** Return the next edge if there is one, or throw a
     *  {@link NoSuchElementException} instead.
     */
    public DagEdge next() {
      if (hasNext()) {
        int curr = cursorArcs++;
        return _outedges.get(curr);
      }
      throw new NoSuchElementException();
    }

    /** Add this edge to the current dag */
    public void add(DagEdge arc) {
      if (_outedges == null) {
        _outedges = new ArrayList<DagEdge>();
      }
      if (cursorArcs > 0
          && _outedges.get(cursorArcs - 1).getFeature() > arc.getFeature())
        _outedges.add(cursorArcs - 1, arc);
      else
        _outedges.add(cursorArcs, arc);
      ++cursorArcs;
    }
  }

  public static void invalidate() {
    ++ currentGeneration ;
  }

  // *************************************************************************
  // Constructors
  // *************************************************************************

  protected DagNode(int typeIdent) {
    _typeCode = typeIdent;
    _outedges = null;
    _isNominal = false;
    _generation = 0;
    _copyGeneration = 0;
  }

  public DagNode(String string, DagNode dagNode) {
    this(TOP_ID);
    addEdge(new DagEdge(getFeatureId(string), dagNode));
  }

  public DagNode(short featureId, DagNode dagNode) {
    this(TOP_ID);
    addEdge(new DagEdge(featureId, dagNode));
  }

  public DagNode(VarEdge edge) {
    this(TOP_ID);
    addEdge(edge);
  }

  public DagNode(String type) {
    this(getTypeId(type));
  }

  public DagNode() {
    this(TOP_ID);
  }

  protected DagNode clone(int type) {
    DagNode result = new DagNode(type);
    result._isNominal = _isNominal;
    return result;
  }

  // *************************************************************************
  // Parsing Strings to Dags
  // *************************************************************************

  /** Convert the given input string, which contains a (partial) logical form,
   *  into an internal data structure for processing.
   */
  public static DagNode parseLfString(String input) {
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

  /** Get the error position of the last LF parse, or null, if there is no
   *  such error.
   */
  public static Position getLastLFErrorPosition() {
    return _lfLexer.getLastErrorPosition();
  }

  // *************************************************************************
  // Grammar interface
  // *************************************************************************

  public static short getFeatureId(String name) {
    return _types.getFeatureId(name);
  }

  public static String getFeatureName(short id) {
    return _types.getFeatureName(id);
  }

  public static int getTypeId(String name) {
    return _types.getTypeId(name);
  }

  public static String getTypeName(int id) {
    return _types.getTypeName(id);
  }

  protected static int unifyTypes(int type1, int type2) {
    // TODO replace by type unification proper. We currently don't need this.
    return (type1 == type2 ? type1 : BOTTOM_ID);
  }

  protected boolean keepFeature(short feat) {
    return true;
  }

  /** does typeId1 subsumes (is more general than or equal to) typeId2 */
  public static boolean subsumesType(int typeId1, int typeId2) {
    if (typeId1 == typeId2 || typeId1 == DagNode.TOP_ID) {
      return true;
    }
    return (_types != null && _types.subsumes(typeId1, typeId2));
  }


  // *************************************************************************
  // Operations specific to tomabechi dag representation
  // *************************************************************************

  public DagNode dereference() {
    if (_generation != currentGeneration || _forward == null) return this;
    return (_forward).dereference();
  }

  private void setForward(DagNode fs) {
    _generation = currentGeneration;
    _forward = fs;
  }

  private DagNode getForward() {
    return (_generation != copyGeneration) ? null : this._copy;
  }

  private void setCopy(DagNode fs) {
    _copyGeneration = copyGeneration;
    _copy = fs;
  }

  private DagNode getCopy() {
    return (_copyGeneration != copyGeneration) ? null : this._copy;
  }

  /** An iterator that works for complete as well as transitional (unified
   *  but not copied) dags correctly.
   *  Printers may have to have access to this special iterator, therefore
   *  the visibility is default.
   * @return An iterator iterating over all edges of this dag
   */
  EdgeIterator getNewEdgeIterator() {
    return this.new EdgeIterator();
  }

  public boolean edgesAreEmpty() {
    cleanupEdges();
    return (_outedges == null);
  }

  // the next two only make sense in UtterancePlanner version
  public DagNode setNominal() {
    _isNominal = true;
    return this;
  }

  public boolean isNominal() {
    return _isNominal;
  }

  // *************************************************************************
  // END Operations specific to tomabechi dag representation
  // *************************************************************************

  public int getType() {
    return this._typeCode;
  }

  public void setType(int typeIdent) {
    this._typeCode = typeIdent;
  }

  public String getTypeName() {
    return getTypeName(_typeCode);
  }

  public static void recordFailures(boolean state) {
    recordFailures = state;
  }

  public static void registerPrinter(DagPrinter printer) {
    _DEFAULT_PRINTER = printer;
  }

  private DagNode cloneFSRec() {
    DagNode newCopy = getCopy();
    if (newCopy == null) {
      newCopy = this.clone(_typeCode);
      setCopy(newCopy);
      if (getEdges() != null) {
        for (DagEdge e : getEdges()) {
          newCopy.addEdge(e.copySafely(e._value.cloneFSRec()));
        }
      }
    }
    return newCopy;
  }

  public DagNode cloneFS() {
    // invalidate the copy links
    ++ copyGeneration;
    // need a new generation to make a fresh copy: special invalidation
    return cloneFSRec();
  }

  public DagNode copySafelyRec(IdentityHashMap<DagNode, DagNode> copyMap) {
    DagNode here = this.dereference();
    DagNode newCopy = copyMap.get(here);
    if (newCopy == null) {
      newCopy = here.clone(_typeCode);
      copyMap.put(here, newCopy);
      if (getEdges() != null) {
        for (DagEdge e : getEdges()) {
          newCopy.addEdge(e.copySafely(e._value.copySafelyRec(copyMap)));
        }
      }
    }
    return newCopy;
  }

  /** Insanely safe copying of dags */
  public DagNode copySafely() {
    return copySafelyRec(new IdentityHashMap<DagNode, DagNode>());
  }


  /** recursive helper function for copyResult()
   *  @param deleteDaughters delete some top level features only concerned with
   *                         building the constituent tree (grammar specified)
   */
  private DagNode copyResultRec(boolean deleteDaughters) {
    DagNode in = this.dereference();
    DagNode newCopy = in.getCopy();
    if (newCopy != null) {
      return newCopy;
    }

    newCopy = clone(_typeCode);
    in.setCopy(newCopy);

    if (in._outedges != null && ! in._outedges.isEmpty()) {
      newCopy._outedges = new ArrayList<DagEdge>(in._outedges.size());
      for (DagEdge arc : in._outedges) {
        short feat = arc._feature;
        if (!deleteDaughters || keepFeature(feat)) {
          newCopy._outedges.add(
              arc.copySafely(arc._value.copyResultRec(false)));
        }
      }
    }
    return newCopy;
  }

  /** Copy the result after a series of unifications.
   * @return a copied result independent from the input dag
   */
  public DagNode copyIntermediate() {
    // invalidate the copy links
    ++ copyGeneration;
    // Return a copied result using the scratch buffer of this node
    return copyResultRec(false);
  }

  public DagNode copyAndInvalidate() {
    DagNode copy = copyIntermediate();
    invalidate();
    return copy;
  }

  public DagNode copyIntermediate(IdentityHashMap<Object, Object> origToCopy) {
    DagNode in = this.dereference();
    DagNode newCopy = (DagNode) origToCopy.get(in);
    if (newCopy != null) {
      return newCopy;
    }

    newCopy = in.clone(in.getType());
    origToCopy.put(in, newCopy);

    if (in._outedges != null && ! in._outedges.isEmpty()) {
      newCopy._outedges = new ArrayList<DagEdge>(in._outedges.size());
      for (DagEdge arc : in._outedges) {
        newCopy._outedges.add(arc.copyIntermediate(origToCopy));
      }
    }
    return newCopy;
  }


  /** Will always return true. Otherwise, the whole system breaks, because
   *  already applied changes could not be rolled back.
   */
  public boolean add(DagNode arg) {
    DagNode in1 = this.dereference();
    DagNode in2 = arg.dereference();
    if (in1 == in2) return true;

    int type1 = in1._typeCode;
    int type2 = in2._typeCode;

    in1._isNominal |= in2._isNominal;

    int unifType;
    // NO TYPE UNIFICATION --> NO FAILURE, this is intended.
    if (UNIFY_TYPES) {
      unifType = unifyTypes(type1, type2);
      if (unifType == BOTTOM_ID) {
        if (recordFailures)
          forwardFailures.put(this, FailType.TYPE);
        return false;
      }
    }
    else {
      // overwrite types
      unifType = type2;
    }


    in1._typeCode = unifType; // this makes all scratch slots of in1 current
    in2.setForward(in1);      // this makes all scratch slots of in2 current

    EdgeIterator arc1It = in1.getNewEdgeIterator();
    EdgeIterator arc2It = in2.getNewEdgeIterator();

    // the test if the iterators are null is not necessary here (can't occur
    // with a call to getNewEdgeIterator)
    DagEdge arc1 = null, arc2 = null;
    short feat1 = ((arc1It != null && arc1It.hasNext())
                   ? (arc1 = arc1It.next())._feature : NO_FEAT);
    short feat2 = ((arc2It != null && arc2It.hasNext())
                   ? (arc2 = arc2It.next())._feature : NO_FEAT);

    while (feat1 != NO_FEAT || feat2 != NO_FEAT) {
      while (feat1 < feat2) {
        // feature in 1 but not in 2: skip
        feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
      }
      while (feat1 > feat2) { // feature in 2 missing in 1: add edge to 1
        arc1It.add(arc2);
        if (arc2._value._isNominal) {
          this._isNominal = true;
        }
        feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
      }
      if (feat1 == feat2 && feat1 != NO_FEAT) {
        // this differs from ordinary unification
        DagNode subThis = arc1._value;
        DagNode subArg = arc2._value;
        if (subThis._isNominal || subArg._isNominal) {
          // "relation" arcs
          if (! (subThis._isNominal && subArg._isNominal)) {
            logger.warn("Status of relation/feature unclear " +
                "during unification: {}", getFeatureName(feat1));
          }
          arc1It.add(arc2);
        }
        else { // "feature" arcs
          // "Unify" only if both sub-nodes are proper and not special dag nodes
          if (subThis.getClass() == DagNode.class
              && subArg.getClass() == DagNode.class) {
            if (! subThis.add(subArg))
              return false;;
          } else {
            // special dag nodes ALWAYS must be added. Their content is not
            // resolved until expandVars is called
            arc1It.add(arc2);
          }
        }

        feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
        feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
      }
    }

    return true;
  }


  /** This method is called on the replace part of the rules. It
   *  therefore can contain var nodes, etc., which are not part
   *  of ordinary dags.
   *
   * For edges pointing to vars, add them as such, not with the unification
   * algo and handle them in expandVars properly by extracting/adding the
   * right things. For "constants" we can use it as it is now
   *
   * @throws UnificationException if something can not be properly resolved,
   *         like for example a variable under a TYPE feature is bound to
   *         a complex node without TYPE, so the value can not be obtained.
   */
  public final DagNode expandVars(Bindings bindings)
  throws UnificationException {
    DagNode here = dereference();
    if (here._outedges != null) {
      List<DagNode> varDags = new LinkedList<DagNode>();
      Iterator<DagEdge> it = _outedges.iterator();
      // collect all ID edges with special nodes and evaluate their content.
      // the original edges are deleted
      while (it.hasNext()) {
        DagEdge edge = it.next().evaluate(bindings);
        DagNode sub = edge._value;
        if (sub instanceof SpecialDagNode) {
          DagNode eval = ((SpecialDagNode)sub).evaluate(here, bindings);
          if (edge._feature == ID_FEAT_ID)  // why this??
              // || edge._feature == PROP_FEAT_ID)
          {
            // add the plain evaluated node to the varDags
            varDags.add(eval);
          } else {
            // case a) if the node is a plain node without edges, use it as is
            if (! eval.edgesAreEmpty()) {
              Iterator<DagEdge> edgeit = eval.getEdgeIterator();
              DagEdge firstEdge = edgeit.next();
              // case b) if it has only a PROP, use the value under PROP
              if (firstEdge.getFeature() == DagNode.PROP_FEAT_ID
                  && ! edgeit.hasNext()) {
                eval = firstEdge.getValue();
              } else {
                // take the value of the edge with the same feature name
                while (firstEdge._feature != edge._feature) {
                  if (! it.hasNext()) {
                    eval = null;
                    break;
                  }
                  firstEdge = edgeit.next();
                }
                if (eval != null) {
                  eval = firstEdge._value;
                }
              }
            }
            if (eval == null) {
              throw new UnificationException(
                  "The following edge could not be properly expanded: " + edge);
            }
            DagNode newSub = new DagNode();
            newSub.addEdge(new DagEdge(edge._feature, eval));
            varDags.add(newSub);
          }
          it.remove();
        }
        else {
          // either other feature or relation. call recursively
          edge._value = sub.expandVars(bindings);
        }
      }
      for(DagNode varDag : varDags) {
        // unify the contents of the var into this node
        here.add(varDag);
      }
    } else {
      // __TYPE or __PROP
      if (this instanceof SpecialDagNode) {
        return ((SpecialDagNode)this).evaluate(this, bindings);
      }
    }
    return here;
  }


  /*
  Before reactivating this, make sure that a generation-protected slot is
  available for use in visited()


  private int countCorefsRec(int maxCoref) {
    if (visited() < 0) {
      setVisited(0);
      for(EdgeIterator edgeIt = new EdgeIterator(); edgeIt.hasNext();) {
        DagEdge fvpair = edgeIt.next();
        maxCoref = fvpair.getValue().countCorefsRec(maxCoref);
      }
    } else {
      if (visited() == 0) {
        setVisited(++maxCoref);
      }
    }
    return maxCoref;
  }

  public int countCorefs() {
    int result = countCorefsRec(0);
    return result;
  }
  */
  /*
  @SuppressWarnings("null")
  private int subsumesBiRec(DagNode in2, int result) {
    { DagNode fs1 = this.getForward();
      if ((result & THIS_MORE_GENERAL) != 0) {
        if (fs1 == null) {
          this.setForward(in2);
        } else {
          if (fs1 != in2) { // forward = false
            if (recordFailures)
              forwardFailures.put(this, FailType.VARIABLE);
            if ((result &= THIS_MORE_GENERAL_MASK) == 0) return 0;
          }
        }
      }
    }
    { DagNode fs2 = in2.getCopy();
      if ((result & ARG_MORE_GENERAL) != 0) {
        if (fs2 == null) {
          in2.setCopy(this);
        } else {
          if (fs2 != this) {  // backward = false
            if (recordFailures)
              backwardFailures.put(this, FailType.VARIABLE);
            if ((result &= ARG_MORE_GENERAL_MASK) == 0) return 0;
          }
        }
      }
    }
    int type1 = this.getNewType();
    int type2 = in2.getNewType();
    if (type1 != type2) {
      if (! subsumesType(type1, type2)) {
        if (recordFailures)
          forwardFailures.put(this, FailType.TYPE);
        if ((result &= THIS_MORE_GENERAL_MASK) == 0) return 0;
      }
      if (! subsumesType(type2, type1)) {
        if (recordFailures)
          backwardFailures.put(this, FailType.TYPE);
        if ((result &= ARG_MORE_GENERAL_MASK) == 0) return 0;
      }
    }

    List<DagEdge> edges1 = this.getEdges();
    List<DagEdge> edges2 = in2.getEdges();
    if (edges1 == null || edges2 == null) {
      if (edges1 != edges2) {
        if (edges1 == null) {
          if (recordFailures)
            backwardFailures.put(this, FailType.FEATURE);
          if ((result &= ARG_MORE_GENERAL_MASK) == 0) return 0;
        } else {
          if (recordFailures)
            forwardFailures.put(this, FailType.FEATURE);
          if ((result &= THIS_MORE_GENERAL_MASK) == 0) return 0;
        }
      }
      return result;
    }

    Iterator<DagEdge> arc1It = edges1.iterator();
    Iterator<DagEdge> arc2It = edges2.iterator();
    DagEdge arc1 = null, arc2 = null;
    int feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
    int feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
    while (feat1 != NO_FEAT && feat2 != NO_FEAT) {
      if (feat1 < feat2) { // feature in 1 missing in 2: no forward
        if (recordFailures)
          forwardFailures.put(this, FailType.FEATURE);
        if ((result &= THIS_MORE_GENERAL_MASK) == 0) return 0;
        while (feat1 < feat2) {
          feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
        }
      }
      if (feat1 > feat2) { // feature in 2 missing in 1: no backward
        if (recordFailures)
          backwardFailures.put(this, FailType.FEATURE);
        if ((result &= ARG_MORE_GENERAL_MASK) == 0) return 0;
        while (feat1 > feat2) {
          feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
        }
      }
      if (feat1 == feat2 && feat1 != NO_FEAT) {
        if ((result = arc1._value.subsumesBiRec(arc2._value, result)) == 0)
          return 0;
        feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
        feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
      }
    }
    if (feat1 != feat2) {
      if (feat1 == NO_FEAT) { // more features in arg: this is more general
        if (recordFailures)
          backwardFailures.put(this, FailType.FEATURE);
        result &= ARG_MORE_GENERAL_MASK;
      } else {
        if (recordFailures)
          forwardFailures.put(this, FailType.FEATURE);
        result &= THIS_MORE_GENERAL_MASK;
      }
    }

    return result;
  }


  /** compute the subsumption relation between this and fs in both directions:
   * FORWARD subsumption means that `this' subsumes (is a more general)
   * structure than `fs', while
   * BACKWARD subsumption means that `this' is subsumed by (more informative
   * than) `fs'
   *
  public int subsumesBi(DagNode fs) {
    if (recordFailures) {
      forwardFailures.clear();
      backwardFailures.clear();
    }
    int result = subsumesBiRec(fs, THIS_MORE_GENERAL + ARG_MORE_GENERAL);
    invalidate();
    return result;
  }

  @SuppressWarnings("null")
  private boolean subsumesRec(DagNode in2) {
    { DagNode fs1 = this.getForward();
      if (fs1 == null) {
        this.setForward(in2);
      } else {
        if (fs1 != in2) { // forward = false
          return false;
        }
      }
    }

    int type1 = this.getNewType();
    int type2 = in2.getNewType();
    if (type1 != type2) {
      if (! subsumesType(type1, type2)) {
        return false;
      }
    }

    List<DagEdge> edges1 = this.getEdges();
    List<DagEdge> edges2 = in2.getEdges();
    if (edges2 == null) {
      return (edges1 == edges2);
    }
    if (edges1 == null) {
      return true;
    }

    Iterator<DagEdge> arc1It = edges1.iterator();
    Iterator<DagEdge> arc2It = edges2.iterator();
    DagEdge arc1 = null, arc2 = null;
    int feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
    int feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
    while (feat1 != NO_FEAT && feat2 != NO_FEAT) {
      if (feat1 < feat2) { // feature in 1 missing in 2: no forward
        return false;
      }
      if (feat1 > feat2) { // feature in 2 missing in 1: no backward
        while (feat1 > feat2) {
          feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
        }
      }
      if (feat1 == feat2 && feat1 != NO_FEAT) {
        if (! arc1._value.subsumesRec(arc2._value)) return false;
        feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
        feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
      }
    }
    return ((feat1 == feat2) || (feat1 == NO_FEAT));
  }

  /** Return true if `this' is more general than fs *
  public boolean subsumes(DagNode fs) {
    boolean result = subsumesRec(fs);
    invalidate();
    return result;
  }


  /** return true if fs is more general than `this' *
  public boolean isSubsumedBy(DagNode fs) {
    boolean result = (fs).subsumesRec(this);
    invalidate();
    return result;
  }
  */

  /** Check all possible combinations of edge values for equality. Since we
   *  may have edges with identical features, we have to do this expensive
   *  check.
   */
  private boolean
  equalsCrossCheck(List<DagNode> equals1, List<DagNode> equals2,
      IdentityHashMap<DagNode, IdentityHashMap<DagNode, Boolean>> eqClasses) {
    assert(equals2.size() == equals1.size());
    // for every i, we have to find at least one j that is equals
    for (int i = 0; i < equals1.size(); ++i) {
      boolean result = false;
      DagNode dag1 = equals1.get(i);
      for (int j = 0; j < equals2.size(); ++j) {
        boolean localResult = dag1.equalsRecMulti(equals2.get(j), eqClasses);
        result |= localResult;
      }
      if (! result) return false;
    }
    return true;
  }

  /** recursive helper function for equals with multiple edges with the same
   *  feature. This only tests local failures. In the end the validity of
   *  variable bindings has to be checked by computing a perfect matching of
   *  the corresponding nodes of dag1 and dag2 from eqClasses.
   *
   *  @return false if a local failure has been detected that makes it
   *                impossible that this and in2 are equal, true otherwise.
   */
  private boolean equalsRecMulti(DagNode dag2,
      IdentityHashMap<DagNode, IdentityHashMap<DagNode, Boolean>> eqClasses) {
    DagNode in1 = this.dereference();
    DagNode in2 = dag2.dereference();

    IdentityHashMap<DagNode, Boolean> eqToThis = eqClasses.get(in1);
    if (eqToThis == null) {
      eqToThis = new IdentityHashMap<DagNode, Boolean>();
      eqClasses.put(in1, eqToThis);
    } else {
      if (eqToThis.containsKey(in2))
        return eqToThis.get(in2);
    }

    int type1 = in1.getType();
    int type2 = in2.getType();
    if (type1 != type2) {
      eqToThis.put(in2, false);
      return false;
    }

    List<DagEdge> edges1 = in1.getEdges();
    List<DagEdge> edges2 = in2.getEdges();

    // temporarily, to avoid infinite recursion in case of cycle
    eqToThis.put(in2, true);

    DagEdge arc1 = null, arc2 = null;
    int feat1 = NO_FEAT, feat2 = NO_FEAT;
    Iterator<DagEdge> arc1It = null, arc2It = null;
    if (edges1 != null) {
      arc1It = edges1.iterator();
      feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
    }
    if (edges2 != null) {
      arc2It = edges2.iterator();
      feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
    }
    /** I can rightly assume that ID is the first feature */
    if (feat1 == ID_FEAT_ID)
      feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
    if (feat2 == ID_FEAT_ID)
      feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);

    while (feat1 == feat2 &&  feat1 != NO_FEAT) {
      /** Collect all edges on both sides that have this feature */
      int feat = feat1;
      List<DagNode> equals1 = new ArrayList<DagNode>();
      List<DagNode> equals2 = new ArrayList<DagNode>();
      while (feat1 == feat && feat2 == feat) {
        equals1.add(arc1.getValue());
        equals2.add(arc2.getValue());
        feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next())._feature : NO_FEAT);
        feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next())._feature : NO_FEAT);
      }
      // avoid unnecessary cross checks: if the features differ now, the whole
      // dag can't be equal
      if (feat1 != feat2
          || ! equalsCrossCheck(equals1, equals2, eqClasses)) {
        eqToThis.put(in2, false);
        return false;
      }
    }
    if (feat1 != feat2) {
      eqToThis.put(in2, false);
      return false;
    }
    return true;
  }


  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof DagNode)) return false;
    IdentityHashMap<DagNode, IdentityHashMap<DagNode, Boolean>> eqClasses =
      new IdentityHashMap<DagNode, IdentityHashMap<DagNode, Boolean>>();
    boolean result = equalsRecMulti((DagNode) obj, eqClasses);
    if (result) {
      int size = eqClasses.size();
      // check if we find a proper match for eqClasses
      // create a bipartite graph from the equality classes
      BipartiteGraph bg = new BipartiteGraph(size, size);
      IdentityHashMap<DagNode, Integer> nodeNumbering =
        new IdentityHashMap<DagNode, Integer>();
      int nextSourceNumber = 0;
      for(Entry<DagNode, IdentityHashMap<DagNode, Boolean>> sourceEntry
          : eqClasses.entrySet()) {
        int nodeNumber = nextSourceNumber++;
        IdentityHashMap<DagNode, Boolean> targets = sourceEntry.getValue();
        for (Entry<DagNode, Boolean> targetEntry : targets.entrySet()) {
          DagNode target = targetEntry.getKey();
          int targetNumber = 0;
          if (nodeNumbering.containsKey(target)) {
            targetNumber = nodeNumbering.get(target);
          }
          else {
            targetNumber = nodeNumbering.size();
            nodeNumbering.put(target, targetNumber);
            if (targetNumber > size) {
              logger.warn("More target that source nodes");
              return false;
            }
          }
          if (targetEntry.getValue()) {
            bg.add(nodeNumber, targetNumber);
          }
        }
      }
      assert(nextSourceNumber == eqClasses.size());
      if (nodeNumbering.size() != eqClasses.size()) {
        result = false;
      }
      else {
        int match = bg.hopcroft_karp();
        result = (match == eqClasses.size());
      }
    }
    return result;
  }


  /** This is overwriting the hash code because i overwrite equals.
   *  Nevertheless, using HashMap with DagNode is of no use, always use
   *  IdentityHashMaps
   */
  @Override
  public int hashCode() {
    throw new NoSuchMethodError("Hash Maps not supported by Dag Node");
  }


  /** We assume the feature list is either equal to null or not empty. Other
   *  code can rely on that
   */
  private void addEdge(DagEdge undumpArc) {
    if (null == _outedges) _outedges = new ArrayList<DagEdge>(3);
    _outedges.add(undumpArc);
  }

  /** Add a new edge (which is returned) to this DagNode, preserving all
   *  necessary implicit invariants.
   */
  public DagEdge addEdge(short featCode, DagNode fs) {
    DagEdge result = new DagEdge(featCode, fs);
    if (edgesAreEmpty()) {
      addEdge(result);
    } else {
      int i = 0;
      while (i < _outedges.size() && _outedges.get(i).getFeature() < featCode)
        ++i;
      _outedges.add(i, result);
    }
    return result;
  }

  public void sortEdges() {
    Collections.sort(_outedges,
        new Comparator<DagEdge>(){
          public int compare(DagEdge arg0, DagEdge arg1) {
            return arg0._feature - arg1._feature;
          }
    });
  }

  private ArrayList<DagEdge> getEdges() {
    return _outedges;
  }

  public Iterator<DagEdge> getEdgeIterator() {
    return _outedges == null ? emptyEdges : _outedges.iterator();
  }

  public EdgeIterator getTransitionalEdgeIterator() {
    return new EdgeIterator();
  }

  /* return the edge under feature, if existent, null otherwise
   * could be improved using binary or interpolation search.
   * Works correctly only on non-temporary dags.
   */
  public DagEdge getEdge(short feature) {
    if (_outedges == null) return null;
    for (DagEdge edge : _outedges) {
      int f = edge.getFeature();
      if (f == feature)
        return edge;
      if (f > feature)
        break;
    }
    return null;
  }

  /** return the sub-node under feature if existent, null otherwise */
  public DagNode getValue(short feature) {
    DagEdge edge = getEdge(feature);
    return (edge == null ? null : edge.getValue());
  }

  private class EdgesIterator implements Iterator<DagEdge> {
    private Iterator<DagEdge> _impl;
    DagEdge _curr = null;

    public EdgesIterator(short feature) {
      if (_outedges != null) {
        _impl = _outedges.iterator();
        while (_impl.hasNext() &&
               (_curr = _impl.next())._feature < feature) {}
      }
      if (_curr != null && _curr._feature != feature) {
        _curr = null;
      }
    }

    public boolean hasNext() { return _curr != null; }

    public DagEdge next() {
      DagEdge now = _curr;
      if (_impl.hasNext()) {
        _curr = _impl.next();
        if (_curr._feature != now._feature) _curr = null;
      }
      else {
        _curr = null;
      }
      return now;
    }

    public void remove() { throw new UnsupportedOperationException(); }
  }

  /**
   * this function is not present in the standard version because features
   * are unique there
   */
  public Iterator<DagEdge> getEdges(short feature) {
    DagNode value = dereference();
    return value.new EdgesIterator(feature);
  }

  // clear the edges slot if it is not needed
  protected void cleanupEdges() {
    if (_outedges != null && _outedges.isEmpty()) _outedges = null;
  }

  // **************************************************************************
  // BEGIN general convenience functions, path/arg access, etc.
  // **************************************************************************

  /** assign coref numbers to coreferenced nodes. Only nodes that are referred
   *  to more than once get a number greater that zero, all other nodes get
   *  zero.
   *  @return the number of nodes that were referenced more than once.
   */
  public int
  countCorefsLocal(IdentityHashMap<DagNode, Integer> corefs, int nextCorefNo) {
    DagNode here = dereference();
    if (! corefs.containsKey(here)) { // visited for the first time
      corefs.put(here, 0);
      EdgeIterator fvListIt = here.getNewEdgeIterator();
      if (fvListIt != null) {
        while(fvListIt.hasNext()) {
          DagNode sub = fvListIt.next().getValue();
          if (sub != null)
            nextCorefNo = sub.countCorefsLocal(corefs, nextCorefNo);
        }
      }
    } else {
      int corefNo = corefs.get(here);
      if (corefNo == 0) { // visited for the second time at least
        corefs.put(here, ++nextCorefNo);
      }
    }
    return nextCorefNo;
  }

  /** Get the node under the given path.
   *  Works correctly only on non-temporary dags.
   */
  public DagNode getSubNode(Iterator<Short> path) {
    DagNode current = this;
    while (path.hasNext() && current != null) {
      DagEdge next = current.getEdge(path.next());
      current = (next == null) ? null : next.getValue();
    }
    return current;
  }

  /** Remove the edge with the given feature. Works correctly only on
   * non-temporary dags.
   */
  public void removeEdge(short feature) {
    DagNode here = dereference();
    Iterator<DagEdge> it;
    if (here._outedges != null) {
      it = here._outedges.iterator();
      while (it.hasNext()) {
        DagEdge edge = it.next();
        if (edge.getFeature() == feature) {
          it.remove();
          return;
        }
      }
    }
  }

  // *************************************************************************
  // Begin Restrictor and Error Case Reduction
  // *************************************************************************

  private RESTRICT getRestrictorType() {
    return RESTRICT.values()[getType()];
  }

  public void restrict(DagNode restrictor) {
    RESTRICT restrictType = restrictor.getRestrictorType();
    // don't touch anything beyond this node
    if (restrictType == RESTRICT.FULL) return;

    Iterator<? extends DagEdge> arc1It = this.getEdgeIterator();
    Iterator<? extends DagEdge> arc2It = restrictor.getEdgeIterator();
    DagEdge arc1 = null, arc2 = null;
    short feat1 = ((arc1It != null && arc1It.hasNext())
                   ? (arc1 = arc1It.next()).getFeature() : NO_FEAT);
    short feat2 = ((arc2It != null && arc2It.hasNext())
                   ? (arc2 = arc2It.next()).getFeature() : NO_FEAT);

    while (feat1 != NO_FEAT || feat2 != NO_FEAT) {
      while (feat1 < feat2) {
        // feature in this dag but not in restrictor
        if (restrictType == RESTRICT.KEEP) {
          arc1It.remove(); // delete the not mentioned feature
        }
        feat1 = (arc1It.hasNext()
                 ? (arc1 = arc1It.next()).getFeature() : NO_FEAT);
      }
      while (feat1 > feat2) { // feature in restrictor missing in this dag
        feat2 = (arc2It.hasNext()
                 ? (arc2 = arc2It.next()).getFeature() : NO_FEAT);
      }
      if (feat1 == feat2 && feat1 != NO_FEAT) {
        if (restrictType == RESTRICT.REMOVE ||
            arc2.getValue().getRestrictorType() == RESTRICT.DELETE) {
          arc1It.remove();
        } else {
          arc1.getValue().restrict(arc2.getValue());
        }
        feat1 = (arc1It.hasNext()
                 ? (arc1 = arc1It.next()).getFeature() : NO_FEAT);
        feat2 = (arc2It.hasNext()
                 ? (arc2 = arc2It.next()).getFeature() : NO_FEAT);
      }
    }
    cleanupEdges();
  }

  /*
  private void restrictSimpleRec(HashSet<DagNode> visited) {
    Iterator<? extends DagEdge> arcIt = this.getEdgeIterator();
    if (visited.contains(this) || arcIt == null) return;
    visited.add(this);
    while (arcIt.hasNext()) {
      DagEdge arc = arcIt.next();
      if (! keepFeature(arc.getFeature())) {
        arcIt.remove();
      } else {
        arc.getValue().restrict();
      }
    }
    edgesAreEmpty();
  }

  public void restrict() {
    restrictSimpleRec(new HashSet<DagNode>());
  }

  public void reduceRec(TFSErrorProducer ep, DagNode resSubNode,
                        HashSet<DagNode> visited) {
    if (visited.contains(this)) return;
    visited.add(this);
    Iterator<? extends DagEdge> currentEdgeIt = this.getEdgeIterator();
    if (currentEdgeIt == null) return;
    // while (featureToRemove still available)
    while (currentEdgeIt.hasNext()) {
      // check error with enhanced restrictor (add featureToRemove)
      DagEdge currentEdge = currentEdgeIt.next();
      resSubNode.addEdge(currentEdge.getFeature(),
                         new DagNode(RESTRICT.DELETE.ordinal()));
      // if error persists, keep restrictor, otherwise remove last feature
      if (ep.errorPersists()) {
        // error is still there, proceed to the next feature
        // nothing to do here
      } else {
        // first remove current edge from restrictor
        resSubNode.removeEdge(currentEdge.getFeature());
        DagNode resSubNext =
          new DagNode(RESTRICT.NO.ordinal());
        // try it one level deeper
        resSubNode.addEdge(currentEdge.getFeature(), resSubNext);
        currentEdge.getValue().reduceRec(ep, resSubNext, visited);
      }
      // get next featureToRemove
    }
  }
  */

  /** TODO: This will work only for the simple cases !!! */
  @SuppressWarnings("null")
  private boolean subsumesRec(DagNode in2) {
    { DagNode fs1 = this.getForward();
      if (fs1 == null) {
        this.setForward(in2);
      } else {
        return (fs1 == in2);
      }
    }

    int type1 = this.getType();
    int type2 = in2.getType();
    if (type1 != type2) {
      if (! subsumesType(type1, type2)) {
        return false;
      }
    }

    List<DagEdge> edges1 = this.getEdges();
    List<DagEdge> edges2 = in2.getEdges();
    if (edges2 == null) {
      return (edges1 == edges2);
    }
    if (edges1 == null) {
      return true;
    }

    Iterator<DagEdge> arc1It = edges1.iterator();
    Iterator<DagEdge> arc2It = edges2.iterator();
    DagEdge arc1 = null, arc2 = null;
    int feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next()).getFeature() : NO_FEAT);
    int feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next()).getFeature() : NO_FEAT);
    while (feat1 != NO_FEAT && feat2 != NO_FEAT) {
      if (feat1 < feat2) { // feature in 1 missing in 2: no forward
        return false;
      }
      if (feat1 > feat2) { // feature in 2 missing in 1: no backward
        while (feat1 > feat2) {
          feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next()).getFeature() : NO_FEAT);
        }
      }
      if (feat1 == feat2 && feat1 != NO_FEAT) {
        if (! arc1.getValue().subsumesRec(arc2.getValue())) return false;
        feat1 = (arc1It.hasNext() ? (arc1 = arc1It.next()).getFeature() : NO_FEAT);
        feat2 = (arc2It.hasNext() ? (arc2 = arc2It.next()).getFeature() : NO_FEAT);
      }
    }
    return ((feat1 == feat2) || (feat1 == NO_FEAT));
  }

  /** Return true if `this' is more general than fs */
  public boolean subsumes(DagNode fs) {
    boolean result = subsumesRec(fs);
    invalidate();
    return result;
  }


  /** return true if fs is more general than `this' */
  public boolean isSubsumedBy(DagNode fs) {
    boolean result = (fs).subsumesRec(this);
    invalidate();
    return result;
  }


  // *************************************************************************
  // Begin construct jxchg string representation from (permanent) dag
  // *************************************************************************

  private void toStringRec(boolean readable, StringBuilder sb,
                           IdentityHashMap<DagNode, Integer> corefs) {
    DagNode here = this.dereference();
    int corefNo = corefs.get(here);
    if (corefNo < 0) { // already printed, only coref
      sb.append(" #").append(-corefNo).append(' ');
      return;
    }

    if (corefNo > 0) {
      sb.append(" #").append(corefNo).append(' ');
      corefs.put(here, -corefNo);
    }

    sb.append('[');
    sb.append(readable ? here.getTypeName() : here.getType());
    EdgeIterator fvListIt = here.getNewEdgeIterator();
    if (fvListIt != null && fvListIt.hasNext()) {
      while(fvListIt.hasNext()) {
        DagEdge edge = fvListIt.next();
        sb.append(' ');
        sb.append(readable ? edge.getName() : edge.getFeature());
        edge.getValue().toStringRec(readable, sb, corefs);
      }
    }
    else {
      sb.append('@').append(here.getTypeName()).append('@');
    }
    sb.append(']');
  }

  public void toStringRec(DagPrinter p) {
    p.tsr(this);
  }

  /** print fs in standardized default format */
  public final String asString() {
    DagPrinter def = _DEFAULT_PRINTER;
    try {
      usePrettyPrinter();
      return toString(false);
    } finally {
      _DEFAULT_PRINTER = def;
    }
  }

  /** print fs in jxchg format */
  @Override
  public final String toString() {
    return toString(PRINT_READABLE);
  }

  public final String toString(boolean readable) {
    StringBuilder sb = new StringBuilder();
    if (_DEFAULT_PRINTER != null) {
      synchronized (_DEFAULT_PRINTER) {
        _DEFAULT_PRINTER.getCorefs(this);
        _DEFAULT_PRINTER.setReadable(readable);
        _DEFAULT_PRINTER.toStringRec(this, sb);
      }
    }
    else {
      IdentityHashMap<DagNode, Integer> corefMap =
          new IdentityHashMap<DagNode, Integer>();
      int corefs = 0;
      corefs = countCorefsLocal(corefMap, corefs);
      toStringRec(readable, sb, corefMap);
    }
    return sb.toString();
  }


}
