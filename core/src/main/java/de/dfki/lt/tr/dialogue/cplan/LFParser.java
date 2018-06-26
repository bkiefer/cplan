/* A Bison parser, made by GNU Bison 3.0.4.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

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

package de.dfki.lt.tr.dialogue.cplan;
/* First part of user declarations.  */

/* "LFParser.java":37  */ /* lalr1.java:91  */

/* "LFParser.java":39  */ /* lalr1.java:92  */
/* "%code imports" blocks.  */
/* "LFParser.y":3  */ /* lalr1.java:93  */

import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

@SuppressWarnings({"fallthrough", "unused"})

/* "LFParser.java":52  */ /* lalr1.java:93  */

/**
 * A Bison parser, automatically generated from <tt>LFParser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class LFParser
{
    /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.0.4";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";


  /**
   * True if verbose error messages are enabled.
   */
  private boolean yyErrorVerbose = true;

  /**
   * Return whether verbose error messages are enabled.
   */
  public final boolean getErrorVerbose() { return yyErrorVerbose; }

  /**
   * Set the verbosity of error messages.
   * @param verbose True to request verbose error messages.
   */
  public final void setErrorVerbose(boolean verbose)
  { yyErrorVerbose = verbose; }




  

  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>LFParser</tt>.
   */
  public interface Lexer {
    /** Token returned by the scanner to signal the end of its input.  */
    public static final int EOF = 0;

/* Tokens.  */
    /** Token number,to be returned by the scanner.  */
    static final int ID = 258;
    /** Token number,to be returned by the scanner.  */
    static final int VAR = 259;
    /** Token number,to be returned by the scanner.  */
    static final int GVAR = 260;
    /** Token number,to be returned by the scanner.  */
    static final int COMPARISON = 261;
    /** Token number,to be returned by the scanner.  */
    static final int ARROW = 262;
    /** Token number,to be returned by the scanner.  */
    static final int STRING = 263;


    

    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal ();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex () throws java.io.IOException;

    /**
     * Entry point for error reporting.  Emits an error
     * in a user-defined way.
     *
     * 
     * @param msg The string for the error message.
     */
     void yyerror (String msg);
  }

  /**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;
  
  



  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public LFParser (Lexer yylexer) 
  {
    
    this.yylexer = yylexer;
    
  }

  private java.io.PrintStream yyDebugStream = System.err;

  /**
   * Return the <tt>PrintStream</tt> on which the debugging output is
   * printed.
   */
  public final java.io.PrintStream getDebugStream () { return yyDebugStream; }

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

  /**
   * Print an error message via the lexer.
   *
   * @param msg The error message.
   */
  public final void yyerror (String msg)
  {
    yylexer.yyerror (msg);
  }


  protected final void yycdebug (String s) {
    if (yydebug > 0)
      yyDebugStream.println (s);
  }

  private final class YYStack {
    private int[] stateStack = new int[16];
    
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push (int state, Object value                            ) {
      height++;
      if (size == height)
        {
          int[] newStateStack = new int[size * 2];
          System.arraycopy (stateStack, 0, newStateStack, 0, height);
          stateStack = newStateStack;
          

          Object[] newValueStack = new Object[size * 2];
          System.arraycopy (valueStack, 0, newValueStack, 0, height);
          valueStack = newValueStack;

          size *= 2;
        }

      stateStack[height] = state;
      
      valueStack[height] = value;
    }

    public final void pop () {
      pop (1);
    }

    public final void pop (int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (num > 0) {
        java.util.Arrays.fill (valueStack, height - num + 1, height + 1, null);
        
      }
      height -= num;
    }

    public final int stateAt (int i) {
      return stateStack[height - i];
    }

    public final Object valueAt (int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print (java.io.PrintStream out)
    {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++)
        {
          out.print (' ');
          out.print (stateStack[i]);
        }
      out.println ();
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
   * Return whether error recovery is being done.  In this state, the parser
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
  private int yy_lr_goto_state_ (int yystate, int yysym)
  {
    int yyr = yypgoto_[yysym - yyntokens_] + yystate;
    if (0 <= yyr && yyr <= yylast_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - yyntokens_];
  }

  private int yyaction (int yyn, YYStack yystack, int yylen) 
  {
    Object yyval;
    

    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    if (yylen > 0)
      yyval = yystack.valueAt (yylen - 1);
    else
      yyval = yystack.valueAt (0);

    yy_reduce_print (yyn, yystack);

    switch (yyn)
      {
          case 2:
  if (yyn == 2)
    /* "LFParser.y":131  */ /* lalr1.java:489  */
    { yyval = null; _lf = (( DagNode )(yystack.valueAt (1-(1)))); if (extMode) return YYACCEPT; };
  break;
    

  case 3:
  if (yyn == 3)
    /* "LFParser.y":132  */ /* lalr1.java:489  */
    { yyval = null; _lf = (( DagNode )(yystack.valueAt (1-(1)))); if (extMode) return YYACCEPT; };
  break;
    

  case 4:
  if (yyn == 4)
    /* "LFParser.y":133  */ /* lalr1.java:489  */
    { yyval = null; _lf = (( DagNode )(yystack.valueAt (1-(1)))); if (extMode) return YYACCEPT; };
  break;
    

  case 5:
  if (yyn == 5)
    /* "LFParser.y":134  */ /* lalr1.java:489  */
    { yyval = null; _lf = null; _correct = false; };
  break;
    

  case 6:
  if (yyn == 6)
    /* "LFParser.y":139  */ /* lalr1.java:489  */
    {
     yyval = unify(getNewLF((( String )(yystack.valueAt (7-(2))))).setNominal(),
                unify(newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (7-(4))))), (( DagNode )(yystack.valueAt (7-(6))))));
   };
  break;
    

  case 7:
  if (yyn == 7)
    /* "LFParser.y":143  */ /* lalr1.java:489  */
    {
     yyval = unify(getNewLF((( String )(yystack.valueAt (5-(2))))).setNominal(), (( DagNode )(yystack.valueAt (5-(4)))));
   };
  break;
    

  case 8:
  if (yyn == 8)
    /* "LFParser.y":146  */ /* lalr1.java:489  */
    { yyval = (( DagNode )(yystack.valueAt (3-(2)))).setNominal(); };
  break;
    

  case 9:
  if (yyn == 9)
    /* "LFParser.y":149  */ /* lalr1.java:489  */
    { yyval = unify((( DagNode )(yystack.valueAt (3-(1)))), (( DagNode )(yystack.valueAt (3-(3))))); };
  break;
    

  case 10:
  if (yyn == 10)
    /* "LFParser.y":150  */ /* lalr1.java:489  */
    { yyval = (( DagNode )(yystack.valueAt (1-(1)))); };
  break;
    

  case 11:
  if (yyn == 11)
    /* "LFParser.y":153  */ /* lalr1.java:489  */
    { yyval = newLF((( String )(yystack.valueAt (6-(2)))), (( DagNode )(yystack.valueAt (6-(5))))).setNominal(); };
  break;
    

  case 12:
  if (yyn == 12)
    /* "LFParser.y":154  */ /* lalr1.java:489  */
    { yyval = newLF((( String )(yystack.valueAt (4-(2)))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (4-(4))))))
                                    .setNominal();
                        };
  break;
    

  case 13:
  if (yyn == 13)
    /* "LFParser.y":157  */ /* lalr1.java:489  */
    { yyval = newLF((( String )(yystack.valueAt (4-(2)))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (4-(4))))))
                                    .setNominal();
                           };
  break;
    

  case 14:
  if (yyn == 14)
    /* "LFParser.y":161  */ /* lalr1.java:489  */
    { yyval = newLF((( String )(yystack.valueAt (6-(2)))),
                                     unify(getNewLF((( String )(yystack.valueAt (6-(4))))).setNominal(),
                                           newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (6-(6)))))))
                            .setNominal();
                        };
  break;
    

  case 15:
  if (yyn == 15)
    /* "LFParser.y":167  */ /* lalr1.java:489  */
    { yyval = newLF((( String )(yystack.valueAt (5-(2)))), getNewLF((( String )(yystack.valueAt (5-(4))))).setNominal())
                            .setNominal();
                        };
  break;
    

  case 16:
  if (yyn == 16)
    /* "LFParser.y":170  */ /* lalr1.java:489  */
    { yyval = unify(getNewLF((( String )(yystack.valueAt (3-(1))))).setNominal(),
                                     newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (3-(3))))));
                        };
  break;
    

  case 17:
  if (yyn == 17)
    /* "LFParser.y":173  */ /* lalr1.java:489  */
    { yyval = getNewLF((( String )(yystack.valueAt (2-(1))))).setNominal(); };
  break;
    

  case 18:
  if (yyn == 18)
    /* "LFParser.y":174  */ /* lalr1.java:489  */
    { yyval = newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (2-(2))))).setNominal(); };
  break;
    

  case 19:
  if (yyn == 19)
    /* "LFParser.y":175  */ /* lalr1.java:489  */
    { yyval = newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (1-(1))))); };
  break;
    

  case 20:
  if (yyn == 20)
    /* "LFParser.y":179  */ /* lalr1.java:489  */
    {
      DagNode prop = newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (5-(3)))));
      if ((( DagNode )(yystack.valueAt (5-(4)))) != null) {
        prop = unify(prop, (( DagNode )(yystack.valueAt (5-(4)))));
      }
      yyval = unify(getNewLF("raw").setNominal(),
             unify(newLF(DagNode.TYPE_FEAT_ID, (( String )(yystack.valueAt (5-(1))))), prop));
    };
  break;
    

  case 21:
  if (yyn == 21)
    /* "LFParser.y":188  */ /* lalr1.java:489  */
    {
	DagNode res = newLF((( String )(yystack.valueAt (5-(2)))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (5-(4)))))).setNominal();
	yyval = ((( DagNode )(yystack.valueAt (5-(5)))) == null) ? res : unify(res, (( DagNode )(yystack.valueAt (5-(5)))));
	};
  break;
    

  case 22:
  if (yyn == 22)
    /* "LFParser.y":192  */ /* lalr1.java:489  */
    {
	DagNode res = newLF((( String )(yystack.valueAt (5-(2)))), newLF(DagNode.PROP_FEAT_ID, (( String )(yystack.valueAt (5-(4)))))).setNominal();
	yyval = ((( DagNode )(yystack.valueAt (5-(5)))) == null) ? res : unify(res, (( DagNode )(yystack.valueAt (5-(5)))));
	};
  break;
    

  case 23:
  if (yyn == 23)
    /* "LFParser.y":196  */ /* lalr1.java:489  */
    { yyval = null; };
  break;
    

  case 24:
  if (yyn == 24)
    /* "LFParser.y":198  */ /* lalr1.java:489  */
    { yyval = (( DagNode )(yystack.valueAt (3-(2)))); };
  break;
    

  case 25:
  if (yyn == 25)
    /* "LFParser.y":200  */ /* lalr1.java:489  */
    { yyval = unify((( DagNode )(yystack.valueAt (3-(1)))), (( DagNode )(yystack.valueAt (3-(3))))); };
  break;
    

  case 26:
  if (yyn == 26)
    /* "LFParser.y":201  */ /* lalr1.java:489  */
    { yyval = (( DagNode )(yystack.valueAt (1-(1)))); };
  break;
    

  case 27:
  if (yyn == 27)
    /* "LFParser.y":204  */ /* lalr1.java:489  */
    { yyval = new DagNode((( String )(yystack.valueAt (1-(1))))); };
  break;
    

  case 28:
  if (yyn == 28)
    /* "LFParser.y":205  */ /* lalr1.java:489  */
    { yyval = new DagNode((( String )(yystack.valueAt (1-(1))))); };
  break;
    

  case 29:
  if (yyn == 29)
    /* "LFParser.y":206  */ /* lalr1.java:489  */
    { yyval = (( DagNode )(yystack.valueAt (3-(2)))); };
  break;
    

  case 30:
  if (yyn == 30)
    /* "LFParser.y":207  */ /* lalr1.java:489  */
    {
    DagNode d = corefs.get((( String )(yystack.valueAt (1-(1)))));
    if (d == null) {
      d = new DagNode(DagNode.TOP_TYPE);
      corefs.put((( String )(yystack.valueAt (1-(1)))), d);
    }
    yyval = d;
  };
  break;
    

  case 31:
  if (yyn == 31)
    /* "LFParser.y":215  */ /* lalr1.java:489  */
    { yyval = makeList((( LinkedList<DagNode> )(yystack.valueAt (3-(2))))); };
  break;
    

  case 32:
  if (yyn == 32)
    /* "LFParser.y":216  */ /* lalr1.java:489  */
    { yyval = makeDifflist((( LinkedList<DagNode> )(yystack.valueAt (3-(2))))); };
  break;
    

  case 33:
  if (yyn == 33)
    /* "LFParser.y":217  */ /* lalr1.java:489  */
    { yyval = makeList(Collections.emptyList()); };
  break;
    

  case 34:
  if (yyn == 34)
    /* "LFParser.y":218  */ /* lalr1.java:489  */
    { yyval = makeDifflist(Collections.emptyList()); };
  break;
    

  case 35:
  if (yyn == 35)
    /* "LFParser.y":221  */ /* lalr1.java:489  */
    { yyval = unify((( DagNode )(yystack.valueAt (3-(1)))), (( DagNode )(yystack.valueAt (3-(3))))); };
  break;
    

  case 36:
  if (yyn == 36)
    /* "LFParser.y":222  */ /* lalr1.java:489  */
    { yyval = (( DagNode )(yystack.valueAt (1-(1)))); };
  break;
    

  case 37:
  if (yyn == 37)
    /* "LFParser.y":225  */ /* lalr1.java:489  */
    { yyval = makePath((( LinkedList<String> )(yystack.valueAt (2-(1)))), (( DagNode )(yystack.valueAt (2-(2))))); };
  break;
    

  case 38:
  if (yyn == 38)
    /* "LFParser.y":228  */ /* lalr1.java:489  */
    { (( LinkedList<String> )(yystack.valueAt (3-(3)))).addFirst((( String )(yystack.valueAt (3-(1))))); yyval = (( LinkedList<String> )(yystack.valueAt (3-(3)))); };
  break;
    

  case 39:
  if (yyn == 39)
    /* "LFParser.y":229  */ /* lalr1.java:489  */
    { yyval = new LinkedList<String>() {{ add((( String )(yystack.valueAt (1-(1))))); }}; };
  break;
    

  case 40:
  if (yyn == 40)
    /* "LFParser.y":232  */ /* lalr1.java:489  */
    { (( LinkedList<DagNode> )(yystack.valueAt (3-(3)))).addFirst((( DagNode )(yystack.valueAt (3-(1))))); yyval = (( LinkedList<DagNode> )(yystack.valueAt (3-(3)))); };
  break;
    

  case 41:
  if (yyn == 41)
    /* "LFParser.y":233  */ /* lalr1.java:489  */
    { yyval = new LinkedList<DagNode>() {{ add((( DagNode )(yystack.valueAt (1-(1))))); }}; };
  break;
    

  case 42:
  if (yyn == 42)
    /* "LFParser.y":234  */ /* lalr1.java:489  */
    { yyval = null; };
  break;
    


