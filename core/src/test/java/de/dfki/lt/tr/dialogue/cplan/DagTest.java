package de.dfki.lt.tr.dialogue.cplan;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class DagTest {
  private static String[] testLFs = {
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Modality>cognition ^ <Content>(e1:ascription ^ <Target>(b2:entity ^ object ^ <Salient>true) ^ <Color>(b3:quality ^ red)))",
    "@dummy1:dvp(c-goal ^ <SpeechAct>question ^ <Content>(dummy2:ascription ^ dummy ^ <Target>(dummy3:entity ^ box ^ <InfoStatus>familiar) ^ <Color>(dummy4:quality ^ red ^ <Questioned>true)))",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Relation>reject ^ <AcknoModality>vision)",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(b1:entity ^ ball ^ <Color>red ^ <InfoStatus>familiar ^ <Size>big))",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(e1:ascription ^ <Target>(b2:entity ^ object ^ <InfoStatus>familiar ^ <Size>big) ^ <Type>(b3:entity ^ box ^ <Color>red)) ^ <Relation>answer)",
    "@d1:dvp(c-goal ^ <SpeechAct>question ^ <Content>(e1:ascription ^ <Target>(b2:entity ^ object ^ <Salient>true) ^ <Type>(b3:ont-entity ^ object ^ <Questioned>true)))",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(e1:cognition ^ know ^ <Actor>(i:person ^ I) ^ <Patient>(c:entity ^ color ^ <InfoStatus>familiar)) ^ <Relation>answer)",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(e1:ascription ^ <Target>(b2:entity ^ object ^ <Salient>true) ^ <Type>(b3:ont-entity ^ object ^ <Questioned>true)))",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(e1:ascription ^ <Target>(b2:entity ^ box ^ <InfoStatus>familiar) ^ <Color>(b3:quality ^ blue)) ^ <Relation>answer ^ <Polarity>neg)",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(e1:cognition ^ know ^ <Actor>(i:person ^ I) ^ <Polarity>neg) ^ <Relation>answer)",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(e1:cognition ^ know ^ <Actor>(i:person ^ I)) ^ <Relation>answer ^ <AnswerType>negative)",
    "@d1:dvp(c-goal ^ <Content>(e1:entity ^ <List>conjunction ^ <First>(b1:physical ^ ball ^ <Size>big) ^ <Next>(b2:physical ^ box ^ <Shape>tall)))",
    "@d1:dvp(c-goal ^ <Content>(e1:entity ^ <List>disjunction ^ <First>(b1:physical ^ ball ^ <Size>big) ^ <Next>(b2:physical ^ box ^ <Shape>tall)))",
    "@d1:dvp(c-goal ^ <SpeechAct>question ^ <Content>(e1:ascription ^ <Target>(b2:entity ^ object ^ <InfoStatus>familiar) ^ <Color>(l1:quality ^ <List>disjunction ^ <First>(q1:quality ^ blue) ^ <Next>(q2:quality ^ brown))))",
    "@d1:dvp(c-goal ^ <SpeechAct>assertion ^ <Content>(a1:ascription ^ <Target>(e1:entity ^ <List>conjunction ^ <First>(b1:physical ^ ball ^ <InfoStatus>familiar ^ <Size>big) ^ <Next>(b2:physical ^ box ^ <InfoStatus>familiar ^ <Shape>tall))))",
    "@DVP:dvp(accept ^ <Content>(CONTENT:marker))",
    "@d1:dvp(c-goal ^ <Content>(a1:ascription) ^ <Content2>a1:ascription)",
    "@l1:d-units(list ^ <First>(i1:person ^ I ^ <Num>sg) ^ <Next>(l2:d-units ^ list ^ <First>(l3:d-units ^ list ^ <First>(k1:cognition ^ know ^ <Mood>imp ^ <Actor>(a1:entity ^ addressee) ^ <Event>(s1:perception ^ see ^ <Mood>ind ^ <Tense>past ^ <Actor>(i2:person ^ I ^ <Num>sg) ^ <Subject>i2:person) ^ <Subject>a1:entity) ^ <Next>(m1:person ^ man ^ <Delimitation>unique ^ <Num>sg ^ <Quantification>specific)) ^ <Next>x1))",
    "@l1:d-units(list ^ <First>(t1:modal ^ try ^ <Mood>ind ^ <Tense>past ^ <Actor>(o1:entity ^ or ^ <First>(h1:person ^ he ^ <Num>sg) ^ <Next>(i1:person ^ I ^ <Num>sg)) ^ <Event>(w1:action-motion ^ walk ^ <Actor>o1:entity) ^ <Subject>o1:entity) ^ <Next>x1)",
    "@a:b(z ^ <First>(a:b ^ z ^ <First>(c:d ^ z) ^ <First>(e:f ^ y)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(a:b ^ z ^ <First>(e:f ^ z) ^ <First>(c:d ^ y) ^ <First>(i:j ^ x)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(i:j) ^ <First>(a:b ^ z ^ <First>(e:f ^ z) ^ <First>(c:d ^ y) ^ <First>(i:j ^ x)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(k:l) ^ <First>(a:b ^ z ^ <First>(e:f ^ z) ^ <First>(c:d ^ y) ^ <First>(i:j ^ x)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(:p ^ k) ^ <First>(:p ^ j) ^ <First>(:p ^ j))",
    "@a:b(z ^ <First>(:p ^ k) ^ <First>(:p ^ k) ^ <First>(:p ^ j))"
  };

  String[] testLFsAllEquals = {
    "@a:b(z ^ <First>(a:b ^ z ^ <First>(e:f ^ z) ^ <First>(c:d ^ y)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(a:b ^ z ^ <First>(c:d ^ y) ^ <First>(e:f ^ z)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(a:b ^ z ^ <First>(e:f ^ z) ^ <First>(c:d ^ y) ^ <First>(i:j ^ x)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(a:b ^ z ^ <First>(i:j ^ x) ^ <First>(c:d ^ y) ^ <First>(e:f ^ z)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(i:j) ^ <First>(a:b ^ z ^ <First>(e:f ^ y) ^ <First>(c:d ^ z) ^ <First>(i:j ^ x)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)))",
    "@a:b(z ^ <First>(a:b ^ z ^ <First>(i:j ^ x) ^ <First>(c:d ^ z) ^ <First>(e:f ^ y)) ^ <First>(g:h ^ z ^ <First>(e:f) ^ <First>(c:d)) ^<First> (i:j))"
  };


  @Before public void setUp() {
    // next line is needed to initialize static fields
    @SuppressWarnings("unused")
    UtterancePlanner up = new UtterancePlanner();
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
    DagNode.useDebugPrinter();
  }

  private boolean checkSorted(DagNode dag) {
    short lastfeat = -1;
    for (DagEdge edge : dag._outedges) {
      if (lastfeat > edge._feature) return false;
      lastfeat = edge._feature;
    }
    return true;
  }

  @Test public void addEdgeTest() {
    DagNode e1 = new DagNode();
    e1.addEdge((short)3, new DagNode("A"));
    e1.addEdge((short)1, new DagNode("A"));
    assertTrue(checkSorted(e1));
    e1.addEdge((short)4, new DagNode("A"));
    assertTrue(checkSorted(e1));

    e1 = new DagNode();
    e1.addEdge((short)3, new DagNode("A"));
    e1.addEdge((short)5, new DagNode("A"));
    assertTrue(checkSorted(e1));
    e1.addEdge((short)1, new DagNode("A"));
    assertTrue(checkSorted(e1));
    e1.addEdge((short)2, new DagNode("A"));
    assertTrue(checkSorted(e1));
    e1.addEdge((short)4, new DagNode("A"));
    assertTrue(checkSorted(e1));
    e1.addEdge((short)6, new DagNode("A"));
    assertTrue(checkSorted(e1));
    e1.addEdge((short)0, new DagNode("A"));
    assertTrue(checkSorted(e1));

  }

  @Test
  public void addTest1() {
    DagNode d1 = new DagNode();
    DagNode d2 = new DagNode();
    d1.addEdge((short)1, new DagNode("A"));
    d2.addEdge((short)2, new DagNode("B"));

    d1.add(d2);
    DagNode d4 = d1.copyAndInvalidate();

    DagNode d3 = new DagNode();
    d3.addEdge((short)1, new DagNode("A"));
    d3.addEdge((short)2, new DagNode("B"));

    assertEquals(d4, d3);
  }

  @Test
  public void addTest2() {
    DagNode d1 = new DagNode();
    DagNode d2 = new DagNode();
    d1.addEdge((short)2, new DagNode("B"));
    d2.addEdge((short)1, new DagNode("A"));
    d2.addEdge((short)3, new DagNode("C"));

    d1.add(d2);
    DagNode d4 = d1.copyAndInvalidate();

    DagNode d3 = new DagNode();
    d3.addEdge((short)1, new DagNode("A"));
    d3.addEdge((short)2, new DagNode("B"));
    d3.addEdge((short)3, new DagNode("C"));
    assertEquals(d4, d3);
  }

  @Test
  public void addTest3() {
    DagNode d1 = new DagNode();
    DagNode d2 = new DagNode();
    d1.addEdge((short)2, new DagNode("B"));
    d1.addEdge((short)4, new DagNode("D"));
    d2.addEdge((short)3, new DagNode("C"));

    d1.add(d2);
    DagNode d4 = d1.copyAndInvalidate();

    DagNode d3 = new DagNode();
    d3.addEdge((short)2, new DagNode("B"));
    d3.addEdge((short)3, new DagNode("C"));
    d3.addEdge((short)4, new DagNode("D"));
    assertEquals(d4, d3);
  }

  @Test
  public void addTest4() {
    DagNode d1 = new DagNode();
    DagNode d2 = new DagNode();
    d1.addEdge((short)2, new DagNode("B"));
    d1.addEdge((short)4, new DagNode("D"));
    d2.addEdge((short)5, new DagNode("E"));

    d1.add(d2);
    DagNode d4 = d1.copyAndInvalidate();

    DagNode d3 = new DagNode();
    d3.addEdge((short)2, new DagNode("B"));
    d3.addEdge((short)4, new DagNode("D"));
    d3.addEdge((short)5, new DagNode("E"));
    assertEquals(d4, d3);
  }

  @Test
  public void addTest5() {
    DagNode d1 = new DagNode();
    DagNode d2 = new DagNode();
    d1.addEdge((short)2, new DagNode("B"));
    d1.addEdge((short)4, new DagNode("D"));
    d2.addEdge((short)1, new DagNode("A"));

    d1.add(d2);
    DagNode d4 = d1.copyAndInvalidate();

    DagNode d3 = new DagNode();
    d3.addEdge((short)1, new DagNode("A"));
    d3.addEdge((short)2, new DagNode("B"));
    d3.addEdge((short)4, new DagNode("D"));
    assertEquals(d4, d3);
  }

  private boolean testEqualsMultiOne(String lf, String lf2) {
    LFParser lfparser = new LFParser(new Lexer(new StringReader(lf)));
    lfparser.setErrorVerbose(true);
    DagNode res = null;
    try {
      if (lfparser.parse())
        res = lfparser.getResultLF();
    }
    catch (IOException ioex) {
    }
    LFParser lfparser2 = new LFParser(new Lexer(new StringReader(lf2)));
    lfparser2.setErrorVerbose(true);
    DagNode res2 = null;
    try {
      if (lfparser2.parse())
        res2 = lfparser2.getResultLF();
    }
    catch (IOException ioex) {
    }
    if (res == null || res2 == null) return false;
    return res.equals(res2);
  }

  /*
  @Test public void testEqualsMultiSpecial() {
    int i = 2; int j = 3;
    assertEquals("Run "+ i + " vs. " + j, (i == j),
        testEqualsMultiOne(testLFs[i], testLFs[j]));
  }
  */

  @Test public void testEqualsMultiDisjunct() {
    int i = 0;
    for (String lf : testLFs) {
      int j = 0;
      for (String lf2 : testLFs) {
        assertEquals("Run "+ i + " vs. " + j, (i == j),
            testEqualsMultiOne(lf, lf2));
        ++j;
      }
      ++i;
    }
  }

  /*
  @Test public void testEqualsMultiAllSpecial() {
    int i = 2;
    assertEquals("Run "+ i, true,
        testEqualsMultiOne(testLFsAllEquals[i], testLFsAllEquals[i+1]));
  }
  */

  @Test public void testEqualsMultiAllEquals() {
    for (int i = 0; i < testLFsAllEquals.length; i += 2) {
      assertEquals("Run "+ i + " vs. " + (i + 1), true,
          testEqualsMultiOne(testLFsAllEquals[i], testLFsAllEquals[i+1]));
    }
  }

  /*
  public static void main(String args[]) {
    new DagTest().testEqualsMultiSpecial();
  }
  */
}
