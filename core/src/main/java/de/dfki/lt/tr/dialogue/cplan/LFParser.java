/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015, 2018-2021 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

package de.dfki.lt.tr.dialogue.cplan;



import java.text.MessageFormat;
import java.util.ArrayList;
/* "%code imports" blocks.  */
/* "LFParser.y":3  */

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

@SuppressWarnings({"fallthrough", "unused"})

/* "LFParser.java":54  */

/**
 * A Bison parser, automatically generated from <tt>LFParser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class LFParser
{
  /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.8.2";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";



  /**
   * True if verbose error messages are enabled.
   */
  private boolean yyErrorVerbose = true;

  /**
   * Whether verbose error messages are enabled.
   */
  public final boolean getErrorVerbose() { return yyErrorVerbose; }

  /**
   * Set the verbosity of error messages.
   * @param verbose True to request verbose error messages.
   */
  public final void setErrorVerbose(boolean verbose)
  { yyErrorVerbose = verbose; }




  public enum SymbolKind
  {
    S_YYEOF(0),                    /* "end of file"  */
    S_YYerror(1),                  /* error  */
    S_YYUNDEF(2),                  /* "invalid token"  */
    S_ID(3),                       /* ID  */
    S_VAR(4),                      /* VAR  */
    S_GVAR(5),                     /* GVAR  */
    S_COMPARISON(6),               /* COMPARISON  */
    S_ARROW(7),                    /* ARROW  */
    S_STRING(8),                   /* STRING  */
    S_9_(9),                       /* '@'  */
    S_10_(10),                     /* ':'  */
    S_11_(11),                     /* '('  */
    S_12_(12),                     /* ')'  */
    S_13_(13),                     /* '^'  */
    S_14_(14),                     /* '<'  */
    S_15_(15),                     /* '>'  */
    S_16_(16),                     /* ','  */
    S_17_(17),                     /* '='  */
    S_YYACCEPT(18),                /* $accept  */
    S_start(19),                   /* start  */
    S_lf(20),                      /* lf  */
    S_lfconj(21),                  /* lfconj  */
    S_lfterm(22),                  /* lfterm  */
    S_rawlf(23),                   /* rawlf  */
    S_keyval(24);                  /* keyval  */


    private final int yycode_;

    SymbolKind (int n) {
      this.yycode_ = n;
    }

    private static final SymbolKind[] values_ = {
      SymbolKind.S_YYEOF,
      SymbolKind.S_YYerror,
      SymbolKind.S_YYUNDEF,
      SymbolKind.S_ID,
      SymbolKind.S_VAR,
      SymbolKind.S_GVAR,
      SymbolKind.S_COMPARISON,
      SymbolKind.S_ARROW,
      SymbolKind.S_STRING,
      SymbolKind.S_9_,
      SymbolKind.S_10_,
      SymbolKind.S_11_,
      SymbolKind.S_12_,
      SymbolKind.S_13_,
      SymbolKind.S_14_,
      SymbolKind.S_15_,
      SymbolKind.S_16_,
      SymbolKind.S_17_,
      SymbolKind.S_YYACCEPT,
      SymbolKind.S_start,
      SymbolKind.S_lf,
      SymbolKind.S_lfconj,
      SymbolKind.S_lfterm,
      SymbolKind.S_rawlf,
      SymbolKind.S_keyval
    };

    static final SymbolKind get(int code) {
      return values_[code];
    }

    public final int getCode() {
      return this.yycode_;
    }

    /* Return YYSTR after stripping away unnecessary quotes and
       backslashes, so that it's suitable for yyerror.  The heuristic is
       that double-quoting is unnecessary unless the string contains an
       apostrophe, a comma, or backslash (other than backslash-backslash).
       YYSTR is taken from yytname.  */
    private static String yytnamerr_(String yystr)
    {
      if (yystr.charAt (0) == '"')
        {
          StringBuffer yyr = new StringBuffer();
          strip_quotes: for (int i = 1; i < yystr.length(); i++)
            switch (yystr.charAt(i))
              {
              case '\'':
              case ',':
                break strip_quotes;

              case '\\':
                if (yystr.charAt(++i) != '\\')
                  break strip_quotes;
                /* Fall through.  */
              default:
                yyr.append(yystr.charAt(i));
                break;

              case '"':
                return yyr.toString();
              }
        }
      return yystr;
    }

    /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
       First, the terminals, then, starting at \a YYNTOKENS_, nonterminals.  */
    private static final String[] yytname_ = yytname_init();
  private static final String[] yytname_init()
  {
    return new String[]
    {
  "\"end of file\"", "error", "\"invalid token\"", "ID", "VAR", "GVAR",
  "COMPARISON", "ARROW", "STRING", "'@'", "':'", "'('", "')'", "'^'",
  "'<'", "'>'", "','", "'='", "$accept", "start", "lf", "lfconj", "lfterm",
  "rawlf", "keyval", null
    };
  }

    /* The user-facing name of this symbol.  */
    public final String getName() {
      return yytnamerr_(yytname_[yycode_]);
    }

  };


  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>LFParser</tt>.
   */
  public interface Lexer {
    /* Token kinds.  */
    /** Token "end of file", to be returned by the scanner.  */
    static final int YYEOF = 0;
    /** Token error, to be returned by the scanner.  */
    static final int YYerror = 256;
    /** Token "invalid token", to be returned by the scanner.  */
    static final int YYUNDEF = 264;
    /** Token ID, to be returned by the scanner.  */
    static final int ID = 258;
    /** Token VAR, to be returned by the scanner.  */
    static final int VAR = 259;
    /** Token GVAR, to be returned by the scanner.  */
    static final int GVAR = 260;
    /** Token COMPARISON, to be returned by the scanner.  */
    static final int COMPARISON = 261;
    /** Token ARROW, to be returned by the scanner.  */
    static final int ARROW = 262;
    /** Token STRING, to be returned by the scanner.  */
    static final int STRING = 263;

    /** Deprecated, use YYEOF instead.  */
    public static final int EOF = YYEOF;


    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex() throws java.io.IOException;

    /**
     * Emit an errorin a user-defined way.
     *
     *
     * @param msg The string for the error message.
     */
     void yyerror(String msg);


  }


  /**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;





  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public LFParser(Lexer yylexer)
  {

    this.yylexer = yylexer;

  }


  private java.io.PrintStream yyDebugStream = System.err;

  /**
   * The <tt>PrintStream</tt> on which the debugging output is printed.
   */
  public final java.io.PrintStream getDebugStream() { return yyDebugStream; }

  /**
   * Set the <tt>PrintStream</tt> on which the debug output is printed.
   * @param s The stream that is used for debugging output.
   */
  public final void setDebugStream(java.io.PrintStream s) { yyDebugStream = s; }

  private int yydebug = 0;

  /**
   * Answer the verbosity of the debugging output; 0 means that all kinds of
   * output from the parser are suppressed.
   */
  public final int getDebugLevel() { return yydebug; }

  /**
   * Set the verbosity of the debugging output; 0 means that all kinds of
   * output from the parser are suppressed.
   * @param level The verbosity level for debugging output.
   */
  public final void setDebugLevel(int level) { yydebug = level; }


  private int yynerrs = 0;

  /**
   * The number of syntax errors so far.
   */
  public final int getNumberOfErrors() { return yynerrs; }

  /**
   * Print an error message via the lexer.
   *
   * @param msg The error message.
   */
  public final void yyerror(String msg) {
      yylexer.yyerror(msg);
  }


  protected final void yycdebugNnl(String s) {
    if (0 < yydebug)
      yyDebugStream.print(s);
  }

  protected final void yycdebug(String s) {
    if (0 < yydebug)
      yyDebugStream.println(s);
  }

  private final class YYStack {
    private int[] stateStack = new int[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push(int state, Object value) {
      height++;
      if (size == height) {
        int[] newStateStack = new int[size * 2];
        System.arraycopy(stateStack, 0, newStateStack, 0, height);
        stateStack = newStateStack;

        Object[] newValueStack = new Object[size * 2];
        System.arraycopy(valueStack, 0, newValueStack, 0, height);
        valueStack = newValueStack;

        size *= 2;
      }

      stateStack[height] = state;
      valueStack[height] = value;
    }

    public final void pop() {
      pop(1);
    }

    public final void pop(int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (0 < num) {
        java.util.Arrays.fill(valueStack, height - num + 1, height + 1, null);
      }
      height -= num;
    }

    public final int stateAt(int i) {
      return stateStack[height - i];
    }

    public final Object valueAt(int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print(java.io.PrintStream out) {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++) {
        out.print(' ');
        out.print(stateStack[i]);
      }
      out.println();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).
   */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).
   */
  public static final int YYABORT = 1;



  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.
   */
  public static final int YYERROR = 2;

  /**
   * Internal return codes that are not supported for user semantic
   * actions.
   */
  private static final int YYERRLAB = 3;
  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;


  private int yyerrstatus_ = 0;


  /**
   * Whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.
   */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  /** Compute post-reduction state.
   * @param yystate   the current state
   * @param yysym     the nonterminal to push on the stack
   */
  private int yyLRGotoState(int yystate, int yysym) {
    int yyr = yypgoto_[yysym - YYNTOKENS_] + yystate;
    if (0 <= yyr && yyr <= YYLAST_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - YYNTOKENS_];
  }

  private int yyaction(int yyn, YYStack yystack, int yylen)
  {
    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    Object yyval = (0 < yylen) ? yystack.valueAt(yylen - 1) : yystack.valueAt(0);

    yyReducePrint(yyn, yystack);

    switch (yyn)
      {
          case 2: /* start: lf  */
  if (yyn == 2)
    /* "LFParser.y":110  */
                         { yyval = null; _lf = (( DagNode )(yystack.valueAt (0))); if (extMode) return YYACCEPT; };
  break;


  case 3: /* start: rawlf  */
  if (yyn == 3)
    /* "LFParser.y":111  */
                         { yyval = null; _lf = (( DagNode )(yystack.valueAt (0))); if (extMode) return YYACCEPT; };
  break;


  case 4: /* start: error  */
  if (yyn == 4)
    /* "LFParser.y":112  */
                         { yyval = null; _lf = null; _correct = false; };
  break;


  case 5: /* lf: '@' ID ':' ID '(' lfconj ')'  */
  if (yyn == 5)
    /* "LFParser.y":117  */
                                  {
     yyval = unify(getNewLF((( String )(yystack.valueAt (5)))).setNominal(),
                unify(newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (3)))), (( DagNode )(yystack.valueAt (1)))));
   };
  break;


  case 6: /* lf: '@' ID '(' lfconj ')'  */
  if (yyn == 6)
    /* "LFParser.y":121  */
                            {
     yyval = unify(getNewLF((( String )(yystack.valueAt (3)))).setNominal(), (( DagNode )(yystack.valueAt (1))));
   };
  break;


  case 7: /* lf: '(' lfconj ')'  */
  if (yyn == 7)
    /* "LFParser.y":124  */
                      { yyval = (( DagNode )(yystack.valueAt (1))).setNominal(); };
  break;


  case 8: /* lfconj: lfterm '^' lfconj  */
  if (yyn == 8)
    /* "LFParser.y":127  */
                           { yyval = unify((( DagNode )(yystack.valueAt (2))), (( DagNode )(yystack.valueAt (0)))); };
  break;


  case 9: /* lfconj: lfterm  */
  if (yyn == 9)
    /* "LFParser.y":128  */
                           { yyval = (( DagNode )(yystack.valueAt (0))); };
  break;


  case 10: /* lfterm: '<' ID '>' '(' lfconj ')'  */
  if (yyn == 10)
    /* "LFParser.y":131  */
                                   { yyval = newLF((( String )(yystack.valueAt (4))), (( DagNode )(yystack.valueAt (1)))).setNominal(); };
  break;


  case 11: /* lfterm: '<' ID '>' ID  */
  if (yyn == 11)
    /* "LFParser.y":132  */
                        { yyval = newLF((( String )(yystack.valueAt (2))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (0)))))
                                    .setNominal();
                        };
  break;


  case 12: /* lfterm: '<' ID '>' STRING  */
  if (yyn == 12)
    /* "LFParser.y":135  */
                           { yyval = newLF((( String )(yystack.valueAt (2))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (0)))))
                                    .setNominal();
                           };
  break;


  case 13: /* lfterm: '<' ID '>' ID ':' ID  */
  if (yyn == 13)
    /* "LFParser.y":139  */
                        { yyval = newLF((( String )(yystack.valueAt (4))),
                                     unify(getNewLF((( String )(yystack.valueAt (2)))).setNominal(),
                                           newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (0))))))
                            .setNominal();
                        };
  break;


  case 14: /* lfterm: '<' ID '>' ID ':'  */
  if (yyn == 14)
    /* "LFParser.y":145  */
                        { yyval = newLF((( String )(yystack.valueAt (3))), getNewLF((( String )(yystack.valueAt (1)))).setNominal())
                            .setNominal();
                        };
  break;


  case 15: /* lfterm: ID ':' ID  */
  if (yyn == 15)
    /* "LFParser.y":148  */
                        { yyval = unify(getNewLF((( String )(yystack.valueAt (2)))).setNominal(),
                                     newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (0)))));
                        };
  break;


  case 16: /* lfterm: ID ':'  */
  if (yyn == 16)
    /* "LFParser.y":151  */
                        { yyval = getNewLF((( String )(yystack.valueAt (1)))).setNominal(); };
  break;


  case 17: /* lfterm: ':' ID  */
  if (yyn == 17)
    /* "LFParser.y":152  */
                        { yyval = newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (0)))).setNominal(); };
  break;


  case 18: /* lfterm: ID  */
  if (yyn == 18)
    /* "LFParser.y":153  */
                        { yyval = newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (0)))); };
  break;


  case 19: /* rawlf: ID '(' ID keyval ')'  */
  if (yyn == 19)
    /* "LFParser.y":157  */
                        {
                          DagNode prop = newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (2))));
                          if ((( DagNode )(yystack.valueAt (1))) != null) {
                            prop = unify(prop, (( DagNode )(yystack.valueAt (1))));
                          }
                          yyval = unify(getNewLF("raw").setNominal(),
                                 unify(newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (4)))), prop));
                        };
  break;


  case 20: /* keyval: ',' ID '=' STRING keyval  */
  if (yyn == 20)
    /* "LFParser.y":166  */
                                  {
	DagNode res = newLF((( String )(yystack.valueAt (3))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (1))))).setNominal();
	yyval = ((( DagNode )(yystack.valueAt (0))) == null) ? res : unify(res, (( DagNode )(yystack.valueAt (0))));
	};
  break;


  case 21: /* keyval: ',' ID '=' ID keyval  */
  if (yyn == 21)
    /* "LFParser.y":170  */
                              {
	DagNode res = newLF((( String )(yystack.valueAt (3))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (1))))).setNominal();
	yyval = ((( DagNode )(yystack.valueAt (0))) == null) ? res : unify(res, (( DagNode )(yystack.valueAt (0))));
	};
  break;


  case 22: /* keyval: %empty  */
  if (yyn == 22)
    /* "LFParser.y":174  */
         { yyval = null; };
  break;