/* "LFParser.java":671  */ /* lalr1.java:489  */
        default: break;
      }

    yy_symbol_print ("-> $$ =", yyr1_[yyn], yyval);

    yystack.pop (yylen);
    yylen = 0;

    /* Shift the result of the reduction.  */
    int yystate = yy_lr_goto_state_ (yystack.stateAt (0), yyr1_[yyn]);
    yystack.push (yystate, yyval);
    return YYNEWSTATE;
  }


  /* Return YYSTR after stripping away unnecessary quotes and
     backslashes, so that it's suitable for yyerror.  The heuristic is
     that double-quoting is unnecessary unless the string contains an
     apostrophe, a comma, or backslash (other than backslash-backslash).
     YYSTR is taken from yytname.  */
  private final String yytnamerr_ (String yystr)
  {
    if (yystr.charAt (0) == '"')
      {
        StringBuffer yyr = new StringBuffer ();
        strip_quotes: for (int i = 1; i < yystr.length (); i++)
          switch (yystr.charAt (i))
            {
            case '\'':
            case ',':
              break strip_quotes;

            case '\\':
              if (yystr.charAt(++i) != '\\')
                break strip_quotes;
              /* Fall through.  */
            default:
              yyr.append (yystr.charAt (i));
              break;

            case '"':
              return yyr.toString ();
            }
      }
    else if (yystr.equals ("$end"))
      return "end of input";

    return yystr;
  }


  /*--------------------------------.
  | Print this symbol on YYOUTPUT.  |
  `--------------------------------*/

  private void yy_symbol_print (String s, int yytype,
                                 Object yyvaluep                                 )
  {
    if (yydebug > 0)
    yycdebug (s + (yytype < yyntokens_ ? " token " : " nterm ")
              + yytname_[yytype] + " ("
              + (yyvaluep == null ? "(null)" : yyvaluep.toString ()) + ")");
  }


  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
   public boolean parse () throws java.io.IOException

  {
    


    /* Lookahead and lookahead in internal form.  */
    int yychar = yyempty_;
    int yytoken = 0;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;
    YYStack yystack = new YYStack ();
    int label = YYNEWSTATE;

    /* Error handling.  */
    int yynerrs_ = 0;
    

    /* Semantic value of the lookahead.  */
    Object yylval = null;

    yycdebug ("Starting parse\n");
    yyerrstatus_ = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval );



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:
        yycdebug ("Entering state " + yystate + "\n");
        if (yydebug > 0)
          yystack.print (yyDebugStream);

        /* Accept?  */
        if (yystate == yyfinal_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yy_pact_value_is_default_ (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == yyempty_)
          {


            yycdebug ("Reading a token: ");
            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal ();

          }

        /* Convert token to internal form.  */
        if (yychar <= Lexer.EOF)
          {
            yychar = yytoken = Lexer.EOF;
            yycdebug ("Now at end of input.\n");
          }
        else
          {
            yytoken = yytranslate_ (yychar);
            yy_symbol_print ("Next token is", yytoken,
                             yylval);
          }

        /* If the proper action on seeing token YYTOKEN is to reduce or to
           detect an error, take that action.  */
        yyn += yytoken;
        if (yyn < 0 || yylast_ < yyn || yycheck_[yyn] != yytoken)
          label = YYDEFAULT;

        /* <= 0 means reduce or error.  */
        else if ((yyn = yytable_[yyn]) <= 0)
          {
            if (yy_table_value_is_error_ (yyn))
              label = YYERRLAB;
            else
              {
                yyn = -yyn;
                label = YYREDUCE;
              }
          }

        else
          {
            /* Shift the lookahead token.  */
            yy_symbol_print ("Shifting", yytoken,
                             yylval);

            /* Discard the token being shifted.  */
            yychar = yyempty_;

            /* Count tokens shifted since error; after three, turn off error
               status.  */
            if (yyerrstatus_ > 0)
              --yyerrstatus_;

            yystate = yyn;
            yystack.push (yystate, yylval);
            label = YYNEWSTATE;
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
        label = yyaction (yyn, yystack, yylen);
        yystate = yystack.stateAt (0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs_;
            if (yychar == yyempty_)
              yytoken = yyempty_;
            yyerror (yysyntax_error (yystate, yytoken));
          }

        
        if (yyerrstatus_ == 3)
          {
        /* If just tried and failed to reuse lookahead token after an
         error, discard it.  */

        if (yychar <= Lexer.EOF)
          {
          /* Return failure if at end of input.  */
          if (yychar == Lexer.EOF)
            return false;
          }
        else
            yychar = yyempty_;
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
        yystate = yystack.stateAt (0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;       /* Each real token shifted decrements this.  */

        for (;;)
          {
            yyn = yypact_[yystate];
            if (!yy_pact_value_is_default_ (yyn))
              {
                yyn += yyterror_;
                if (0 <= yyn && yyn <= yylast_ && yycheck_[yyn] == yyterror_)
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
            yystate = yystack.stateAt (0);
            if (yydebug > 0)
              yystack.print (yyDebugStream);
          }

        if (label == YYABORT)
            /* Leave the switch.  */
            break;



        /* Shift the error token.  */
        yy_symbol_print ("Shifting", yystos_[yyn],
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




  // Generate an error message.
  private String yysyntax_error (int yystate, int tok)
  {
    if (yyErrorVerbose)
      {
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
        if (tok != yyempty_)
          {
            /* FIXME: This method of building the message is not compatible
               with internationalization.  */
            StringBuffer res =
              new StringBuffer ("syntax error, unexpected ");
            res.append (yytnamerr_ (yytname_[tok]));
            int yyn = yypact_[yystate];
            if (!yy_pact_value_is_default_ (yyn))
              {
                /* Start YYX at -YYN if negative to avoid negative
                   indexes in YYCHECK.  In other words, skip the first
                   -YYN actions for this state because they are default
                   actions.  */
                int yyxbegin = yyn < 0 ? -yyn : 0;
                /* Stay within bounds of both yycheck and yytname.  */
                int yychecklim = yylast_ - yyn + 1;
                int yyxend = yychecklim < yyntokens_ ? yychecklim : yyntokens_;
                int count = 0;
                for (int x = yyxbegin; x < yyxend; ++x)
                  if (yycheck_[x + yyn] == x && x != yyterror_
                      && !yy_table_value_is_error_ (yytable_[x + yyn]))
                    ++count;
                if (count < 5)
                  {
                    count = 0;
                    for (int x = yyxbegin; x < yyxend; ++x)
                      if (yycheck_[x + yyn] == x && x != yyterror_
                          && !yy_table_value_is_error_ (yytable_[x + yyn]))
                        {
                          res.append (count++ == 0 ? ", expecting " : " or ");
                          res.append (yytnamerr_ (yytname_[x]));
                        }
                  }
              }
            return res.toString ();
          }
      }

    return "syntax error";
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yy_pact_value_is_default_ (int yyvalue)
  {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yy_table_value_is_error_ (int yyvalue)
  {
    return yyvalue == yytable_ninf_;
  }

  private static final byte yypact_ninf_ = -48;
  private static final byte yytable_ninf_ = -1;

  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final byte yypact_[] = yypact_init();
  private static final byte[] yypact_init()
  {
    return new byte[]
    {
      45,   -48,     8,     3,     6,    39,    11,   -48,   -48,   -48,
      19,    15,    28,    41,    47,    46,    38,   -48,   -48,   -48,
       0,    58,     9,    43,    48,   -48,    49,    63,     6,    66,
     -48,    55,   -48,     6,   -48,   -48,    57,    59,    52,    53,
      60,    39,   -48,    61,   -48,    39,    68,    65,    67,    69,
     -48,    56,   -48,    31,   -48,    58,   -48,    58,   -48,   -48,
     -48,    62,   -48,     6,   -48,    70,   -48,     6,   -48,   -48,
     -48,     2,    72,    79,    73,    49,    49,   -48,   -48,   -48,
     -48,   -48
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final byte yydefact_[] = yydefact_init();
  private static final byte[] yydefact_init()
  {
    return new byte[]
    {
       0,     5,     0,     0,     0,     0,     0,     2,     3,     4,
       0,     0,    19,     0,     0,     0,    10,    27,    30,    28,
       0,     0,     0,     0,    26,     1,    23,     0,     0,    17,
      18,     0,     8,     0,    33,    42,    41,     0,    39,     0,
      36,     0,    34,     0,    24,     0,     0,     0,     0,     0,
      16,     0,     9,     0,    31,     0,    29,     0,    37,    32,
      25,     0,    20,     0,     7,    12,    13,     0,    40,    38,
      35,     0,     0,    15,     0,    23,    23,     6,    14,    11,
      22,    21
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final byte yypgoto_[] = yypgoto_init();
  private static final byte[] yypgoto_init()
  {
    return new byte[]
    {
     -48,   -48,   -48,   -26,   -48,   -48,   -47,   -48,    -5,   -48,
      29,   -48,    32,   -21
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final byte yydefgoto_[] = yydefgoto_init();
  private static final byte[] yydefgoto_init()
  {
    return new byte[]
    {
      -1,     6,     7,    15,    16,     8,    47,     9,    36,    24,
      39,    40,    41,    37
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final byte yytable_[] = yytable_init();
  private static final byte[] yytable_init()
  {
    return new byte[]
    {
      23,    43,    49,    17,    18,    75,    11,    52,    19,    12,
      76,    25,    17,    18,    20,    34,    13,    19,    21,    10,
      14,    22,    26,    20,    35,    27,    28,    21,    80,    81,
      22,    42,    68,    35,    17,    18,    58,    72,    29,    19,
      60,    74,    17,    18,    30,    20,     1,    19,     2,    21,
      31,    33,    22,    20,     3,    35,     4,    21,    32,    65,
      22,    38,    44,     5,    66,    46,    48,    67,    45,    50,
      51,    61,    56,    53,    54,    55,    57,    62,    63,    71,
      73,    64,    78,    59,    77,    79,    70,    69
    };
  }

private static final byte yycheck_[] = yycheck_init();
  private static final byte[] yycheck_init()
  {
    return new byte[]
    {
       5,    22,    28,     3,     4,     3,     3,    33,     8,     3,
       8,     0,     3,     4,    14,    15,    10,     8,    18,    11,
      14,    21,     3,    14,    24,    10,    11,    18,    75,    76,
      21,    22,    53,    24,     3,     4,    41,    63,    10,     8,
      45,    67,     3,     4,     3,    14,     1,     8,     3,    18,
       3,    13,    21,    14,     9,    24,    11,    18,    12,     3,
      21,     3,    19,    18,     8,    16,     3,    11,    20,     3,
      15,     3,    19,    16,    15,    23,    16,    12,    11,    17,
      10,    12,     3,    22,    12,    12,    57,    55
    };
  }

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
  private static final byte yystos_[] = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,     1,     3,     9,    11,    18,    26,    27,    30,    32,
      11,     3,     3,    10,    14,    28,    29,     3,     4,     8,
      14,    18,    21,    33,    34,     0,     3,    10,    11,    10,
       3,     3,    12,    13,    15,    24,    33,    38,     3,    35,
      36,    37,    22,    38,    19,    20,    16,    31,     3,    28,
       3,    15,    28,    16,    15,    23,    19,    16,    33,    22,
      33,     3,    12,    11,    12,     3,     8,    11,    38,    37,
      35,    17,    28,    10,    28,     3,     8,    12,     3,    12,
      31,    31
    };
  }

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final byte yyr1_[] = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    25,    26,    26,    26,    26,    27,    27,    27,    28,
      28,    29,    29,    29,    29,    29,    29,    29,    29,    29,
      30,    31,    31,    31,    32,    33,    33,    34,    34,    34,
      34,    34,    34,    34,    34,    35,    35,    36,    37,    37,
      38,    38,    38
    };
  }

/* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
  private static final byte yyr2_[] = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     1,     1,     1,     7,     5,     3,     3,
       1,     6,     4,     4,     6,     5,     3,     2,     2,     1,
       5,     5,     5,     0,     3,     3,     1,     1,     1,     3,
       1,     3,     3,     2,     2,     3,     1,     2,     3,     1,
       3,     1,     1
    };
  }

  /* YYTOKEN_NUMBER[YYLEX-NUM] -- Internal symbol number corresponding
      to YYLEX-NUM.  */
  private static final short yytoken_number_[] = yytoken_number_init();
  private static final short[] yytoken_number_init()
  {
    return new short[]
    {
       0,   256,   264,   258,   259,   260,   261,   262,   263,    64,
      58,    40,    41,    94,    60,    62,    44,    61,    91,    93,
      38,   265,   266,    46,   267
    };
  }

  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  private static final String yytname_[] = yytname_init();
  private static final String[] yytname_init()
  {
    return new String[]
    {
  "$end", "error", "$undefined", "ID", "VAR", "GVAR", "COMPARISON",
  "ARROW", "STRING", "'@'", "':'", "'('", "')'", "'^'", "'<'", "'>'",
  "','", "'='", "'['", "']'", "'&'", "\"<!\"", "\"!>\"", "'.'", "\"...\"",
  "$accept", "start", "lf", "lfconj", "lfterm", "rawlf", "keyval", "tdllf",
  "nodes", "node", "featvals", "featval", "path", "elements", null
    };
  }

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  private static final short yyrline_[] = yyrline_init();
  private static final short[] yyrline_init()
  {
    return new short[]
    {
       0,   131,   131,   132,   133,   134,   139,   143,   146,   149,
     150,   153,   154,   157,   160,   166,   170,   173,   174,   175,
     178,   188,   192,   196,   198,   200,   201,   204,   205,   206,
     207,   215,   216,   217,   218,   221,   222,   225,   228,   229,
     232,   233,   234
    };
  }


  // Report on the debug stream that the rule yyrule is going to be reduced.
  private void yy_reduce_print (int yyrule, YYStack yystack)
  {
    if (yydebug == 0)
      return;

    int yylno = yyrline_[yyrule];
    int yynrhs = yyr2_[yyrule];
    /* Print the symbols being reduced, and their result.  */
    yycdebug ("Reducing stack by rule " + (yyrule - 1)
              + " (line " + yylno + "), ");

    /* The symbols being reduced.  */
    for (int yyi = 0; yyi < yynrhs; yyi++)
      yy_symbol_print ("   $" + (yyi + 1) + " =",
                       yystos_[yystack.stateAt(yynrhs - (yyi + 1))],
                       ((yystack.valueAt (yynrhs-(yyi + 1)))));
  }

  /* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
  private static final byte yytranslate_table_[] = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    20,     2,
      11,    12,     2,     2,    16,     2,    23,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    10,     2,
      14,    17,    15,     2,     9,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,    18,     2,    19,    13,     2,     2,     2,     2,     2,
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
       5,     6,     7,     8,     2,    21,    22,    24
    };
  }

  private static final byte yytranslate_ (int t)
  {
    if (t >= 0 && t <= yyuser_token_number_max_)
      return yytranslate_table_[t];
    else
      return yyundef_token_;
  }

  private static final int yylast_ = 87;
  private static final int yynnts_ = 14;
  private static final int yyempty_ = -2;
  private static final int yyfinal_ = 25;
  private static final int yyterror_ = 1;
  private static final int yyerrcode_ = 256;
  private static final int yyntokens_ = 25;

  private static final int yyuser_token_number_max_ = 267;
  private static final int yyundef_token_ = 2;

/* User implementation code.  */
/* Unqualified %code blocks.  */
/* "LFParser.y":24  */ /* lalr1.java:1066  */

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
    corefs.clear();
  }

  public boolean correct() { return _correct; }

  HashMap<String, DagNode> corefs = new HashMap<>();

  DagNode makePath(LinkedList<String> path, DagNode val) {
    Iterator<String> it = path.descendingIterator();
    while (it.hasNext()) {
      val = new DagNode(it.next(), val);
    }
    return val;
  }

  DagNode makeList(List<DagNode> nodes) {
    return null;
  }

  DagNode makeDifflist(List<DagNode> nodes) {
    return null;
  }

/* "LFParser.java":1436  */ /* lalr1.java:1066  */

}

