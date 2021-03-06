package de.dfki.lt.tr.dialogue.cplan;

import java.io.StringReader;

import static org.junit.Assert.*;
import org.junit.Test;

public class LexerTest {

  public static final boolean PRINT = false;

  private static String testString1 =
    "A_aT2-> :#s-s_s0e -><Feature>(b:c) //asldkfjlaje / jasyel/kld\n" +
    "bbla^ | /*lkajsd;f yua\n" +
    "sdaljf *& /  asdjf /*\n" +
    "***\n" +
    "*/_bla\n" + " # \n" +
    "@<##VAR_0< ! \";lkja-> <sfdy \\\" ;lka^sd|jfl \\alsd\\\\fja\"" +
    "=>5=5";

  private static int[] test1Tokens = {
    RuleParser.Lexer.ID, '>', ':', RuleParser.Lexer.VAR, RuleParser.Lexer.ARROW,
    '<', RuleParser.Lexer.ID, '>', '(', RuleParser.Lexer.ID, ':',
    RuleParser.Lexer.ID, ')',
    RuleParser.Lexer.ID, '^', '|', RuleParser.Lexer.ID, '#', '@',
    '<', RuleParser.Lexer.GVAR, '<', '!', RuleParser.Lexer.STRING,
    RuleParser.Lexer.ARROW, RuleParser.Lexer.ID, '=', RuleParser.Lexer.ID,
  };

  private static String[] test1Lvals = {
    "A_aT2-", null, null, "s-s_s0e", "->",
    null, "Feature", null, null, "b", null,
    "c", null,
    "bbla", null, null, "_bla", null, null,
    null, "VAR_0", null, null, ";lkja-> <sfdy \" ;lka^sd|jfl alsd\\fja",
    "=>", "5", null, "5"
  };

  @Test public void scanString() throws java.io.IOException {
    Lexer yylex = new Lexer(new StringReader(testString1));
    int token = yylex.yylex();
    int i = 0;
    while (token != RuleParser.Lexer.EOF) {
      if (PRINT)
        System.out.println(yylex.getTokenName(token) + " : " + yylex.getLVal());
      assertEquals(i, test1Tokens[i], token);
      assertEquals(Integer.toString(i), test1Lvals[i], yylex.getLVal());
      ++i;
      token = yylex.yylex();
    }
  }

  public static void main(String[] args) throws java.io.IOException {
    System.out.println(testString1);
    new LexerTest().scanString();
  }

}