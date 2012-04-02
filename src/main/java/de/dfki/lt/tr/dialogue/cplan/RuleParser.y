/* -*- Mode: Java -*- */

%code imports {
import java.io.Reader;
import java.util.List;
import java.util.LinkedList;

import de.dfki.lt.tr.dialogue.cplan.Path;
import de.dfki.lt.tr.dialogue.cplan.util.Position;
import de.dfki.lt.tr.dialogue.cplan.matches.*;
import de.dfki.lt.tr.dialogue.cplan.actions.*;

@SuppressWarnings({"rawtypes", "unchecked", "fallthrough", "unused"})
}

%language "Java"

%locations

%define package "de.dfki.lt.tr.dialogue.cplan"

%define public

%define parser_class_name "RuleParser"

%code {
  private List _rules;

  public List<Rule> getRules() {
    return (List<Rule>) _rules;
  }

  public void reset() {
    _rules = new LinkedList<BasicRule>();
  }

  public void reset(String inputDescription, Reader input) {
    reset();
    ((de.dfki.lt.tr.dialogue.cplan.Lexer)this.yylexer)
      .setInputReader(inputDescription, input);
  }

  private LinkedList<BasicRule> newRuleList(BasicRule rule) {
    LinkedList<BasicRule> l = new LinkedList<BasicRule>();
    if (rule != null) {
      l.add(rule);
    }
    return l;
  }


  private LinkedList<BasicRule> addConjunction(Match m,
                                               LinkedList<BasicRule> rules) {
    for (BasicRule rule : rules) {
      rule.setMatch(new Conjunction(m.deepCopy(), rule.getMatch()));
    }
    return rules;
  }

  private LinkedList<BasicRule> addDisjunction(Match m,
                                               LinkedList<BasicRule> rules) {
    for (BasicRule rule : rules) {
      rule.setMatch(new Disjunction(m.deepCopy(), rule.getMatch()));
    }
    return rules;
  }


  private LinkedList<BasicRule> addTopVarMatches(LinkedList<BasicRule> rules) {
    for (BasicRule rule : rules) {
      rule.setMatch(new VarMatch(null, rule.getMatch()));
    }
    return rules;
  }

  private BasicRule newRule(Match match, List actions, Location loc) {
    return new BasicRule(match, (List<Action>) actions, loc);
  }


  private FunCall getNewFunCall(String name, List args, Location loc) {
    try {
      return new FunCall(name, args);
    }
    catch (NoSuchMethodException ex) {
      yyerror(loc, "No such Function registered: " + ex.getMessage());
    }
    catch (IndexOutOfBoundsException ex) {
      yyerror(loc, ex.getMessage());
    }
    return null;
  }

  private FunCallDagNode getNewFunCallDagNode(String name, List args,
                                              Location loc) {
    try {
      return new FunCallDagNode(name, args);
    }
    catch (NoSuchMethodException ex) {
      yyerror(loc, "No such Function registered: " + ex.getMessage());
    }
    catch (IndexOutOfBoundsException ex) {
      yyerror(loc, ex.getMessage());
    }
    return null;
  }

  private class Op {
    char _op;
    LinkedList<BasicRule> _rules;

    Op(char op, LinkedList<BasicRule> rules) {
      _op = op;
      _rules = rules;
    }

    public LinkedList<BasicRule> apply(Match m) {
      if (_op == '^') {
        return addConjunction(m, _rules);
      } else {
        return addDisjunction(m, _rules);
      }
    }
  }

  public LinkedList<Op> newOpList(char op, LinkedList<BasicRule> rules) {
    LinkedList<Op> result = new LinkedList<Op>();
    result.add(new Op(op, rules));
    return result;
  }

  public LinkedList<Rule> applyToAll(Match m, List<Op> ops) {
    LinkedList<Rule> result = new LinkedList<Rule>();
    for (Op op : ops) {
      result.addAll(op.apply(m));
    }
    return result;
  }

  /*
  private class Op {

    private Op _rest;
    private Match _m;
    private List<BasicRule> _rules;

    char _op;

    List<BasicRule> apply(Match m);

    private Op(char op, Match m, List<BasicRule> l, Op rest) {
      _op = op
      _rules = l;
      _m = m;
      _rest = rest;
    }

    public Op(char op, List<BasicRule> l, Op rest) {
      this(op, null, l, rest);
    }

    public Op(char op, Match m, Op rest) {
      this(op, m, null, rest);
    }

    public Op(char op, Match m) {
      this(op, m, null, null );
    }

    public Op(char op, List<BasicRule> l) {
      this(op, null, l, null);
    }

    public List<BasicRule> apply(Match m) {
      if (
    }
  }
  */
}

