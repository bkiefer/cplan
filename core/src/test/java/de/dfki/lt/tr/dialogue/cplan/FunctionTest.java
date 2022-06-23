package de.dfki.lt.tr.dialogue.cplan;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.tr.dialogue.cplan.functions.string.Split;
import de.dfki.lt.tr.dialogue.cplan.functions.string.Substring;

public class FunctionTest {

  @Before public void setUp() {
    // next line is needed to initialize static fields
    UtterancePlanner up = new UtterancePlanner();
    up.initHierachy();
    // DagNode.registerPrinter(null); // return to default printer
    String[] features = { "Aspect", "Mood", "Tense", "Delimitation", "Num", "Quantification", "List" };
    String[] relations = { "Actor", "Event", "Patient", "Subject", "First", "Next" };
    Arrays.sort(features);
    Arrays.sort(relations);
    for (String feature : features) {
      DagNode.getFeatureId(feature);
    }
    for (String feature : relations) {
      DagNode.getFeatureId(feature);
    }
    DagNode.invalidate();
  }

  @Test public void testSubstring() {
    Substring ss = new Substring();
    String[][] patterns = {
        {"@a:b(<s>\"TestString\" ^ <f>\"0\" ^ <t>\"-1\")", "TestString"},
        {"@a:b(<s>\"TestString\" ^ <f>\"4\" ^ <t>\"-2\")", "Strin"},
        {"@a:b(<s>\"TestString\" ^ <f>\"-1\" ^ <t>\"-1\")", "g"},
        {"@a:b(<s>\"TestString\" ^ <f>\"-2\" ^ <t>\"-2\")", "n"},
        {"@a:b(<s>\"TestString\" ^ <f>\"3\" ^ <t>\"3\")", ""},
        {"@a:b(<s>\"TestString\" ^ <f>\"3\" ^ <t>\"4\")", "t"},
    };
    for (String[] pat : patterns) {
      DagNode d = DagNode.parseLfString(pat[0]);
      List<DagNode> args = new ArrayList<>();
      args.add(d.getEdge(DagNode.getFeatureId("s")).getValue());
      args.add(d.getEdge(DagNode.getFeatureId("f")).getValue());
      args.add(d.getEdge(DagNode.getFeatureId("t")).getValue());

      assertEquals(pat[1], ss.apply(args));
    }
  }
  
  private String getStringVal(DagNode res, String edgeName) {
    return res.getEdge(DagNode.getFeatureId(edgeName)).getValue().asString();
  }
  
  @Test public void testSplit() {
    Split sp = new Split();
    List<DagNode> args = new ArrayList<>();
    DagNode d = DagNode.parseLfString(
        "@a:b(<s>\"Split_this_string\" ^ <t>\"_\")");
    args.add(d.getEdge(DagNode.getFeatureId("s")).getValue());
    args.add(d.getEdge(DagNode.getFeatureId("t")).getValue());
    DagNode res = (DagNode)sp.apply(args);

    assertEquals("Split", getStringVal(res, "1"));
    assertEquals("this", getStringVal(res,"2"));
    assertEquals("string", getStringVal(res, "3"));
  }
}