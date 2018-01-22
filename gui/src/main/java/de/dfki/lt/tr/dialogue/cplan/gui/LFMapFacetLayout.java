package de.dfki.lt.tr.dialogue.cplan.gui;

import java.util.Arrays;

import de.dfki.lt.loot.gui.Style;
import de.dfki.lt.loot.gui.ViewContext;
import de.dfki.lt.loot.gui.adapters.ModelAdapter;
import de.dfki.lt.loot.gui.layouts.FacetLayoutBase;
import de.dfki.lt.loot.gui.nodes.AligningNode;
import de.dfki.lt.loot.gui.nodes.GraphicalNode;
import de.dfki.lt.loot.gui.nodes.SquareBracketNode;
import de.dfki.lt.loot.gui.nodes.TextNode;
import de.dfki.lt.loot.gui.nodes.BracketNode.Orientation;
import de.dfki.lt.tr.dialogue.cplan.DagEdge;
import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.Environment;

/** A Layout specifically to render OpenCCG logical forms (HLDS)
 *  This is a FacetLayout to be used with AbstractLayout just to get the
 *  coreferences done for free.
 *  This means that the LFLayout registering this layout must be used with
 *  an EmptyAdapter that always returns MAP as facet.
 */
public class LFMapFacetLayout extends FacetLayoutBase {

  public static short[] _excludedFeatures = null;

  public static Environment env;

  /** Pass the feature IDs of ID, TYPE, and PROP in an array */
  public static void init(short[] excluded) {
    // ModelAdapterFactory.register(DagNode.class, LFModelAdapter.class);
    _excludedFeatures = excluded;
  }

  public int facet() {
    return ModelAdapter.MAP;
  }

  public GraphicalNode transform(Object model, ViewContext context,
      int facetMask) {
    //Environment env = ((IPViewContext)context).getEnvironment();
    if (model == null) return null;
    DagNode dag = (DagNode) model;
    dag = dag.dereference();

    if (! dag.isNominal()) {
      DagNode prop = dag.getEdge(_excludedFeatures[2]).getValue().dereference();
      return new TextNode(env.getTypeName(prop), Style.get("type"));
    }

    DagNode.EdgeIterator it = dag.getTransitionalEdgeIterator();
    DagEdge edge = null;
    DagNode prop = null ;
    DagNode id = null;
    DagNode type = null;

    while (it.hasNext()) {
      edge = it.next();
      short feature = edge.getFeature();
      if (feature == _excludedFeatures[0]) {
        id = edge.getValue().dereference(); edge = null;
      } else if (feature == _excludedFeatures[1]) {
        type = edge.getValue().dereference(); edge = null;
      } else if (feature == _excludedFeatures[2]) {
        prop = edge.getValue().dereference(); edge = null;
      } else {
        break;
      }
    }

    /* Version with TabularNode takes to much space
    TabularNode tb = new TabularNode(true, "wnw");
    StringBuilder sb = new StringBuilder();
    if (id != null) sb.append(id.getTypeName());
    sb.append(":");
    if (type != null) sb.append(type.getTypeName());
    tb.startNext();
    tb.addNode(new TextNode(sb.toString(), Style.get("type")));
    tb.addNode(new TextNode(" ^ ", Style.get("type")));
    if (prop != null)
      tb.addNode(new TextNode(prop.getTypeName(), Style.get("type")));

    it = dag.getTransitionalEdgeIterator();
    while (it.hasNext()) {
      edge = it.next();
      short feature = edge.getFeature();
      if (Arrays.binarySearch(_excludedFeatures, feature) < 0) {
        tb.startNext();
        tb.addNode(new EmptyNode());
        tb.addNode(new TextNode(" ^ ", Style.get("type")));
        CompositeNode fvpNode = new CompositeNode('n');
        fvpNode.addNode(new TextNode(edge.getName(), Style.get("feature")));
        fvpNode.addNode(_metaLayout.transform(edge.getValue(), context,
                                              facetMask));
        tb.addNode(fvpNode);
      }
    }
    */

    AligningNode tb = new AligningNode('w');
    StringBuilder sb = new StringBuilder();
    if (id != null) sb.append(env.getTypeName(id));
    sb.append(":");
    if (type != null) sb.append(env.getTypeName(type));
    if (prop != null) {
      sb.append(" ^ ").append(env.getTypeName(prop));
    }
    tb.addNode(new TextNode(sb.toString(), Style.get("type")));

    it = dag.getTransitionalEdgeIterator();
    while (it.hasNext()) {
      edge = it.next();
      short feature = edge.getFeature();
      if (Arrays.binarySearch(_excludedFeatures, feature) < 0) {
        AligningNode fvpNode = new AligningNode('n');
        fvpNode.addNode(new TextNode(env.getName(edge) + " ",
            Style.get("feature")));
        fvpNode.addNode(_meta.transform(edge.getValue(), context, facetMask));
        tb.addNode(fvpNode);
      }
    }

    AligningNode node = new AligningNode('h');
    // add brackets and feature-value list to the result node
    node.addNode(new SquareBracketNode(Orientation.west));
    node.addNode(tb);
    node.addNode(new SquareBracketNode(Orientation.east));
    return node;
  }
}