%token < String >  ID         258
%token < String >  VAR        259
%token < String >  GVAR       260
// %token < String >  COMPARISON 261
%token < String >  ARROW      262
%token < String >  STRING     263
%token < String >  RVAR       264

%type < Match > expr eterm term feature nominal id_lvar iv_expr iv_term
%type < DagNode > rexpr rterm rfeat r_id_var rnominal rarg
%type < Path > path

%type < VarDagNode > lval

%type < Action > action

%type < BasicRule > rule

%type < LinkedList > start rules rulegroup group groups

%type < List > actions rargs

%type < MatchLVal > funcall

%%

start : rules            { _rules = $1; }

rules : rulegroup rules   { _rules = $1; _rules.addAll($2); $$ = _rules; }
      | rulegroup         { $$ = $1; }
      ;

rulegroup : rule '.'              { $$ = newRuleList($1); }
          | expr '{' groups '}'   { $$ = applyToAll($1, $3); }
          ;

groups : '|' group groups    { $3.addFirst(new Op('|', $2)); $$ = $3; }
       | '^' group groups    { $3.addFirst(new Op('^', $2)); $$ = $3; }
       | '|' group           { $$ = newOpList('|', $2); }
       | '^' group           { $$ = newOpList('^', $2); }
       ;

group : rule '.'         { $$ = newRuleList($1); }
      | expr '{' groups '}'  { $$ = applyToAll($1, $3); }
      ;

/*
rules : rule '.' rules   { if ($1 != null) $3.addFirst($1); $$ = $3;}
      | rule '.'         { $$ = newRuleList($1); }
      ;
*/

rule : expr ARROW actions { $$ = newRule(new VarMatch(null, $1), $3, @2); }
     | error              { $$ = null; }
     ;

expr : expr '|' eterm     { $$ = new Disjunction($1, $3); }
     | eterm
     ;

eterm : eterm '^' term     { $$ = new Conjunction($1, $3); }
      // | term COMPARISON expr // general boolean expressions: later
      | term
      ;

term : '<' id_lvar '>' term     { $$ = new FeatVal($2, $4); }
     | '<' id_lvar '>'          { $$ = new FeatVal($2, null); }
     | '(' GVAR '~' expr ')'    { $$ = new VarMatch(new GlobalVar($2), $4); }
     | '(' funcall '~' expr ')' { $$ = new VarMatch($2, $4); }
     | feature                  { $$ = $1; }
     ;

feature : nominal         { $$ = $1; }
        | nominal iv_term { $$ = new Conjunction($1,
                                   new FeatVal(DagNode.TYPE_FEAT_ID, $2)); }
        | ':' iv_term     { $$ = new FeatVal(DagNode.TYPE_FEAT_ID, $2); }
        | id_lvar         { $$ = new FeatVal(DagNode.PROP_FEAT_ID, $1); }
        | STRING          { $$ = new FeatVal(DagNode.PROP_FEAT_ID,
                                             new Atom($1)); }
        | '!' term        { $2.setNegated(true); $$ = $2; }
        | '(' expr ')'    { $$ = $2; }
        | '=' nominal     { $$ = new Coref($2); }
// Not needed anymore, the matching against function return values has all the
// functionality that is possible here. Would require unifiability of dags, not
// just simple matching
//      | funcall         { $$ = new UnifyMatch(null, $1),; }
        ;

nominal : ID ':'     { $$ = new FeatVal(DagNode.ID_FEAT_ID, new Atom($1)); }
        | VAR ':'    { $$ = new LocalVar($1);  }
        | GVAR ':'   { $$ = new GlobalVar($1); }
        ;

id_lvar : VAR      { $$ = new LocalVar($1); }
        | ID       { $$ = new Atom($1); }

iv_term : id_lvar           { $$ = $1; }
        | '!' iv_term       { $2.setNegated(true); $$ = $2; }
        | '(' iv_expr ')'   { $$ = $2; }
        ;

iv_expr : iv_term               { $$ = $1; }
        | iv_term '|' iv_expr   { $$ = new Disjunction($1, $3); }
        ;

funcall : ID '(' rargs ')' { $$ = getNewFunCall($1, $3, @1);
                             if ($$ == null) return YYERROR ;
                           }
        | ID '(' ')'       { $$ = getNewFunCall($1, null, @1);
                             if ($$ == null) return YYERROR ;
                           }
        ;

