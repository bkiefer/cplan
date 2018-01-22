package de.dfki.lt.tr.dialogue.cplan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.tr.dialogue.cplan.functions.FunctionFactory;
import de.dfki.lt.tr.dialogue.cplan.io.LFDagPrinter;

public class UttplanParserTest {

  private boolean _print = false;

  // Parse test patterns
  private static String[] parseTestPatternsNegative =
  {
    // 0 : no rvars on right hand side
    ":dvp, ###foo ^ :bla -> # ^ bar",

    // 1 : paths only for global vars on lhs
    ":dvp -> #glob <feat> ^ bar",

    // 2 : wrong number of arguments for math function
    ":dvp -> # ^ <result> eq(3).",

    // 3 : wrong number of arguments for math function
    ":dvp, eq(3) ^ :no -> # ^ <result> yes.",

    // 4 : unknown function
    ":dvp, foo(3) ^ :no -> # ^ <result> yes.",

    // 5 : unknown function
    ":dvp -> # ^ <result> foo(3).",

    // 6 check rule groups need initial expression
    "{ ^ <foo> bar -> # ^ <bar> foo. ^ <foo> baz -> # ^ <baz> foo. }",

    // 7 check rule groups: group obligatory
    ":dvp { ^ <foo> bar -> # ^ <bar> foo. ^ <g> a }"
  };


