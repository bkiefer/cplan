package de.dfki.lt.tr.dialogue.cplan;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class LFParserTest {

  public static final boolean PRINT = false;

  // TODO MAKE STRING EXAMPLE WORK THE WAY IT SHOULD (CONSISTENTLY)
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
    "@a:b(foo ^ <bar>\"string value\")"
  };

  private static String[] rawLFs = {
    "provide(answer)",
    "@raw:provide(answer)",
    "provide(answer, value=\"foo bar\")",
    "@raw:provide(answer ^ <value>\"foo bar\")",
    "provide(answer, value=foo, text=bar)",
    "@raw:provide(answer ^ <value>foo ^ <text>bar)"
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
      DagNode.mapFeatureToNumber(feature);
    }
    for (String feature : relations) {
      DagNode.mapFeatureToNumber(feature);
    }
  }

  private void testOne(String lf, int i) throws IOException {
    LFParser lfparser = new LFParser(new Lexer(new StringReader(lf)));
    lfparser.setErrorVerbose(true);
    DagNode res = null;
    if (lfparser.parse())
      res = lfparser.getResultLF();
    DagNode.usePrettyPrinter();
    String lfString = (res == null) ? null : res.toString();
    if (PRINT) { System.out.println(lf); System.out.println(lfString); }
    assertEquals("Run "+ i + " " + lf, lf, lfString);
  }

  @Test public void testLFs() throws IOException {
    int i = 0;
    for (String lf : testLFs) {
      testOne(lf, ++i);
    }
  }

  private void testOneRaw(String raw, String lf, int i) throws IOException {
    LFParser lfparserRaw = new LFParser(new Lexer(new StringReader(raw)));
    lfparserRaw.setErrorVerbose(true);
    LFParser lfparser = new LFParser(new Lexer(new StringReader(lf)));
    lfparser.setErrorVerbose(true);
    DagNode res = null, rawRes = null;
    if (lfparserRaw.parse())
      rawRes = lfparserRaw.getResultLF();
    if (lfparser.parse())
      res = lfparser.getResultLF();

    String lfString = (res == null) ? null : res.toString();
    if (PRINT) { System.out.println(lf); System.out.println(lfString); }
    assertEquals("Run "+ i + " " + lf, res, rawRes);
  }

  @Test public void testRawLFs() throws IOException {
    for(int i = 0; i < rawLFs.length; i += 2) {
      testOneRaw(rawLFs[i], rawLFs[i+1], i>>1 + 1);
    }
  }

  private void testOneExt(String lf, int i) throws IOException {
    LFParser lfparser = new LFParser(new Lexer(new StringReader(lf)));
    lfparser.setExtMode(true);
    lfparser.setErrorVerbose(true);
    DagNode res = null;
    if (lfparser.parse())
      res = lfparser.getResultLFs().get(0);
    String lfString = (res == null) ? null : res.toString();
    if (PRINT) { System.out.println(lf); System.out.println(lfString); }
    assertEquals("Run "+ i + " " + lf, lf, lfString);
  }

  @Test public void testExtLFs() throws IOException {
    int i = 0;
    for (String lf : testLFs) {
      testOneExt(lf, ++i);
    }
  }

  /*
  @Test
  public void testExtLfDisj() throws IOException {
    String testItem = "@d:dvp(\n" +
        "<SpeechAct>( greeting | closing | acknowledgementName | confirmName |\n"+
        " provideQuestion | provideChoiceAnswersQuiz | provideChoiceAnswersQuiz" +
        " | provideChoiceAnswersQuiz) ^\n" +
        "<currentGame>(unknown | quiz | imitation | dance) ^\n" +
        "<gameImitation>(unknown | first | notfirst) ^\n" +
        "<gameQuiz>(unknown | first | notfirst) ^\n" +
        "<gameDance>(unknown | first | notfirst) ^\n" +
        "<childName>(unknown | Gianni)^\n" +
        "<childGender>(unknown | m | f) ^\n" +
        "<robotName>(unknown | Nao) ^\n" +
        "<robotGender>(unknown | m | f) ^\n" +
        "<familiarityCond>(unknown | yes | no) ^\n" +
        "<asrResult>(unknown | empty)  ^\n" +
        "\n" +
        "<frameOfReference>unknown ^\n" +
        "<systemGesture>unknown ^\n" +
        "<moveAction>unknown ^\n" +
        "<moveObject>unknown ^\n" +
        "<performanceMovements>unknown ^\n\n" +
        "<questionQuiz>unknown ^\n" +
        " <answerQuiz1>unknown ^\n" +
        " <answerQuiz2>unknown ^\n" +
        " <answerQuiz3>unknown ^\n" +
        " <answerQuiz4>unknown ^\n" +
        "<correctAnswerQuiz>(unknown | 1 | 2 | 3 | 4 | 5) ^\n" +
        "<performanceQuestions>(unknown | 1 | 2) ^\n" +
        "<selectedAnswer>(unknown | 1 | 2 | 3 | 4 | 5) ^\n" +
        "<questionCount>(unknown | 1 | 2 | 3))\n";
    ExtLFParser lfparser = new ExtLFParser(new Lexer(new StringReader(testItem)));
    lfparser.setErrorVerbose(true);
    DagNode res = null;
    assertTrue(lfparser.parse());
    int lfs = lfparser.getResultLFs().size();
    assertEquals(80621568, lfs);
  }
   */
}