/*
args : arg ',' args   { $3.add(0, $1); $$ = $3; }
     | arg            { List<Match> result = new LinkedList<Match>();
                        result.add($1);
                        $$ = result;
                      }
     ;

arg  : id_lvar        { $$ = $1; }
     | STRING         { $$ = new Atom($1); }
     ;
*/

// **************************** ACTIONS ***************************************
// Now for the right hand sides of the rules:
// no negation/alternative/comparison, but global var assignment and
// replacement vs. conjunction/addition

actions : action {
            List<Action> result  = new LinkedList<Action>();
            result.add($1);
            $$ = result;
          }
        | action ',' actions  { $3.add(0, $1); $$ = $3; }
        ;

action : lval '=' rexpr
       {
         DagNode rval = (($3 == null) ? null : $3.copyAndInvalidate());
         $$ = new Assignment($1, rval);
       }
       | lval '^' rexpr
       {
         DagNode rval = (($3 == null) ? null : $3.copyAndInvalidate());
         $$ = new Addition($1, rval);
       }
// THAT DOES NOT SUFFICE! IT MIGHT BE NICE TO SPECIFY REXPRS TO DELETE E.G.
// THE TYPE AND PROP AND SOME FEATURES IN ONE SWEEP, BUT KEEP THE REST
       | lval '!' '<' ID  '>'
       { $$ = new Deletion($1, new DagNode($4, new DagNode())); }
       ;

lval : VAR       { $$ = new VarDagNode($1, Bindings.LOCAL); }
     | RVAR      { $$ = new VarDagNode($1, Bindings.RIGHTLOCAL); }
     | '#'       { $$ = new VarDagNode("#", Bindings.LOCAL); }
     | GVAR path { $$ = new VarDagNode($1, $2); }
//   | ID ':'    { $$ = new VarDagNode($1, Bindings.ABSOLUTE); }
     ;

path : '<' ID '>' path { $$ = $4.addToFront($2); }
     |                 { $$ = new Path(); }
     ;

rexpr  : rexpr '^' rterm  { $1.add($3); $1.setNominal(); $$ = $1; }
       | rterm            { $$ = $1; }
       ;

rterm : '<' ID '>' rterm  { $$ = new DagNode($2, $4).setNominal(); }
      | rfeat             { $$ = $1; }
      ;


rfeat : rnominal          { $$ = $1; }
      | rnominal r_id_var { $1.add(new DagNode(DagNode.TYPE_FEAT_ID, $2));
                            $$ = $1; }
      | ':' r_id_var      { $$ = new DagNode(DagNode.TYPE_FEAT_ID, $2)
                                    .setNominal();
                          }
      | r_id_var          { $$ = new DagNode(DagNode.PROP_FEAT_ID, $1); }
      | STRING            { $$ = new DagNode(DagNode.PROP_FEAT_ID,
                                             new DagNode($1)); }
      | '(' rexpr ')'     { $$ = $2.setNominal(); }
      // | ID '(' rargs ')'  { $$ = getNewFunCallDagNode($1, $3, @1);
      //                       if ($$ == null) return YYERROR ;
      //                     }
      // | ID '(' ')'        { $$ = getNewFunCallDagNode($1, null, @1);
      //                       if ($$ == null) return YYERROR ;
      //                     }
      ;

rnominal : r_id_var ':' {
             $$ = new DagNode(DagNode.ID_FEAT_ID, $1).setNominal();
         }
         ;

rargs : rarg ',' rargs   { $3.add(0, $1); $$ = $3; }
      | rarg             { List<DagNode> result = new LinkedList<DagNode>();
                           result.add($1);
                           $$ = result;
                         }
      ;

rarg  : r_id_var  { $$ = $1; }
      | STRING    { $$ = new DagNode($1); }
      | '#'       { $$ = new VarDagNode("#", Bindings.LOCAL); }
      ;

r_id_var : ID     { $$ = new DagNode($1); }
         | VAR    { $$ = new VarDagNode($1, Bindings.LOCAL); }
         | GVAR path { $$ = new VarDagNode($1, $2); }
         | RVAR   { $$ = new VarDagNode($1, Bindings.RIGHTLOCAL); }
         | ID '(' rargs ')'  { $$ = getNewFunCallDagNode($1, $3, @1);
                               if ($$ == null) return YYERROR ;
                             }
         | ID '(' ')'        { $$ = getNewFunCallDagNode($1, null, @1);
                               if ($$ == null) return YYERROR ;
                             }
         ;
