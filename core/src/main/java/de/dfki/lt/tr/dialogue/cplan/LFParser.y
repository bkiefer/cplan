/* -*- Mode: Java -*- */

%code imports {
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

@SuppressWarnings({"fallthrough", "unused"})
}

%language "Java"

%define api.package "de.dfki.lt.tr.dialogue.cplan"

%define api.parser.public

%define api.prefix {LF}

%define parse.error verbose

%define parse.trace

%code {
  private boolean extMode = false;

  private boolean _correct = true;

  private DagNode _lf;

  private HashMap<String, DagNode> _nodes = new HashMap<String, DagNode>();

  private DagNode newLF(String feature, String type) {
    return new DagNode(feature, new DagNode(type));
  }

  private DagNode newLF(String feature, DagNode value) {
    return new DagNode(feature, value);
  }

  private DagNode newLF(short feature, String type) {
    return new DagNode(feature, new DagNode(type));
  }

  private DagNode getNewLF(String id) {
    DagNode lf = _nodes.get(id);
    if (lf == null) {
      lf = newLF(DagNode.ID_FEAT_ID, id);
      _nodes.put(id, lf);
    }
    return lf;
  }

  /** unify two conjunctions */
  private DagNode unify(DagNode left, DagNode right) {
    left.add(right);
    return left;
  }

  /** Transfer the result back to the caller of the parser */
  public List<DagNode> getResultLFs() {
    List<DagNode> result = new LinkedList<DagNode>();
    if (_lf != null) result.add(_lf.copyAndInvalidate());
    return result;
  }

  /** Transfer the result back to the caller of the parser */
  public DagNode getResultLF() {
    return _lf.copyAndInvalidate();
  }

  public void reset() {
    _lf = null;
    _nodes.clear();
    _correct = true;
  }

  /** extMode == true allows to parse multiple LFs in a row, while false will
   *  parse only one LF
   */
  public void setExtMode(boolean what) {
    extMode = what;
  }

  public void reset(String inputDescription, Reader input) {
    reset();
    ((de.dfki.lt.tr.dialogue.cplan.Lexer)this.yylexer)
      .setInputReader(inputDescription, input);
  }

  public boolean correct() { return _correct; }

}

%token < String >  ID         258
%token < String >  VAR        259
%token < String >  GVAR       260
%token < String >  COMPARISON 261
%token < String >  ARROW      262
%token < String >  STRING     263

%type < DagNode > lf lfconj lfterm rawlf keyval

%type < Object > start

// %type < Object > rexpr rterm rnominal

%%

start : lf               { $$ = null; _lf = $1; if (extMode) return YYACCEPT; }
      | rawlf            { $$ = null; _lf = $1; if (extMode) return YYACCEPT; }
      | error            { $$ = null; _lf = null; _correct = false; }
      ;

// plain logical forms

lf : '@' ID ':' ID '(' lfconj ')' {
     $$ = unify(getNewLF($2).setNominal(),
                unify(newLF(DagNode.TYPE_FEAT_ID, $4), $6));
   }
   | '@' ID  '(' lfconj ')' {
     $$ = unify(getNewLF($2).setNominal(), $4);
   }
   | '(' lfconj ')'   { $$ = $2.setNominal(); }
   ;

lfconj : lfterm '^' lfconj { $$ = unify($1, $3); }
       | lfterm            { $$ = $1; }
       ;

lfterm : '<' ID '>' '(' lfconj ')' { $$ = newLF($2, $5).setNominal(); }
       | '<' ID '>' ID  { $$ = newLF($2, newLF(DagNode.PROP_FEAT_ID, $4))
                                    .setNominal();
                        }
       | '<' ID '>' STRING { $$ = newLF($2, newLF(DagNode.PROP_FEAT_ID, $4))
                                    .setNominal();
                           }
       | '<' ID '>' ID ':' ID
                        { $$ = newLF($2,
                                     unify(getNewLF($4).setNominal(),
                                           newLF(DagNode.TYPE_FEAT_ID, $6)))
                            .setNominal();
                        }
       | '<' ID '>' ID ':'
                        { $$ = newLF($2, getNewLF($4).setNominal())
                            .setNominal();
                        }
       | ID ':' ID      { $$ = unify(getNewLF($1).setNominal(),
                                     newLF(DagNode.TYPE_FEAT_ID, $3));
                        }
       | ID ':'         { $$ = getNewLF($1).setNominal(); }
       | ':' ID         { $$ = newLF(DagNode.TYPE_FEAT_ID, $2).setNominal(); }
       | ID             { $$ = newLF(DagNode.PROP_FEAT_ID, $1); }
       ;

rawlf : ID '(' ID keyval ')'
                        {
                          DagNode prop = newLF(DagNode.PROP_FEAT_ID, $3);
                          if ($4 != null) {
                            prop = unify(prop, $4);
                          }
                          $$ = unify(getNewLF("raw").setNominal(),
                                 unify(newLF(DagNode.TYPE_FEAT_ID, $1), prop));
                        };

keyval : ',' ID '=' STRING keyval {
	DagNode res = newLF($2, newLF(DagNode.PROP_FEAT_ID, $4)).setNominal();
	$$ = ($5 == null) ? res : unify(res, $5);
	}
       | ',' ID '=' ID keyval {
	DagNode res = newLF($2, newLF(DagNode.PROP_FEAT_ID, $4)).setNominal();
	$$ = ($5 == null) ? res : unify(res, $5);
	}
       | { $$ = null; };