/* "LFParser.java":654  */

        default: break;
      }

    yySymbolPrint("-> $$ =", SymbolKind.get(yyr1_[yyn]), yyval);

    yystack.pop(yylen);
    yylen = 0;
    /* Shift the result of the reduction.  */
    int yystate = yyLRGotoState(yystack.stateAt(0), yyr1_[yyn]);
    yystack.push(yystate, yyval);
    return YYNEWSTATE;
  }


  /*--------------------------------.
  | Print this symbol on YYOUTPUT.  |
  `--------------------------------*/

  private void yySymbolPrint(String s, SymbolKind yykind,
                             Object yyvalue) {
      if (0 < yydebug) {
          yycdebug(s
                   + (yykind.getCode() < YYNTOKENS_ ? " token " : " nterm ")
                   + yykind.getName() + " ("
                   + (yyvalue == null ? "(null)" : yyvalue.toString()) + ")");
      }
  }


  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse() throws java.io.IOException

  {


    /* Lookahead token kind.  */
    int yychar = YYEMPTY_;
    /* Lookahead symbol kind.  */
    SymbolKind yytoken = null;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;
    YYStack yystack = new YYStack ();
    int label = YYNEWSTATE;



    /* Semantic value of the lookahead.  */
    Object yylval = null;



    yycdebug ("Starting parse");
    yyerrstatus_ = 0;
    yynerrs = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval);



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:
        yycdebug ("Entering state " + yystate);
        if (0 < yydebug)
          yystack.print (yyDebugStream);

        /* Accept?  */
        if (yystate == YYFINAL_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yyPactValueIsDefault (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == YYEMPTY_)
          {

            yycdebug ("Reading a token");
            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal();

          }

        /* Convert token to internal form.  */
        yytoken = yytranslate_ (yychar);
        yySymbolPrint("Next token is", yytoken,
                      yylval);

        if (yytoken == SymbolKind.S_YYerror)
          {
            // The scanner already issued an error message, process directly
            // to error recovery.  But do not keep the error token as
            // lookahead, it is too special and may lead us to an endless
            // loop in error recovery. */
            yychar = Lexer.YYUNDEF;
            yytoken = SymbolKind.S_YYUNDEF;
            label = YYERRLAB1;
          }
        else
          {
            /* If the proper action on seeing token YYTOKEN is to reduce or to
               detect an error, take that action.  */
            yyn += yytoken.getCode();
            if (yyn < 0 || YYLAST_ < yyn || yycheck_[yyn] != yytoken.getCode()) {
              label = YYDEFAULT;
            }

            /* <= 0 means reduce or error.  */
            else if ((yyn = yytable_[yyn]) <= 0)
              {
                if (yyTableValueIsError(yyn)) {
                  label = YYERRLAB;
                } else {
                  yyn = -yyn;
                  label = YYREDUCE;
                }
              }

            else
              {
                /* Shift the lookahead token.  */
                yySymbolPrint("Shifting", yytoken,
                              yylval);

                /* Discard the token being shifted.  */
                yychar = YYEMPTY_;

                /* Count tokens shifted since error; after three, turn off error
                   status.  */
                if (yyerrstatus_ > 0)
                  --yyerrstatus_;

                yystate = yyn;
                yystack.push(yystate, yylval);
                label = YYNEWSTATE;
              }
          }
        break;

      /*-----------------------------------------------------------.
      | yydefault -- do the default action for the current state.  |
      `-----------------------------------------------------------*/
      case YYDEFAULT:
        yyn = yydefact_[yystate];
        if (yyn == 0)
          label = YYERRLAB;
        else
          label = YYREDUCE;
        break;

      /*-----------------------------.
      | yyreduce -- Do a reduction.  |
      `-----------------------------*/
      case YYREDUCE:
        yylen = yyr2_[yyn];
        label = yyaction(yyn, yystack, yylen);
        yystate = yystack.stateAt(0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs;
            if (yychar == YYEMPTY_)
              yytoken = null;
            yyreportSyntaxError(new Context(this, yystack, yytoken));
          }

        if (yyerrstatus_ == 3)
          {
            /* If just tried and failed to reuse lookahead token after an
               error, discard it.  */

            if (yychar <= Lexer.YYEOF)
              {
                /* Return failure if at end of input.  */
                if (yychar == Lexer.YYEOF)
                  return false;
              }
            else
              yychar = YYEMPTY_;
          }

        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `-------------------------------------------------*/
      case YYERROR:
        /* Do not reclaim the symbols of the rule which action triggered
           this YYERROR.  */
        yystack.pop (yylen);
        yylen = 0;
        yystate = yystack.stateAt(0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;       /* Each real token shifted decrements this.  */

        // Pop stack until we find a state that shifts the error token.
        for (;;)
          {
            yyn = yypact_[yystate];
            if (!yyPactValueIsDefault (yyn))
              {
                yyn += SymbolKind.S_YYerror.getCode();
                if (0 <= yyn && yyn <= YYLAST_
                    && yycheck_[yyn] == SymbolKind.S_YYerror.getCode())
                  {
                    yyn = yytable_[yyn];
                    if (0 < yyn)
                      break;
                  }
              }

            /* Pop the current state because it cannot handle the
             * error token.  */
            if (yystack.height == 0)
              return false;


            yystack.pop ();
            yystate = yystack.stateAt(0);
            if (0 < yydebug)
              yystack.print (yyDebugStream);
          }

        if (label == YYABORT)
          /* Leave the switch.  */
          break;



        /* Shift the error token.  */
        yySymbolPrint("Shifting", SymbolKind.get(yystos_[yyn]),
                      yylval);

        yystate = yyn;
        yystack.push (yyn, yylval);
        label = YYNEWSTATE;
        break;

        /* Accept.  */
      case YYACCEPT:
        return true;

        /* Abort.  */
      case YYABORT:
        return false;
      }
}




  /**
   * Information needed to get the list of expected tokens and to forge
   * a syntax error diagnostic.
   */
  public static final class Context {
    Context(LFParser parser, YYStack stack, SymbolKind token) {
      yyparser = parser;
      yystack = stack;
      yytoken = token;
    }

    private LFParser yyparser;
    private YYStack yystack;


    /**
     * The symbol kind of the lookahead token.
     */
    public final SymbolKind getToken() {
      return yytoken;
    }

    private SymbolKind yytoken;
    static final int NTOKENS = LFParser.YYNTOKENS_;

    /**
     * Put in YYARG at most YYARGN of the expected tokens given the
     * current YYCTX, and return the number of tokens stored in YYARG.  If
     * YYARG is null, return the number of expected tokens (guaranteed to
     * be less than YYNTOKENS).
     */
    int getExpectedTokens(SymbolKind yyarg[], int yyargn) {
      return getExpectedTokens (yyarg, 0, yyargn);
    }

    int getExpectedTokens(SymbolKind yyarg[], int yyoffset, int yyargn) {
      int yycount = yyoffset;
      int yyn = yypact_[this.yystack.stateAt(0)];
      if (!yyPactValueIsDefault(yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative
             indexes in YYCHECK.  In other words, skip the first
             -YYN actions for this state because they are default
             actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST_ - yyn + 1;
          int yyxend = yychecklim < NTOKENS ? yychecklim : NTOKENS;
          for (int yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck_[yyx + yyn] == yyx && yyx != SymbolKind.S_YYerror.getCode()
                && !yyTableValueIsError(yytable_[yyx + yyn]))
              {
                if (yyarg == null)
                  yycount += 1;
                else if (yycount == yyargn)
                  return 0; // FIXME: this is incorrect.
                else
                  yyarg[yycount++] = SymbolKind.get(yyx);
              }
        }
      if (yyarg != null && yycount == yyoffset && yyoffset < yyargn)
        yyarg[yycount] = null;
      return yycount - yyoffset;
    }
  }




  private int yysyntaxErrorArguments(Context yyctx, SymbolKind[] yyarg, int yyargn) {
    /* There are many possibilities here to consider:
       - If this state is a consistent state with a default action,
         then the only way this function was invoked is if the
         default action is an error action.  In that case, don't
         check for expected tokens because there are none.
       - The only way there can be no lookahead present (in tok) is
         if this state is a consistent state with a default action.
         Thus, detecting the absence of a lookahead is sufficient to
         determine that there is no unexpected or expected token to
         report.  In that case, just report a simple "syntax error".
       - Don't assume there isn't a lookahead just because this
         state is a consistent state with a default action.  There
         might have been a previous inconsistent state, consistent
         state with a non-default action, or user semantic action
         that manipulated yychar.  (However, yychar is currently out
         of scope during semantic actions.)
       - Of course, the expected token list depends on states to
         have correct lookahead information, and it depends on the
         parser not to perform extra reductions after fetching a
         lookahead from the scanner and before detecting a syntax
         error.  Thus, state merging (from LALR or IELR) and default
         reductions corrupt the expected token list.  However, the
         list is correct for canonical LR with one exception: it
         will still contain any token that will not be accepted due
         to an error action in a later state.
    */
    int yycount = 0;
    if (yyctx.getToken() != null)
      {
        if (yyarg != null)
          yyarg[yycount] = yyctx.getToken();
        yycount += 1;
        yycount += yyctx.getExpectedTokens(yyarg, 1, yyargn);
      }
    return yycount;
  }


  /**
   * Build and emit a "syntax error" message in a user-defined way.
   *
   * @param ctx  The context of the error.
   */
  private void yyreportSyntaxError(Context yyctx) {
      if (yyErrorVerbose) {
          final int argmax = 5;
          SymbolKind[] yyarg = new SymbolKind[argmax];
          int yycount = yysyntaxErrorArguments(yyctx, yyarg, argmax);
          String[] yystr = new String[yycount];
          for (int yyi = 0; yyi < yycount; ++yyi) {
              yystr[yyi] = yyarg[yyi].getName();
          }
          String yyformat;
          switch (yycount) {
              default:
              case 0: yyformat = "syntax error"; break;
              case 1: yyformat = "syntax error, unexpected {0}"; break;
              case 2: yyformat = "syntax error, unexpected {0}, expecting {1}"; break;
              case 3: yyformat = "syntax error, unexpected {0}, expecting {1} or {2}"; break;
              case 4: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3}"; break;
              case 5: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3} or {4}"; break;
          }
          yyerror(new MessageFormat(yyformat).format(yystr));
      } else {
          yyerror("syntax error");
      }
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yyPactValueIsDefault(int yyvalue) {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yyTableValueIsError(int yyvalue) {
    return yyvalue == yytable_ninf_;
  }

  private static final byte yypact_ninf_ = -23;
  private static final byte yytable_ninf_ = -1;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final byte[] yypact_ = yypact_init();
  private static final byte[] yypact_init()
  {
    return new byte[]
    {
      13,   -23,    -8,     3,    -2,    11,   -23,   -23,    10,     7,
      15,    20,    23,    16,    14,   -23,    17,    26,    -2,    27,
     -23,    19,   -23,    -2,    28,    24,    21,    25,   -23,    -1,
     -23,    18,   -23,    -2,   -23,    29,   -23,    -2,     1,    30,
      35,    31,    17,    17,   -23,   -23,   -23,   -23,   -23
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final byte[] yydefact_ = yydefact_init();
  private static final byte[] yydefact_init()
  {
    return new byte[]
    {
       0,     4,     0,     0,     0,     0,     2,     3,     0,     0,
      18,     0,     0,     0,     9,     1,    22,     0,     0,    16,
      17,     0,     7,     0,     0,     0,     0,     0,    15,     0,
       8,     0,    19,     0,     6,    11,    12,     0,     0,     0,
      14,     0,    22,    22,     5,    13,    10,    21,    20
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final byte[] yypgoto_ = yypgoto_init();
  private static final byte[] yypgoto_init()
  {
    return new byte[]
    {
     -23,   -23,   -23,   -18,   -23,   -23,   -22
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final byte[] yydefgoto_ = yydefgoto_init();
  private static final byte[] yydefgoto_init()
  {
    return new byte[]
    {
       0,     5,     6,    13,    14,     7,    25
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final byte[] yytable_ = yytable_init();
  private static final byte[] yytable_init()
  {
    return new byte[]
    {
      27,    10,    35,     8,    42,    30,     9,    36,    11,    43,
      37,    15,    12,    16,     1,    39,     2,    17,    18,    41,
      47,    48,     3,    20,     4,    19,    21,    23,    22,    26,
      28,    31,    33,    24,    29,    38,    32,    34,    45,    40,
       0,     0,    44,    46
    };
  }

private static final byte[] yycheck_ = yycheck_init();
  private static final byte[] yycheck_init()
  {
    return new byte[]
    {
      18,     3,     3,    11,     3,    23,     3,     8,    10,     8,
      11,     0,    14,     3,     1,    33,     3,    10,    11,    37,
      42,    43,     9,     3,    11,    10,     3,    13,    12,     3,
       3,     3,    11,    16,    15,    17,    12,    12,     3,    10,
      -1,    -1,    12,    12
    };
  }

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,     1,     3,     9,    11,    19,    20,    23,    11,     3,
       3,    10,    14,    21,    22,     0,     3,    10,    11,    10,
       3,     3,    12,    13,    16,    24,     3,    21,     3,    15,
      21,     3,    12,    11,    12,     3,     8,    11,    17,    21,
      10,    21,     3,     8,    12,     3,    12,    24,    24
    };
  }

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    18,    19,    19,    19,    20,    20,    20,    21,    21,
      22,    22,    22,    22,    22,    22,    22,    22,    22,    23,
      24,    24,    24
    };
  }

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     1,     1,     7,     5,     3,     3,     1,
       6,     4,     4,     6,     5,     3,     2,     2,     1,     5,
       5,     5,     0
    };
  }



  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  private static final short[] yyrline_ = yyrline_init();
  private static final short[] yyrline_init()
  {
    return new short[]
    {
       0,   110,   110,   111,   112,   117,   121,   124,   127,   128,
     131,   132,   135,   138,   144,   148,   151,   152,   153,   156,
     166,   170,   174
    };
  }


  // Report on the debug stream that the rule yyrule is going to be reduced.
  private void yyReducePrint (int yyrule, YYStack yystack)
  {
    if (yydebug == 0)
      return;

    int yylno = yyrline_[yyrule];
    int yynrhs = yyr2_[yyrule];
    /* Print the symbols being reduced, and their result.  */
    yycdebug ("Reducing stack by rule " + (yyrule - 1)
              + " (line " + yylno + "):");

    /* The symbols being reduced.  */
    for (int yyi = 0; yyi < yynrhs; yyi++)
      yySymbolPrint("   $" + (yyi + 1) + " =",
                    SymbolKind.get(yystos_[yystack.stateAt(yynrhs - (yyi + 1))]),
                    yystack.valueAt ((yynrhs) - (yyi + 1)));
  }

  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 264;
    if (t <= 0)
      return SymbolKind.S_YYEOF;
    else if (t <= code_max)
      return SymbolKind.get(yytranslate_table_[t]);
    else
      return SymbolKind.S_YYUNDEF;
  }
  private static final byte[] yytranslate_table_ = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
      11,    12,     2,     2,    16,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    10,     2,
      14,    17,    15,     2,     9,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,    13,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     2
    };
  }


  private static final int YYLAST_ = 43;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 15;
  private static final int YYNTOKENS_ = 18;

/* Unqualified %code blocks.  */
/* "LFParser.y":24  */

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


/* "LFParser.java":1377  */

}
