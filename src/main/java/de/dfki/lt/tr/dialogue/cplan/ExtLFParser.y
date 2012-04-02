/* -*- Mode: Java -*- */

%code imports {
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

 @SuppressWarnings({"fallthrough", "unused", "rawtypes", "unchecked"})
}

%language "Java"

%define package "de.dfki.lt.tr.dialogue.cplan"

%define public

%define parser_class_name "ExtLFParser"

%code {
  private List<DagNode> _lfs;

  private HashMap<String, DagNode> _nodes = new HashMap<String, DagNode>();

  private List<DagNode> listify(DagNode d) {
    List<DagNode> result = new LinkedList<DagNode>();
    result.add(d);
    return result;
  }

  private List<DagNode> newLF(String feature, String type) {
    return listify(new DagNode(feature, new DagNode(type)));
  }

  private List<DagNode> newLF(String feature, List<DagNode> values) {
    List<DagNode> result = new LinkedList<DagNode>();
    for (DagNode value : values) {
      result.add(new DagNode(feature, value));
    }
    return result;
  }

  private List<DagNode> newLF(short feature, String type) {
    return listify(new DagNode(feature, new DagNode(type)));
  }

  private List<DagNode> getNewLF(String id) {
    DagNode lf = _nodes.get(id);
    if (lf == null) {
      lf = newLF(DagNode.ID_FEAT_ID, id).get(0);
      _nodes.put(id, lf);
    }
    return listify(lf);
  }

  /** unify two lists of dag nodes */
  private List<DagNode> unify(List<DagNode> leftList, List<DagNode> rightList) {
    List<DagNode> result = new LinkedList<DagNode>();
    for (DagNode left : leftList) {
      for (DagNode right : rightList) {
        boolean unifies = left.add(right);
        result.add(left.copyAndInvalidate());
      }
    }
    return result;
  }

  private List<DagNode> disjunction(List<DagNode> left, List<DagNode> right) {
    left.addAll(right);
    return left;
  }

  private List<DagNode> setNominal(List<DagNode> dags) {
    for (DagNode dag : dags) {
      dag.setNominal();
    }
    return dags;
  }

  /** Transfer the result back to the caller of the parser */
  public List<DagNode> getResultLFs() {
    return _lfs;
  }

  public void reset() {
    _lfs = null;
    _nodes.clear();
  }

  public void reset(String inputDescription, Reader input) {
    reset();
    ((de.dfki.lt.tr.dialogue.cplan.Lexer)this.yylexer)
      .setInputReader(inputDescription, input);
  }

}

%token < String >  ID         258
%token < String >  VAR        259
%token < String >  GVAR       260
%token < String >  COMPARISON 261
%token < String >  ARROW      262
%token < String >  STRING     263

%type < List > lf lfconj lfterm lfdisj

%type < Object > start

// %type < Object > rexpr rterm rnominal

%%

start : lf               { $$ = null; _lfs = $1; return YYACCEPT; }
      ;

// plain logical forms

lf : '@' ID ':' ID '(' lfconj ')' {
     $$ = unify(setNominal(getNewLF($2)),
                unify(newLF(DagNode.TYPE_FEAT_ID, $4), $6));
   }
   | '@' ID  '(' lfdisj ')' {
     $$ = unify(setNominal(getNewLF($2)), $4);
   }
   | '(' lfdisj ')'   { $$ = setNominal($2); }
   ;

lfdisj : lfconj '|' lfdisj { $$ = disjunction($1, $3); }
       | lfconj            { $$ = $1; }
       ;

lfconj : lfterm '^' lfconj { $$ = unify($1, $3); }
       | lfterm            { $$ = $1; }
       ;

lfterm : '<' ID '>' '(' lfconj ')' { $$ = setNominal(newLF($2, $5)); }
       | '<' ID '>' ID  { $$ = setNominal(
                                 newLF($2, newLF(DagNode.PROP_FEAT_ID, $4)));
                        }
       | '<' ID '>' STRING { $$ = setNominal(
                                    newLF($2, newLF(DagNode.PROP_FEAT_ID, $4)));
                           }
       | '<' ID '>' ID ':' ID
                        { $$ = setNominal(
                                 newLF($2,
                                       unify(setNominal(getNewLF($4)),
                                             newLF(DagNode.TYPE_FEAT_ID, $6))));
                        }
       | '<' ID '>' ID ':'
                        { $$ = setNominal(newLF($2, setNominal(getNewLF($4))));
                        }
       | ID ':' ID      { $$ = unify(setNominal(getNewLF($1)),
                                     newLF(DagNode.TYPE_FEAT_ID, $3));
                        }
       | ID ':'         { $$ = setNominal(getNewLF($1)); }
       | ':' ID         { $$ = setNominal(newLF(DagNode.TYPE_FEAT_ID, $2)); }
       | ID             { $$ = newLF(DagNode.PROP_FEAT_ID, $1); }
       | '(' lfdisj ')' { $$ = $2; }
       ;