  // Match test patterns
  private static String[][] matchTestPatternsPositive =
  {
    // 0 very simple example, check rule syntax
    { "prop -> # ^ prop.", "@a:b(prop)", "@a:b(prop)" },

    // 1 bind local var
    { "#root: -> #root ^ <test> test.", "@a:b(p)", "@a:b(p ^ <test> test)"},

    // 2 match feat-val pair, and test variable bindings
    { "#var:#type ^ #prop ^ <test> test -> " +
      "# ^ <test3> (d:#type ^ #prop) ^ <test2> test2.",
      "@a:b(p ^ <test> test)",
      "@a:b(p ^ <test> test ^ <test3>(d:b ^ p) ^ <test2> test2)",
      "@a:c(p ^ <test> test)",
      "@a:c(p ^ <test> test ^ <test3>(d:c ^ p) ^ <test2> test2)"
    },

    // 3 match proposition
    { "prop -> # ^ <test> test.",
      "@a:b(prop ^ <test2> test)", "@a:b(prop ^ <test2> test ^ <test> test)"
    },

    // 4 match a conjunction
    { "<t> test ^ <t2> t2 -> # ^ <t3> t3.",
      "@a:b(p ^ <t> test ^ <t2> t2)", "@a:b(p ^ <t> test ^ <t2> t2 ^ <t3> t3)",
    },

    // 5 match a complex nominal
    { "<t> (a:b ^ prop ^ <f> g) -> # ^ <t2> yes.",
      "@b:c(p ^ <t> (a:b ^ prop ^ <f> g ^ <g> h))",
      "@b:c(p ^ <t> (a:b ^ prop ^ <f> g ^ <g> h) ^ <t2> yes)",
    },

    // 6 match alternatives
    { "<t1> t1 | <t2> t2 -> # ^ <t3> t3.",
      "@a:b(p ^ <t1> t1)", "@a:b(p ^ <t1> t1 ^ <t3> t3)",
      "@a:b(p ^ <t2> t2)", "@a:b(p ^ <t2> t2 ^ <t3> t3)",
    },

    // 7 path expression
    { "<t1> <t2> t1 -> # ^ <t1> :d .",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1))",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1) ^ <t1> (:d))"
    },

    // 8 more complex conjunction
    { "<t1> (<t2> t1) -> # ^ <t1> (<t1> t2) ^ <t3> t3.",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1))",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1) ^ <t1>(<t1> t2) ^ <t3> t3)"
    },

    // 9 local variable assignment id
    { "<t1> (#i: ^ <t2> t1) -> #i ^ <t1> t2, # ^ <t3> (#i:).",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1))",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1 ^ <t1> t2) ^ <t3> (b: ))"
    },

    // 10 negation 1
    { "<t1> ! t1 -> # ^ <t2> t2.",
      "@a:b(p ^ <t1> t2)", "@a:b(p ^ <t1> t2 ^<t2> t2)"
    },

    // 11 negation 2
    { "<t1> ! <t1> -> # ^ <t2> t2.",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t2> t2))",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t2> t2) ^ <t2> t2)"
    },

    // 12 negation 3
    { "<t1> <t2> ! t3 -> # ^ <t2> t2.",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t2> t2))",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t2> t2) ^ <t2> t2)"
    },

    // 13 local variable assignment id with unification, multiple actions
    { "<t1> (#i: ^ <t2> t1) -> #i ^ (<t1> t2), # ^ <t3> (#i: ^ <t3> t3 ^ <t2> t1).",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1))",
      "@a:b(p ^ <t1> (b:c ^ p ^ <t2> t1 ^ <t1> t2 ^ <t3> t3) ^ <t3> (b:))"
    },

    // 14 short forms for types, props, etc
    { "<t1> :t ^ <t1> id: ^ <t1> prop -> # ^ <t3> t3.",
      "@a:b(foo ^ <t1> (id:t ^ prop))",
      "@a:b(foo ^ <t1> (id:t ^ prop) ^ <t3> t3)"
    },

    // 15 deletion of features
    { "<t1> :t ^ <t1> id: ^ <t1> prop -> # ^ <t3> t3, # ! <t1>.",
      "@a:b(foo ^ <t1> (id:t ^ prop))",
      "@a:b(foo ^ <t3> t3)"
    },

    // 16 replacement action
    { "<t1> (id:t ^ prop ^ #i:) -> #i = (:f ^ <t3> #i:).",
      "@a:b(foo ^ <t1> (id:t ^ prop))",
      "@a:b(foo ^ <t1> (:f ^ <t3> (id:t ^ prop)))"
    },

    // 17 establish a coreference between two features
    { "<foo> #a: ^ <bar> #b: -> #a ^ #b:.",
      "@d1:dvp(c-goal ^ <foo> (:b ^ prop) ^ <bar> (a:b))",
      "@d1:dvp(c-goal ^ <foo> (a:b ^ prop) ^ <bar> (a:b))"
    },

    // 18 embedded variables
    { ":dvp ^ <C> ( #v: ) -> " +
      "#v = (:modal ^ <A>(###a:e) ^ <E>(:p ^ <A> ###i:) ^ <P>(###i:ps) ^ <S> ###a:).",

      "@d1:dvp( cplan ^ <C> ( nom1: ) )",

      "@d1:dvp( cplan ^ <C> " +
      "(:modal ^ <A>(n1:e ) ^ <E>(:p ^ <A> (n2:)) ^ <P>(n2:ps) ^ <S> (n1:)))",
    },

    // 19 Anonymous variables, empty nominal node
    { ":dvp ^ ! <Content> -> # ^ <Content> ( ###v: ).",
      "@d1:dvp( cplan )",
      "@d1:dvp( cplan ^ <Content> ( nom1: ) )",
    },

    // 20 success match with function call
    { ":dvp ^ ! <Content> ^ (constant1() ~ 1) -> # ^ <Content> ( ###v: ).",
      "@d1:dvp( cplan )",
      "@d1:dvp( cplan ^ <Content> ( nom1: ) )",
    },

    // 21 disjunction of types
    { ":a ^ <T>#t:(e1 | e2) -> # ^ <C>(#t:) ^ <S>(#t:), # ! <T>.",
      "@d1:a( cp ^ <T>(y:e2 ^ prop) )",
      "@d1:a( cp ^ <S>(y:e2 ^ prop) ^ <C>y:e2)"
    },

    // 22 disjunction of types, with variable
    { ":a ^ <T>#t:(e1 | #type) -> # ^ <C>(#t:) ^ <S>(:#type), # ! <T>.",
      "@d1:a( cp ^ <T>(y:z ^ prop) )",
      "@d1:a( cp ^ <C>(y:z ^ prop) ^ <S>(:z))"
    },

    // 23 correctly match one of multiple relations with equal name
    { "<C>(#c1: ^ <M>(#m:d) ^ ! <M><C>) -> #c1 ^ <M>(<C>#m:).",
      "@a:dvp(p ^ <C>(c1:d1 ^ e1 ^ <M>(c:d ^ e) ^ <M>(f:g ^ h)))",
      "@a:dvp(p ^ <C>(c1:d1 ^ e1 ^ <M>(c:d ^ e) ^ <M>(f:g ^ h) ^ <M>( <C> c:d)))"
    },

    // 24 correctly match one of multiple relations with equal name
    { "<C>(#c1: ^ <M>(#m:g) ^ ! <M>(<C>)) -> #c1 ^ <M>(<C>#m:).",
      "@a:dvp(p ^ <C>(c1:d1 ^ e1 ^ <M>(c:d ^ e) ^ <M>(f:g ^ h)))",
      "@a:dvp(p ^ <C>(c1:d1 ^ e1 ^ <M>(c:d ^ e) ^ <M>(f:g ^ h) ^ <M>( <C> f:g)))"
    },

    // 25 RVAR test
    { ":dvp ^ <SpeechAct>opening ^ <Content> (#c1:top) -> " +
      " ###opening1 = :opening ^ hi, ###opening2 = :opening ^ hello," +
      " # ! <SpeechAct>, #c1 = ###opening1:. ",
      "@d1:dvp( <SpeechAct>opening ^ <Content> (:top) )",
      "@d1:dvp( <Content> (:opening ^ hi) )"
    },

    // 26 RVAR test, esp. moving simple values
    { ":dvp ^ <SpeechAct>opening ^ <Content> (#c1:top) -> " +
      " ###opening1 = opening, ###opening2 = hello," +
      " # ! <SpeechAct>, #c1 = ( :###opening1 ^ ###opening2 ). ",
      "@d1:dvp( <SpeechAct>opening ^ <Content> (:top) )",
      "@d1:dvp( <Content> (:opening ^ hello) )"
    },

    // 27 checking equal type values on lhs
    { ":dvp ^ <C> <A> :#v ^ <C> <B> #c:#v -> #c ^ :no.",
      "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :yes ) ) )",
      "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :no ) ) )"
    },

    // 28 checking complex equal values on lhs
    { ":dvp ^ <C> <A> #v: ^ <C> <B> #v: ^ <C> <B> #c: -> #c ^ :no.",
      "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :yes ) ) )",
      "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :no ) ) )"
    },

    // 29 checking complex equal values with corefs on lhs
    { ":dvp ^ <Content>#c: ^ <Wh-Restr>#c: -> # ^ :equ.",
      "@a:dvp( <Content>(:ttype ^ prop ^ <List>( <First> (c:b)" +
                                              "^ <Rest> (<First>(c:b))))" +
            "^ <Wh-Restr>(:ttype ^ prop ^ <List>( <First> (d:b)" +
                                               "^ <Rest> (<First>(d:b)))) )",
      "@a:equ( <Content>(:ttype ^ prop ^ <List>( <First> (c:b)" +
                                               "^ <Rest> (<First>(c:b))))" +
             "^ <Wh-Restr>(:ttype ^ prop ^ <List>( <First> (d:b)" +
                                                "^ <Rest> (<First>(d:b)))) )"
    },

    // 30 checking corefs on lhs
    { ":dvp ^ <C> <A> #v: ^ <C> <B> <D> =#v: -> #v ^ :no.",
      "@d1:dvp( <C> ( <A> ( d2:yes ) ^ <B> ( <D> (d2:yes) ) ) )",
      "@d1:dvp( <C> ( <A> ( d2:no ) ^ <B> ( <D> (d2:no) ) ) )"
    },

    // 31 checking corefs on lhs
    { ":dvp ^ <C> <B> <D> #v: ^ <C> <A> = #v: -> #v ^ :no.",
      "@d1:dvp( <C> ( <A> ( d2:yes ) ^ <B> ( <D> (d2:yes) ) ) )",
      "@d1:dvp( <C> ( <A> ( d2:no ) ^ <B> ( <D> (d2:no) ) ) )"
    },

    // 32 check buildin functions +
    { ":dvp -> # ^ <result> add(2, 3).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 5 )"
    },

    // 33 check buildin functions -
    { ":dvp -> # ^ <result> sub(2, 3).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> \"-1\" )"
    },

    // 34 check buildin functions *
    { ":dvp -> # ^ <result> mult(2, 3).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 6 )"
    },

    // 35 check buildin functions /
    { ":dvp -> # ^ <result> div(5, 2).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> \"2.5\" )"
    },

    // 36 check buildin functions <
    { ":dvp -> # ^ <result> lt(div(9, 3), 3) ^ <res2> lt(2, 3).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 0 ^ <res2> 1)"
    },

    // 37 check buildin functions <=
    { ":dvp -> # ^ <result> lteq(div(9, 3), 3) ^ <res2> lteq(4, 3).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 1 ^ <res2> 0)"
    },

    // 38 check buildin functions >
    { ":dvp -> # ^ <result> gt(4, 3) ^ <res2> gt(2, 3).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 1 ^ <res2> 0)"
    },

    // 39 check buildin functions >=
    { ":dvp -> # ^ <result> gteq(4, 3) ^ <res2> gteq(2, 3).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 1 ^ <res2> 0)"
    },

    // 40 check buildin functions ==
    { ":dvp -> # ^ <result> eq(div(8, 2), 3) ^ <res2> eq(3, div(9,3)).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 0 ^ <res2> 1)"
    },

    // 41 check buildin functions !
    { ":dvp -> # ^ <result> not(eq(4, 3)) ^ <res2> not(eq(4,4)).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 1 ^ <res2> 0)"
    },

    // 42 check buildin functions - (unary)
    { ":dvp -> # ^ <result> neg(sub(3, 4)) ^ <res2> neg(5).",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 1 ^ <res2> \"-5\")"
    },

    // 43 check buildin functions: clone
    { ":dvp ^ <a> #a: -> # ^ <result> clone(#a):.",
      "@d1:dvp( <a> (:b)  )",
      "@d1:dvp( <a> (:b) ^ <result> (:b) )"
    },

    // 44 see what happens without clone
    { ":dvp ^ <a> #a: -> # ^ <result> #a:.",
      "@d1:dvp( <a> (:b)  )",
      "@d1:dvp( <a> (nom1:b) ^ <result> (nom1:) )"
    },

    // 45 check build-in function concatenate
    { ":dvp ^ <a> #a ^<b> #b -> # ^ <result> concatenate(#a, #b):.",
      "@d1:dvp( <a> \"let \" ^ <b> \"loose\"  )",
      "@d1:dvp( <a> \"let \" ^ <b> \"loose\" ^ <result> \"let loose\" )"
    },

    // 46 check rule groups
    { ":dvp  { ^ <foo> bar -> # ^ <bar> foo, # ! <foo>. "+
             " ^ <foo> baz -> # ^ <baz> foo, # ! <foo>.}",
      "@d1:dvp( <foo> bar )", "@d1:dvp( <bar> foo )",
    },

    // 47 check rule groups
    { ":dvp { ^ <foo> bar -> # ^ <bar> foo. "+
             "^ <foo> baz -> # ^ <baz> foo. }",
      "@d1:dvp( <foo> bar )", "@d1:dvp( <foo> bar ^ <bar> foo )",
    },

    // 48 check rule groups, multi embedding
    { ":dvp ^ #top: | (atom ^ :type ^ (random(1,2,3) ~ 1))" +
    		" | (##global ~ = #top:) " +
    		"{ ^ <foo> bar -> # ^ <bar> foo. "+
             " ^ <foo> (#y:baz) { ^ #x -> # ! <foo>, # ^ <baz> #y:. } }",
      "@d1:dvp( <foo> bar )", "@d1:dvp( <foo> bar ^ <bar> foo )",
    },

    // 49 check non-numeric arg does not break math functions
    { ":dvp ^ <a>#arg1: ^ ! (mult(neg(b),#arg1) ~ 1) -> # ^ <result> 1.",
      "@d1:dvp( <a> b )",
      "@d1:dvp( <a> b ^ <result> 1)"
    },

    // 50 check buildin functions: warning
    { ":dvp ^ <a> -> # ^ <result> warning(\"warning\").",
      "@d1:dvp( <a> (:b) )",
      "@d1:dvp( <a> (:b) ^ <result> true )"
    },

    // 51 check rule groups
    { ":dvp  { ^ <a>b { ^ <foo> bar -> # ^ <bar> foo, # ! <foo>. "+
      "                 ^ <foo> baz -> # ^ <baz> foo, # ! <foo>. }"+
      "        ^ <a>c { ^ <foo> bar -> # ^ <bar> foo, # ! <foo>. "+
      "                 ^ <foo> baz -> # ^ <baz> foo, # ! <foo>. }}",
      "@d1:dvp( <a> b ^ <foo> bar )", "@d1:dvp( <a> b ^ <bar> foo )",
    },

    // 52 check special edges
    { ":special ^ <foo>(#foo: ^ ! <__TYPE> ^ ! <__PROP> ^ ! <__ID> )"+
      "-> #foo ^ (a:b ^ c).",
      "@d:special(<foo>(<bar>baz))",
      "@d:special(<foo>(a:b ^ c ^ <bar>baz))"
    },

    // 53 test type replacement
    { ":foo ^ <Feat> (:#prop ^ :prop) -> #prop = replace.",
      "@f:foo(<Feat>(:prop))",
      "@f:foo(<Feat>(:replace))",
    },

    // 54 test prop replacement
    { ":foo ^ <Feat> (#prop ^ prop) -> #prop = replace.",
      "@f:foo(<Feat>prop)",
      "@f:foo(<Feat>replace)",
    },

    // 55 check rule groups with shared actions
    { ":dvp  { ^ <foo> bar -> # ^ <bar> foo { ^ <a>b -> # ! <foo>. ^ <a>c -> # ^ <foo>bar. }"+
      "        ^ <foo> baz -> # ^ <baz> foo { ^ <a>b -> # ! <foo>. ^ <a>c -> # ^ <foo>baz. } }",
      "@d1:dvp( <a> b ^ <foo> bar )", "@d1:dvp( <a> b ^ <bar> foo )",
    },

    // 56 check global vars
    { "! ##g: -> ##g = \"hit\", # ^ <hit>random(##g, \"hit\").",
      "@d1:dvp( <a> b )", "@d1:dvp( <a> b ^ <hit>hit)"
    },

    // 57 test contains
    { ":foo ^ <Feat>#f ^ (contains(#f, \"bla\") ~ 1) -> # ^ <Success> \"1\".",
      "@f: foo(<Feat>\"blabla\")", "@f: foo(<Feat>\"blabla\" ^ <Success>\"1\")"
    },

    // 58 test endswith
    { ":foo ^ <Feat>#f ^ (endswith(#f, \"bla\") ~ 1) -> # ^ <Success>\"1\".",
      "@f: foo(<Feat>\"blabla\")", "@f: foo(<Feat>\"blabla\" ^ <Success>\"1\")"
    },

    // 59 test substring
    { ":foo ^ <Feat>#f -> # ^ <Success> substring(#f, 1, 3).",
      "@f: foo(<Feat>\"01234\")", "@f: foo(<Feat>\"01234\" ^ <Success>\"12\")"
    }

    /* not legal
    // 56 check rule groups with shared actions
    { ":dvp  { ^ <foo> bar -> # ^ <bar> foo, # ! <foo> { ^ <a>b {} }"+
      "        ^ <foo> baz -> # ^ <baz> foo, # ! <foo> { ^ <a>b {} }",
      "@d1:dvp( <a> b ^ <foo> bar )", "@d1:dvp( <a> b ^ <bar> foo )",
    },
    */

    /*
    // 55 test complex arguments to functions
    { "<foo> #f: ^ <foo>(!<foobar>) -> #f ^ random(<foobar>b, <foobar>b):.",
      "@f:foo(<foo>(:a))",
      "@f:foo(<foo>(:a ^ <foobar>b))"
    },

    // 56 test functions embedded in complex arguments
    { "<foo> #f: ^ <foo>(!<foobar>) -> #f ^ random(<foobar>add(2,2), <foobar>add(2,2)):.",
      "@f:foo(<foo>(:a))",
      "@f:foo(<foo>(:a ^ <foobar>4))"
    },
     */
  };


  private static String[][] matchTestPatternsNegative =
  {

    // 0 match feat-val pair
    { "<test> test -> # ^ :b ^ <test2> test2.",
      "@a:b(p)", "@a:b(p)",
      "@a:c(p ^ <test> b)", "@a:c(p ^ <test> b)",
      "@a:c(p ^ <b> test)", "@a:c(p ^ <b> test)"
    },

    // 1 match proposition
    { "prop -> # ^ <test> test.",
      "@a:b(prop2 ^ <test2> test)", "@a:b(prop2 ^ <test2> test)"
    },

    /**/
    // 2 match global var: must be bound, then add proposition to current node
    { "#top: ^ (##TEST ~ <test> test) -> # = <__PROP> prop .",
      "@a:b(prop ^ <test> test)", "@a:b( prop ^ <test> test)"
    },
    /**/

    // 3 match a conjunction
    { "<t> test ^ <t2> t2 -> # ^ <t3> t3.",
      "@a:b(p ^ <t> test ^ <t3> t2)", "@a:b(p ^ <t> test ^ <t3> t2)"
    },

    // 4 match a complex nominal
    { "<t> (a:b ^ prop ^ <f> g) -> # ^ <t2> yes.",
      "@b:c(p ^ <t> (a:b ^ prop))", "@b:c(p ^ <t> (a:b ^ prop))"
    },

    // 5 match alternatives
    { "<t1> t1 | <t2> t2 -> # ^ <t3> t3.",
      "@a:b(p ^ <t2> t3)", "@a:b(p ^ <t2> t3)"
    },

    // 6 local variable assignment proposition, the y: nominal must
    // for test purposes because otherwise they are not equal because
    // the coreference is missing
    { "// attribute.color\n" +
      "(:entity | :physical | :e-substance) ^ <Color> #v\n" +
      "->\n" +
      "# ^ <Modifier> (color:q-color ^ #v), # ! <Color>.",
      "@d1:dvp(c-goal ^ <Content>(e1:entity ^ <List>disjunction\n" +
      "        ^ <First>(b1:physical ^ ball ^ <Color>red)\n" +
      "        ^ <Next>(b2:physical ^ box ^ <Shape>tall)))",
      "@d1:dvp(c-goal ^ <Content>(e1:entity ^ <List>disjunction\n" +
      "        ^ <First>(b1:physical ^ ball\n" +
      "                  ^ <Modifier>(color:q-color ^ red))\n" +
      "        ^ <Next>(b2:physical ^ box ^ <Shape>tall)))"
    },

    // 7 negation 1
    { "<t1> ! t1 -> # ^ <t2> t2.",
      "@a:b(p ^ <t1> t1)", "@a:b(p ^ <t1> t1)"
    },

    // 8 negation 2
    { "<t1> ! <t1> -> # ^ <t2> t2.",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t1> t2))",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t1> t2))"
    },

    // 9 negation 3
    { "<t1> <t2> ! t3 -> # ^ <t2> t2.",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t2> t3))",
      "@a:b(p ^ <t1> (c:d ^ p ^ <t2> t3))"
    },

    // 10 feature existence failure
    { "<t2> -> # ^ <t3> t2.",
      "@a:b(p ^ <t1> t1)", "@a:b(p ^ <t1> t1)"
    },

    // 11 local variable assignment proposition, the y: nominal must
    // for test purposes because otherwise they are not equal because
    // the coreference is missing
    { "// attribute.color\n" +
      "(:entity | :physical | :e-substance) ^ <Color> #v\n" +
      "->\n" +
      "# ^ <Modifier> (color:q-color ^ #v).",
      "@d1:dvp(c-goal ^ <Content>(e1:entity ^ <List>disjunction\n" +
      "        ^ <First>(b1:physical ^ ball ^ <Color>red)\n" +
      "        ^ <Next>(b2:physical ^ box ^ <Shape>tall)))",
      "@d1:dvp(c-goal ^ <Content>(e1:entity ^ <List>disjunction\n" +
      "        ^ <First>(b1:physical ^ ball ^ <Color>red\n" +
      "                  ^ <Modifier>(color:q-color ^ red))\n" +
      "        ^ <Next>(b2:physical ^ box ^ <Shape>tall)))"
    },

    // 12 only meaningful for recursive test of embedded deletion
    { ":entity ^ <Shape>#v -> # ^ <Modifier>(shape:q-shape ^ #v), # ! <Shape>.",
      "@d2:dvp( bar ^ <Content> (:foo ^ <T> (:entity ^ <Shape>tall)))",
      "@d2:dvp( bar ^ " +
      "<Content> (:foo ^ <T> (:entity ^ <Modifier>(shape:q-shape ^ tall))))"
    },

    // 13 assign and test a global var
    { "p ^ (##FLAG ~ 7) -> # ^ <t2> t2. <t1> t1 -> ##FLAG = 7. ",
      "@a:b(p ^ <t1> t1)", "@a:b(p ^ <t1> t1 ^ <t2> t2)"
    },

    // 14 failure match function call
    { ":dvp ^ ! <Content> ^ (constant1() ~ 2) -> # ^ <Content> ( ###v: ).",
      "@d1:dvp( cplan )",
      "@d1:dvp( cplan ^ <Content> ( nom1: ) )",
    },

    // 15 checking equal values on lhs wrongly
    { ":dvp ^ <C> <A> :#v ^ <C> <B> #v: -> #v ^ :no.",
      "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :yes ) ) )",
      "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :no ) ) )"
    },

    // 16 check special edges
    { ":special ^ <foo>(#foo: ^ ! <__TYPE> ^ ! <__PROP> ^ ! <__ID> )"+
      "-> #foo ^ (a:b ^ c).",
      "@d:special(<foo>(:b ^ <bar>baz))",
      "@d:special(<foo>(:b ^ <bar>baz))"
    },

    // 17 test if edge does not exist
    { ":a ^ <foo>!<bar> -> # ^ <works_not>2.",
      "@b:a(<foo>(2 ^ <bar>1))", "@b:a(<foo>(2 ^ <bar>1))"
    },

    // 18 test if edge does not exist
    { ":dvp ^ <Time> #time ^ <Sammeln>(#s: ^ !<Time>) -> #s ^ <Time> #time.",
      "@b:dvp(<Time>2 ^ <Sammeln>(1 ^ <foo>1 ^ <Time>0))",
      "@b:dvp(<Time>2 ^ <Sammeln>(1 ^ <foo>1 ^ <Time>0))",
    }

  };

  private static String[][] otherPatterns =
  {
    // 0 only works with more than one application, also for print test.
    { ":a ^ <F> (#i:#t ^ #p) -> ##fs = #i:#t ^ #p. :a ^ !<T> -> # ^ <T> ##fs:.",
      "@i:a(<F>(f:ttype ^ prop))",
      "@i:a(<F>(f:ttype ^ prop) ^ <T>(f:ttype))"
    },

    // 1 global var map
    { ":dvp ^ <Content> #c: -> ##gvar <Content> = #c:." +
    	":dvp ^ !<Cont2> ^ (##gvar ~ <Content>) -> # ^ <Cont2> ( ##gvar<Content>: ).",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ))",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ) ^ <Cont2> (nom1:type))"
    },

    // 2 global var map, alternative
    { ":dvp ^ <Content> #c: -> ##gvar <Content> <First> = #c:." +
      ":dvp ^ !<Cont2> ^ (##gvar ~ <Content> <First> #v:) -> # ^ <Cont2> ( #v: ).",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ))",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ) ^ <Cont2> (nom1:type))"
    },

    // 3 equals test agains global var
    { ":dvp ^ <Content> #c: -> ##gvar = #c:." +
      ":dvp ^ !<Cont2> ^ <Content> ##gvar: ^ <Content> #v: -> # ^ <Cont2> ( #v: ).",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ))",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ) ^ <Cont2> (nom1:type))"
    },

    // 4 coref test agains global var
    { ":dvp ^ <Content> #c: -> ##gvar = #c:." +
      ":dvp ^ !<Cont2> ^ <Content> = ##gvar: -> # ^ <Cont2> ( ##gvar: ).",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ))",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ) ^ <Cont2> (nom1:type))"
    },

    // 5 check rule groups
    { ":dvp  { ^ <foo> bar -> # ^ <bar> foo. "+
             " ^ <foo> baz -> # ^ <baz> foo, # ! <foo>.}",
      "@d1:dvp( <foo> baz )", "@d1:dvp( <baz> foo )",
    },

    // 6 check rule groups
    { ":dvp { ^ <foo> bar -> # ^ <bar> foo. "+
             "^ <foo> baz -> # ^ <baz> foo. }",
      "@d1:dvp( <foo> baz )", "@d1:dvp( <foo> baz ^ <baz> foo )",
    },

    // 7 check rule groups, multi embedding
    { ":dvp { ^ <foo> bar -> # ^ <bar> foo. "+
             " ^ <foo> (#y:baz) { ^ #x:-> # ! <foo>, # ^ <baz> #y:. } }",
      "@d1:dvp( <foo> ( :baz ^ ban ) )", "@d1:dvp( <baz> ( :baz ^ ban ) )",
    },

    // 8 another negation check
    { ":ascription ^ !<PointToTarget> -> # ^ <PointToTarget> ###gvar:.",
      "@d1:dvp( <a> b ^ <foo> (b:ascription ^ <PointToTarget> (t:top)))",
      "@d1:dvp( <a> b ^ <foo> (b:ascription ^ <PointToTarget> (t:top)))",
    },

    // 9 another negation check
    { ":ascription ^ <Tense> ^ !<PointToTarget> -> # ^ <PointToTarget> a:b.",
      "@d1:dvp( <a> b ^ <foo> (b:ascription ^ <Tense> x))",
      "@d1:dvp( <a> b ^ <foo> (b:ascription ^ <Tense> x ^ <PointToTarget> (a:b)))",
    },

    // 10 proper cloning of function arguments test
    { ":dvp ^ <S> x -> # ^ :cnd ^ <out>identity(\"well done \").\n" +
    	":cnd ^ <ctxt>(<name>#n) ^ <out>#s ^ !<done> -> # ^ <out>concatenate(#s, #n) ^ <done>yes.",
      "@d:dvp(<S>x ^ <ctxt> (c:ctxt ^ <name>marco))",
      "@d:cnd(<S>x ^ <ctxt> (c:ctxt ^ <name>marco) ^ <out>\"well done marco\" ^ <done>yes)",
      "@d:dvp(<S>x ^ <ctxt> (c:ctxt ^ <name>marco))",
      "@d:cnd(<S>x ^ <ctxt> (c:ctxt ^ <name>marco) ^ <out>\"well done marco\" ^ <done>yes)",
    },

    // 11 check buildin function length
    { ":dvp ^ <val>#v ^ (length(#v) ~ 44) -> # ! <val>, # ^ <Count>yes.",
      "@d1:dvp( <val>\"The quick brown fox\n jumps over the lazy dog\")",
      "@d1:dvp( <Count> yes )",
      "@d1:dvp( <val>\"Another String\" )",
      "@d1:dvp( <val>\"Another String\" )",
    },

    // 12 check buildin function wc (word count)
    { ":dvp ^ <val>#v ^ (wc(#v) ~ 9) -> # ! <val>, # ^ <Count>yes.",
      "@d1:dvp( <val>\"The quick brown fox\n jumps over the lazy dog\")",
      "@d1:dvp( <Count> yes )",
      "@d1:dvp( <val>\"Another String\" )",
      "@d1:dvp( <val>\"Another String\" )",
    },

    // 13 check buildin function wc (word count)
    { ":dvp ^ <val>#v ^ (gt(wc(#v), 8) ~ 1) -> # ! <val>, # ^ <Count>yes.",
      "@d1:dvp( <val>\"The quick brown fox\n jumps over the lazy dog\")",
      "@d1:dvp( <Count> yes )",
      "@d1:dvp( <val>\"Another String\" )",
      "@d1:dvp( <val>\"Another String\" )",
    },

    // 14 check not multi-applied because of corefs
    { ":content ^ ! <Subject> -> # ^ <Subject>(:person ^ I).",
      "@x:b(<a>(<Cont>(a:content) ^ <Cont3>bar) ^ <Cont2>a:content)",
      "@x:b(<a>(<Cont>(a:content ^ <Subject>(:person ^ I)) ^ <Cont3>bar) ^ <Cont2>a:content)",
    },

    // 15 check ominous double subject error (missing node.dereference())
    { ":dvp ^ !<Content><__TYPE> ^ <Type>(#type:perception) -> # ^ <Content>#type:.\n" +
        ":dvp ^ <Content>(#c1:perception ^ <__TYPE> ^ !<Actor>) ^ <Actor>#a1: ->\n" +
        "#c1 ^ <Actor>#a1:.\n"  +
        ":perception ^ !<Subject> ^ <Actor>#a1: -> # ^ <Subject>#a1:.",
        "@d1:dvp(<Type>(nom1:perception ^ see) ^ <Actor>(nom3:person))",
        "@d1:dvp(<Type>(nom1:perception ^ see) ^ <Actor>(nom3:person) ^" +
        "        <Content>(nom1:perception ^ <Actor>(nom3:person) ^" +
        "                  <Subject>(nom3:person)))",
    },

    // 12 check buildin function wordcount : words
    /* There can be no newlines in the strings so this is disabled.
    // 13 check buildin function wordcount : lines
    { ":dvp ^ <val>#v ^ (wc(\"l\", #v) ~ 2) -> # ! <val>, # ^ <Count>yes.",
      "@d1:dvp( <val>\"The quick brown fox\n jumps over the lazy dog\")",
      "@d1:dvp( <Count> yes )",
      "@d1:dvp( <val>\"Another String\" )",
      "@d1:dvp( <val>\"Another String\" )",
    },
    */

    // 16 check rule groups with shared actions
    { ":dvp  { ^ <foo> bar -> # ^ <bar> foo { ^ <a>b -> # ! <foo>. ^ <a>c -> # ^ <foo>bar. }"+
      "        ^ <foo> baz -> # ^ <baz> foo { ^ <a>b -> # ! <foo>. ^ <a>c -> # ^ <foo>baz. } }",
      "@d1:dvp( <a> b ^ <foo> bar )", "@d1:dvp( <a> b ^ <bar> foo )",
      "@d1:dvp( <a> c ^ <foo> bar )", "@d1:dvp( <a> c ^ <foo> bar ^ <bar> foo )",
      "@d1:dvp( <a> b ^ <foo> baz )", "@d1:dvp( <a> b ^ <baz> foo )",
      "@d1:dvp( <a> c ^ <foo> baz )", "@d1:dvp( <a> c ^ <foo> baz ^ <baz> foo )",
    },

    // 17 equals test agains global var, with recurring rule
    { ":dvp ^ <Content> #c: => ##gvar = #c:." +
      ":dvp ^ !<Cont2> ^ <Content> ##gvar: ^ <Content> #v: -> # ^ <Cont2> ( #v: ).",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ))",
      "@d1:dvp( cplan ^ <Content> ( nom1:type ^ prop ) ^ <Cont2> (nom1:type))"
    },

    // 18 recurring rules test
    { "<c>#c: ^ <l>(#l: ^ <f>#f ^ <r>#r:) => #l = #r:, #c = concatenate(#c, #f).",
      "@d:dv(<c>\"\" ^ <l>(:li ^ <f>1 ^ <r>(:li ^ <f>2 ^ <r>(:li ^ <f>3 ^ <r>(:li)))))",
      "@d:dv(<c>123 ^<l>(:li))"
    },

    // 19 strange error test
    // BEWARE: THIS IS A GOOD EXAMPLE WHERE THE PARALLEL APPLICATION OF RULES
    // SHOWS UP: IN THE MATCHING PHASE, BOTH ARE APPLICABLE, SO 2 OVERWRITES 1
    /*
    { ":dvp ^ <Time> (todayP|todayF) ^ <Sammeln>#s: -> #s ^ <Time> \"today\"." +
      ":dvp ^ <Time> #time ^ <Sammeln>(#s: ^ !<Time>) -> #s ^ <Time> #time.",
      "@d:dvp(<Time>todayP ^ <Sammeln>(<Agent>null))",
      "@d:dvp(<Time>todayP ^ <Sammeln>(<Agent>null ^ <Time>\"today\"))"
    }
    */

  };

  @Before public void setUp() {
    //BasicConfigurator.resetConfiguration();
    //RootLogger.getRootLogger().addAppender(
    //    new ConsoleAppender(new PatternLayout("%m%n")));
    // next line is needed to initialize static fields
    @SuppressWarnings("unused")
    UtterancePlanner up = new UtterancePlanner();
    DagNode.init(new FlatHierarchy());
    DagNode.usePrettyPrinter();
    // DagNode.registerPrinter(null); // return to default printer
  }

  private RuleParser getRuleParser(String input) {
    RuleParser parser = new RuleParser(new Lexer());
    parser.reset(input, new StringReader(input));
    //parser.setDebugLevel(99);
    parser.setErrorVerbose(true);
    return parser;
  }

  private RuleParser getSilentRuleParser(String input) {
    Lexer lexer = new Lexer();
    lexer.setErrorLogger(null);
    lexer.setErrorsQuiet(true);
    RuleParser parser = new RuleParser(lexer);
    parser.reset(input, new StringReader(input));
    //parser.setDebugLevel(99);
    parser.setErrorVerbose (false);
    return parser;
  }

  private Rule getFirstRuleParsed(String input) throws IOException {
    RuleParser parser = getRuleParser(input);
    parser.parse();
    List<Rule> rules = parser.getRules();
    Rule r = rules.get(0);
    if (_print) {
      System.out.println(r); // System.exit(0);
    }
    return r;
  }

  private List<Rule> getRulesParsed(String input) throws IOException {
    RuleParser parser = getRuleParser(input);
    parser.parse();
    List<Rule> r = parser.getRules();
    if (_print) {
      for (Rule rule : r)
        System.out.println(rule); // System.exit(0);
    }
    return r;
  }

  private LFParser getLFParser(String input) {
    LFParser parser = new LFParser(new Lexer());
    parser.reset(input, new StringReader(input));
    //parser.setDebugLevel(99);
    parser.setErrorVerbose(true);
    return parser;
  }

  private DagNode getLF(String input)  throws IOException {
    LFParser lfparser = getLFParser(input);
    lfparser.parse();
    DagNode res = lfparser.getResultLF();
    return res;
  }

  private void testParsingOnePatternSet(String[] testPattern, int i)
  throws IOException {
    {
      RuleParser parser = getRuleParser(testPattern[0]);
      assertTrue("Parsing pattern " + i + " " + testPattern[0],
          (parser.parse()
              && parser.getLexer().getAllErrorPositions().isEmpty()));
      //for (Rule r : parser.getRules()) System.out.println(">>>" + r);
    }

    for (int j = 1; j < testPattern.length; ++j) {
      LFParser lfparser = getLFParser(testPattern[j]);
      assertTrue("Parsing pattern " + i + "(" + j + ") " + testPattern[j]
          , lfparser.parse());
      // System.out.println(lfparser.getLF());
    }
  }

  /*
  @Test public void testParsingSpecial() throws IOException {
    int i = 9;
    String[] testPattern = matchTestPatternsPositive[i];
    RuleParser parser =
      new RuleParser(new Lexer(new StringReader(testPattern[0])));
    if (parser.parse()) {
      for (Rule r : parser.getRules()) {
        // foo
        System.out.println(">>>" + r);
      }
    }
  }
  */

  @Test public void testParsingFailures() throws IOException {
    int i = 0;
    for (String testPattern : parseTestPatternsNegative ) {
      RuleParser parser = getSilentRuleParser(testPattern);
      // getRuleParser(testPattern);
      assertFalse("Parsing pattern " + i + " " + testPattern,
          (parser.parse()
              && parser.getLexer().getAllErrorPositions().isEmpty()));
      ++i;
    }
  }

  @Test public void testParsingPositive() throws IOException {
    int i = 0;
    for (String[] testPattern : matchTestPatternsPositive ) {
      testParsingOnePatternSet(testPattern, i);
      ++i;
    }
  }

  @Test public void testParsingNegative() throws IOException {
    int i = 0;
    for (String[] testPattern : matchTestPatternsNegative ) {
      testParsingOnePatternSet(testPattern, i);
      ++i;
    }
  }

  private void testMatchOneSet(String[] testPattern, boolean value, int i)
  throws IOException {
    Rule r = getFirstRuleParsed(testPattern[0]);

    for (int j = 1; j < testPattern.length; j += 2) {
      DagNode res = getLF(testPattern[j]);
      assertEquals("Matching " + i + "(" + j + ")", value,
          ((BasicRule)r).match(res));
    }
  }

  @Test public void testMatchSpecial() throws IOException {
    int i = -1;
    if (i >= 0) {
      _print = true;
      testMatchOneSet(matchTestPatternsPositive[i], true, i);
      _print = false;
    }
  }

  @Test public void testMatchesPositve() throws IOException {
    int i = 0;
    for (String[] testPattern : matchTestPatternsPositive) {
      testMatchOneSet(testPattern, true, i);
      ++i;
    }
  }

  @Test public void testMatchesNegative() throws IOException {
    int i = 0;
    for (String[] testPattern : matchTestPatternsNegative) {
      testMatchOneSet(testPattern, false, i);
      ++i;
    }
  }

  private void testApplyOneSet(String[] testPattern, boolean value, int i)
  throws IOException {
    Rule r = getFirstRuleParsed(testPattern[0]);

    for (int j = 1; j < testPattern.length; j += 2) {
      DagNode in = getLF(testPattern[j]);
      DagNode out = value ? getLF(testPattern[j+1]) : in.cloneFS();
      //DagNode.registerPrinter(null);
      if (_print) {
        System.out.println(out);
      }
      in = ((BasicRule)r).applyLocallyAndCopy(in);

      if (_print) {
        System.out.println(in);
      }
      if (value) {
        assertEquals("Matching pattern " + i + "(" + j + ")", out, in);
      } else {
        assertEquals("Incorrectly matching pattern " + i + "(" + j + ")", out, in);
        // assertFalse("Matching pattern " + i + "(" + j + ")", in.equals(out));
      }
    }
  }

  @Test public void testRuleApplicationSpecial() throws IOException {
    int i = -1;
    _print = true;
    if (i >= 0)
        testApplyOneSet(matchTestPatternsPositive[i], true, i);
    _print = false;
  }

  @Test public void testRuleApplicationPositive() throws IOException {
    int i = 0;
    for (String[] testPattern : matchTestPatternsPositive) {
      testApplyOneSet(testPattern, true, i);
      ++i;
    }
  }

  @Test public void testRuleApplicationNegative() throws IOException {
    int i = 0;
    //_print = true;
    for (String[] testPattern : matchTestPatternsNegative) {
      testApplyOneSet(testPattern, false, i);
      ++i;
    }
    //_print = false;
  }

  private DagNode embed(DagNode toEmbed, String edgeName) {
    DagNode result = new DagNode();
    result.addEdge(DagNode.TYPE_FEAT_ID, new DagNode("x"));
    result.addEdge(DagNode.getFeatureId(edgeName), toEmbed);
    result.setNominal();
    return result;
  }

  private void testApplyOne(UtterancePlanner up, String[] testPattern,
    boolean value, int i, boolean embedded) {
    for (int j = 1; j < testPattern.length; j += 2) {
      DagNode in = DagNode.parseLfString(testPattern[j]);
      DagNode out = DagNode.parseLfString(testPattern[j+1]);
      if (embedded) {
        in = embed(embed(in, "XXX"), "YYY");
        out = embed(embed(out, "XXX"), "YYY");
      }
      if (_print) {
        System.out.println("> "+in);
      }
      in = up.process(in);
      if (_print) {
        System.out.println("<< "+in);
      }
      // remove invisible prop coreferences by printing and re-reading
      in = DagNode.parseLfString(in.toString());
      if (_print) {
        System.out.println("< "+in);
        System.out.println(out);
      }
      if (value) {
        assertEquals("Matching pattern " + i + "(" + j + ")", out, in);
      } else {
        assertNull("Incorrectly matching pattern " + i + "(" + j + ")", in);
      }
    }
  }

  private void testRecursiveApplicationOne(String[] testPattern,
      boolean value, int i, RuleTracer rt, boolean embed) {
    UtterancePlanner up = new UtterancePlanner();
    up.MAX_ITERATIONS = 10; // if there's an infinite loop, get it fast
    up.addProcessor(up.readRulesFromString(testPattern[0]));
    if (rt != null) {
      up.setTracing(rt);
    }
    testApplyOne(up, testPattern, value, i, embed);
  }

  private void testRecursiveApplicationOne(String[] testPattern,
    boolean value, int i, RuleTracer rt) {
    testRecursiveApplicationOne(testPattern, value, i, rt, false);
  }

  @Test public void testRecSpecial() throws IOException {
    int i = -1;
    // DagNode.useDebugPrinter();
    String[] pattern = (i >=0 ? otherPatterns[i] : null);
    _print = true;
    if (pattern != null)
      testRecursiveApplicationOne(pattern, true, i, null, false);
    _print = false;
  }


  @Test public void testRec() throws IOException {
    //Rule.setTracing(new LoggingTracer(-1));
    testRecursiveApplicationOne(matchTestPatternsNegative[6], true, 6, null);
    // runs into an infinite loop because <Modifier>s are added all the time
    //testRecursiveApplicationOne(matchTestPatternsNegative[11], true, 1);
    testRecursiveApplicationOne(matchTestPatternsPositive[18], true, 18, null);
    testRecursiveApplicationOne(matchTestPatternsNegative[12], true, 12, null);
    testRecursiveApplicationOne(matchTestPatternsNegative[13], true, 13, null);
    for (int i = 0 ; i < otherPatterns.length; ++i) {
      testRecursiveApplicationOne(otherPatterns[i], true, i, null);
    }
  }

  @Test public void testRecEmbedded() throws IOException {
    for (int i = 0 ; i < otherPatterns.length; ++i) {
      testRecursiveApplicationOne(otherPatterns[i], true, i, null, true);
      ++i;
    }
  }

  @Test public void testTracing() throws IOException {
    ArrayList<TraceEvent> events = new ArrayList<TraceEvent>();
    RuleTracer rt = new CollectEventsTracer(events);
    testRecursiveApplicationOne(otherPatterns[17], true, 17, rt);
    // runs into an infinite loop because <Modifier>s are added all the time
    //testRecursiveApplicationOne(matchTestPatternsNegative[11], true, 1);
    assertEquals(4, events.size());
  }

  @Test public void testUntracing() throws IOException {
    CollectEventsTracer rt = new CollectEventsTracer();
    UtterancePlanner up = new UtterancePlanner();
    String[] testPattern = otherPatterns[17];
    up.addProcessor(up.readRulesFromString(testPattern[0]));
    up.setTracing(rt);
    for (int j = 1; j < testPattern.length; j += 2) {
      DagNode in = DagNode.parseLfString(testPattern[j]);
      DagNode out = DagNode.parseLfString(testPattern[j+1]);
      in = up.process(in);
      assertEquals("Matching pattern " + 17 + "(" + j + ")", out, in);
    }
    assertEquals(4, rt.getEvents().size());

    up.setTracing(null);
    for (int j = 1; j < testPattern.length; j += 2) {
      DagNode in = DagNode.parseLfString(testPattern[j]);
      DagNode out = DagNode.parseLfString(testPattern[j+1]);
      in = up.process(in);
      assertEquals("Matching pattern " + 3 + "(" + j + ")", out, in);
    }
    assertEquals(4, rt.getEvents().size());
    }

  @Test
  public void testBuiltinExceptionFunctionLHS() throws IOException {
    String[] pat =    // 15 checking equal values on lhs wrongly
    { ":dvp ^ (throwException(\"LHS\") ~ :no) -> # ^ <no> yes.",
        "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :yes ) ) )",
        "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :no ) ) )"
    };
    String msg = "";
    try {
      testApplyOneSet(pat, true, 0);
    } catch (PlanningException ex) {
      msg = ex.getMessage();
    }
    assertEquals("LHS", msg);
  }

  @Test
  public void testBuiltinExceptionFunctionRHS() throws IOException {
    String[] pat =    // 15 checking equal values on lhs wrongly
    { ":dvp ^ <C> -> # ^ throwException().",
        "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :yes ) ) )",
        "@d1:dvp( <C> ( <A> ( :yes ) ^ <B> ( :no ) ) )"
    };
    String msg = "FOO";
    try {
      testApplyOneSet(pat, true, 0);
    } catch (PlanningException ex) {
      msg = ex.getMessage();
    }
    assertEquals(null, msg);
  }

  @Test public void testRandomFunction() throws IOException {
    String rule =
      ":type ^ prop ^ ! <added> ^ (random(1,2,3) ~ 2) -> # ^ <added> true.";
    String inSpec = "@a:type(prop)";
    String outSpec = "@a:type(prop ^ <added> true)";
    UtterancePlanner up = new UtterancePlanner();
    up.addProcessor(up.readRulesFromString(rule));
    DagNode.usePrettyPrinter();
    DagNode in = DagNode.parseLfString(inSpec);
    DagNode out = DagNode.parseLfString(outSpec);
    int runs = 0;
    do {
      DagNode res = up.process(in);
      // remove invisible prop coreferences by printing and re-reading
      res = DagNode.parseLfString(res.toString());
      ++ runs;
      if (res.equals(out)) {
        System.out.println(runs); break;
      }
    } while (true);
    assertTrue(true);
  }

  /** Test if a directory with plugins can be loaded and used */
  @Test public void testPluginRegistration() throws IOException {
    assertNull(FunctionFactory.get("test"));
    // Test that passing null is OK
    FunctionFactory.registerPlugins(null, null);
    // Pass proper directory
    String baseDir = System.getProperty("basedir");
    File pluginDirectory = ((baseDir == null)
        ? new File("target/plugins/")
        : new File(baseDir, "target/plugins/"));

    FunctionFactory.registerPlugins(pluginDirectory, null);
    String[] testPattern = {
      ":type ^ prop ^ ! <added> ^ (test(1,2,3) ~ 2) -> # ^ <added>9.",
      "@d:type(prop)",
      "@d:type(prop ^ <added> 9)"
    };
    testApplyOneSet(testPattern, true, 0);
  }

  @Test(expected= IllegalArgumentException.class)
  public void testPluginDirectoryWrong() throws IOException {
    FunctionFactory.registerPlugins(
        new File("src/test/resources/plak/"), null);
  }

  @Test public void testPrinting() throws IOException {
    Rule r = getFirstRuleParsed(
        ":a ^ <F> (#i:#t ^ #p) ^ (eq(3,3) ~ 1) "
        + "-> ##fs = #i:#t ^ #p ^ <res> eq(3,3).");
    DagNode.usePrettyPrinter();
    String rString = r.toString();
    assertEquals("((:a ^ <F> ((#i: ^ :#t) ^ #p)) ^ (eq(3, 3) ~ 1)) " +
    		"-> ##fs = @#i:#t(#p ^ <res>eq(3, 3)) .",
        rString);
    DagNode.useDebugPrinter();
    String dString = r.toString();
    assertEquals("((:a ^ <F> ((#i: ^ :#t) ^ #p)) ^ (eq([@3@], [@3@]) ~ 1)) " +
    		"-> ##fs = [ #i: :#t #p res[ eq([@3@], [@3@])]] .",
        dString);
//    assertEquals("((:a ^ <F> ((#i: ^ :#t) ^ #p)) ^ (eq([ 3], [ 3]) ~ 1)) " +
//    		"-> ##fs = [ #i: :#t #p res[ eq([ 3], [ 3])]] .",
//        dString);
  }

  public void testPrintParseOne(String pattern, int i) throws IOException {
    LFDagPrinter lfd = new LFDagPrinter();
    lfd.setRuleMode(true);
    DagNode.registerPrinter(lfd);
    List<Rule> rules = getRulesParsed(pattern);
    //System.out.println("#   " + pattern);
    int j = 1;
    for (Rule r : rules) {
      String ruleString = r.toString();
      //System.out.println("### " + ruleString);
      Rule r2 = null;
      try {
        r2 = getFirstRuleParsed(ruleString);
      }
      catch (IndexOutOfBoundsException ioex) {
      }
      assertNotNull("Printed Rule " + i + "(" + j + ")" +
          " not parseable : <"+ruleString+">", r2);
    }
  }

  /* This assumes that rules are printed in a way that allows to read them in
   * back again, which is currently not the case
   * \todo fix this
   */
  @Test public void testPrintAndParse() throws IOException {
    int i = 0;
    for (String[] testPattern : matchTestPatternsPositive) {
      testPrintParseOne(testPattern[0], i);
      ++i;
    }

    i = -1;
    for (String[] testPattern : matchTestPatternsNegative) {
      testPrintParseOne(testPattern[0], i);
      --i;
    }
  }

  /* This assumes that rules are printed in a way that allows to read them in
   * back again, which is currently not the case
   * \todo fix this
   */
  @Test public void testPrintAndParse2() throws IOException {
    int i = 0;
    for (String[] testPattern : otherPatterns) {
      testPrintParseOne(testPattern[0], i);
      ++i;
    }
  }

  private File getExampleGrammarFile() {
    String relativePath = "src/test/resources/grammars/examples.cpr";
    Properties sysProps = System.getProperties();
    String basedir = (String) sysProps.get("basedir");
    if (basedir != null) {
      return new File(basedir, relativePath);
    }
    return new File(relativePath);
  }

  /* Test loading a grammar/project from a test fixture directory */
  @Test
  public void testLoadGrammar() throws FileNotFoundException, IOException {
    UtterancePlanner up = new UtterancePlanner();
    up.readProjectFile(getExampleGrammarFile());
    assertEquals(1, up.getAllRuleFiles().size());
  }

  // Complex test patterns
  private static String[][] goodPatterns =
  {
    // Example 1
    { "", // no rule, to test grammars in file
      "@d:dvp(<SA>assertion ^ <Rel> accept ^ <Cont> (:conttype ^ <foo> bar))",
      "@d:dvp(<SA>assertion ^ <Cont>(n1:marker ^ ok) ^ <Rel>accept)"
    },

    // Example 2
    { "", // no rule, to test grammars in file
      "@aa:bb(<C>(<Mod>(:g ^ <x> y)))",
      "@aa:bb(<C>(nom1: ^ <Mod>(nom2:g ^ <Cont>(nom3:new ^ clean) ^ <x>y)))"
    },

    // Example 3
    { "", // no rule, to test grammars in file
      "@aa:ascr(<C>d)",
      "@aa:ascr(<C>d ^ <Tense>pres)"
    },

    // Example 4
    { "", // no rule, to test grammars in file
      "@d:dvp(<SpeechAct>assertion ^ <w>(:foo ^ <Tense>pres))",
      "@d:dvp(<SpeechAct>assertion ^ <w>(:foo ^ <Tense>pres ^ <Mood> ind))"
    },

    // Example 5
    { "",
      "@d:disj(<T>(:entity ^ <Tense>pres))",
      "@d:disj(<Subject>(n1:entity ^ <Tense>pres) ^ <CR>(n1:))",
      "@d:disj(<T>(:thing ^ <Tense>pres))",
      "@d:disj(<Subject>(n1:thing ^ <Tense>pres) ^ <CR>(n1:))"
    },

    // Example 6
    { "",
      "@d:disj2(<T>(:entity ^ <Tense>pres))",
      "@d:disj2(<Subject>(n1:entity ^ <Tense>pres) ^ <CR>(n1:))",
      "@d:disj2(<T>(:thing ^ <Tense>pres))",
      "@d:disj2(<Subject>(n1:thing ^ <Tense>pres) ^ <CR>(n1:))"
    },

    // Example 9
    { "", // No Rule, To Test Grammars In File
      "@d:dvp(<Speechact>Assertion ^ <Content>(a:ascription))",
      "@d:dvp(<Speechact>Assertion ^ <Content>(a:ascription ^<PointToTarget>(a:ascription)))"
    },

    // Example 11
    { "", // No Rule, To Test Grammars In File
      "@d:dvp2(<Cont> (x:y ^ z) ^ <S>x)",
      "@d:dvp2(<Cont> (x:y ^ z ^ <S>(z: ^ x)) ^ <S>(z:) ^ <Cont2>(x:y))"
    },

    // Example 12
    { "", // no rule, to test grammars in file
      "@d:dvp(<foo>(:a ^ <F>(b:c ^ d)))",
      "@d:dvp(<foo>(nom1:a ^ <F>(b:c ^ d) ^ <W>b:c ^ <P>(nom3:c ^ d)))"
    },

    // Example 13
    { "", // no rule, to test grammars in file
      "@d:test(<Actor>(:type ^ prop ^ <foo> bar))",
      "@d:test(<Actor>(a:type ^ prop ^ <foo> bar) ^ <Subject>a: ^ <Prop>prop ^ <Type>(:type))"
    },

    // Example 14
    { "", // no rule, to test grammars in file
      "@d:dvp(<Content>(:bar ^ baz) ^ <Wh-Restr>(:bar ^ baz))",
      "@d:equals(<Content>(:bar ^ baz) ^ <Wh-Restr>(:bar ^ baz))"
    },

    // Example 15
    { "", // no rule, to test grammars in file
      "@d:coref(<Content>(:bar ^ baz) ^ <Wh-Restr>(:bar ^ baz))",
      "@d:coref(<Content>(:bar ^ baz) ^ <Wh-Restr>(:bar ^ baz))",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>a:)",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>a: ^ identical)",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>a:bar)",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>a: ^ identical)",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>(a:))",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>a: ^ identical)",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>(a:bar))",
      "@d:coref(<Content>(a:bar ^ baz) ^ <Wh-Restr>a: ^ identical)",
    },

    // Example 17
    { "", // no rule, to test grammars in file
      "@m:math(<arg1>9 ^ <arg2>2)",
      "@m:math(<arg1>9 ^ <arg2>2 ^ <res>\"4.5\" ^ <bool>1)",
      "@m:math(<arg1>9 ^ <arg2>\"30.33\")",
      "@m:math(<arg1>9 ^ <arg2>\"30.33\" ^ <bool>0)"
    },

    // Example 18
    { "", // no rule, to test grammars in file
      "@e:enc(<enc> iso-8859-15 ^ <val> \"äÖìâé\" ^ <val2> \"\")",
      "@e:enc(<enc> iso-8859-15 ^ <val> \"äÖìâé\" ^ <val2> \"\" ^ <right> true)"
    },

    // Example 19
    { "", // no rule, to test grammars in file
      "@s:string(<number>333)",
      "@s:string(<number>333 ^ <numberAsString>\"driehonderddrieëndertig\")",
    },

    // Example 20
    { "", // no rule, to test grammars in file
      "@s:string(<ordinal>\"333\")",
      "@s:string(<ordinal>\"333\" ^ <numberAsString>\"333e\")",
    },
  };

  /* Test loading a grammar/project from a test fixture directory */
  @Test
  public void testGrammarApplySpecial() throws FileNotFoundException, IOException {
    int which = -1;
    if (which > 0) {
      _print = true;
      UtterancePlanner up = new UtterancePlanner();
      up.readProjectFile(getExampleGrammarFile());
      testApplyOne(up, goodPatterns[which], true, which, false);
      _print = false;
    }
  }

  /* Test loading a grammar/project from a test fixture directory */
  @Test
  public void testGrammarApply() throws FileNotFoundException, IOException {
    UtterancePlanner up = new UtterancePlanner();
    up.readProjectFile(getExampleGrammarFile());
    int i = 1;
    for (String [] pattern : goodPatterns) {
      testApplyOne(up, pattern, true, i++, false);
    }
  }


}
