/* A Bison parser, made by GNU Bison 3.5.1.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015, 2018-2020 Free Software Foundation, Inc.

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



/* "%code imports" blocks.  */
/* "RuleParser.y":3  */

import static de.dfki.lt.tr.dialogue.cplan.BasicRule.appendMatches;
import static de.dfki.lt.tr.dialogue.cplan.BasicRule.appendActions;

import java.io.Reader;
import java.util.List;
import java.util.LinkedList;

import de.dfki.lt.tr.dialogue.cplan.Path;
import de.dfki.lt.tr.dialogue.cplan.util.Position;
import de.dfki.lt.tr.dialogue.cplan.matches.*;
import de.dfki.lt.tr.dialogue.cplan.actions.*;

@SuppressWarnings({"rawtypes", "unchecked", "fallthrough", "unused"})

/* "RuleParser.java":55  */

/**
 * A Bison parser, automatically generated from <tt>RuleParser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class RuleParser
{
    /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.5.1";

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



  /**
   * A class defining a pair of positions.  Positions, defined by the
   * <code>Position</code> class, denote a point in the input.
   * Locations represent a part of the input through the beginning
   * and ending positions.
   */
  public class Location {
    /**
     * The first, inclusive, position in the range.
     */
    public Position begin;

    /**
     * The first position beyond the range.
     */
    public Position end;

    /**
     * Create a <code>Location</code> denoting an empty range located at
     * a given point.
     * @param loc The position at which the range is anchored.
     */
    public Location (Position loc) {
      this.begin = this.end = loc;
    }

    /**
     * Create a <code>Location</code> from the endpoints of the range.
     * @param begin The first position included in the range.
     * @param end   The first position beyond the range.
     */
    public Location (Position begin, Position end) {
      this.begin = begin;
      this.end = end;
    }

    /**
     * Print a representation of the location.  For this to be correct,
     * <code>Position</code> should override the <code>equals</code>
     * method.
     */
    public String toString () {
      if (begin.equals (end))
        return begin.toString ();
      else
        return begin.toString () + "-" + end.toString ();
    }
  }




  private Location yylloc (YYStack rhs, int n)
  {
    if (0 < n)
      return new Location (rhs.locationAt (n-1).begin, rhs.locationAt (0).end);
    else
      return new Location (rhs.locationAt (0).end);
  }

  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>RuleParser</tt>.
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
    static final int ARROW = 262;
    /** Token number,to be returned by the scanner.  */
    static final int STRING = 263;
    /** Token number,to be returned by the scanner.  */
    static final int RVAR = 264;


    /**
     * Method to retrieve the beginning position of the last scanned token.
     * @return the position at which the last scanned token starts.
     */
    Position getStartPos ();

    /**
     * Method to retrieve the ending position of the last scanned token.
     * @return the first position beyond the last scanned token.
     */
    Position getEndPos ();

    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal ();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * and beginning/ending positions of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex () throws java.io.IOException;

    /**
     * Entry point for error reporting.  Emits an error
     * referring to the given location in a user-defined way.
     *
     * @param loc The location of the element to which the
     *                error message is related
     * @param msg The string for the error message.
     */
     void yyerror (Location loc, String msg);
  }

/**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;

  



  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public RuleParser (Lexer yylexer) 
  {
    
    this.yylexer = yylexer;
    
  }



  /**
   * Print an error message via the lexer.
   * Use a <code>null</code> location.
   * @param msg The error message.
   */
  public final void yyerror (String msg)
  {
    yylexer.yyerror ((Location)null, msg);
  }

  /**
   * Print an error message via the lexer.
   * @param loc The location associated with the message.
   * @param msg The error message.
   */
  public final void yyerror (Location loc, String msg)
  {
    yylexer.yyerror (loc, msg);
  }

  /**
   * Print an error message via the lexer.
   * @param pos The position associated with the message.
   * @param msg The error message.
   */
  public final void yyerror (Position pos, String msg)
  {
    yylexer.yyerror (new Location (pos), msg);
  }


  private final class YYStack {
    private int[] stateStack = new int[16];
    private Location[] locStack = new Location[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push (int state, Object value                            , Location loc) {
      height++;
      if (size == height)
        {
          int[] newStateStack = new int[size * 2];
          System.arraycopy (stateStack, 0, newStateStack, 0, height);
          stateStack = newStateStack;
          
          Location[] newLocStack = new Location[size * 2];
          System.arraycopy (locStack, 0, newLocStack, 0, height);
          locStack = newLocStack;

          Object[] newValueStack = new Object[size * 2];
          System.arraycopy (valueStack, 0, newValueStack, 0, height);
          valueStack = newValueStack;

          size *= 2;
        }

      stateStack[height] = state;
      locStack[height] = loc;
      valueStack[height] = value;
    }

    public final void pop () {
      pop (1);
    }

    public final void pop (int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (0 < num) {
        java.util.Arrays.fill (valueStack, height - num + 1, height + 1, null);
        java.util.Arrays.fill (locStack, height - num + 1, height + 1, null);
      }
      height -= num;
    }

    public final int stateAt (int i) {
      return stateStack[height - i];
    }

    public final Location locationAt (int i) {
      return locStack[height - i];
    }

    public final Object valueAt (int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print (java.io.PrintStream out) {
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
  private int yyLRGotoState (int yystate, int yysym)
  {
    int yyr = yypgoto_[yysym - yyntokens_] + yystate;
    if (0 <= yyr && yyr <= yylast_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - yyntokens_];
  }

  private int yyaction (int yyn, YYStack yystack, int yylen) 
  {
    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    Object yyval = (0 < yylen) ? yystack.valueAt (yylen - 1) : yystack.valueAt (0);
    Location yyloc = yylloc (yystack, yylen);

    switch (yyn)
      {
          case 2:
  if (yyn == 2)
    /* "RuleParser.y":261  */
                         { _rules = (( LinkedList )(yystack.valueAt (0))); };
  break;
    

  case 3:
  if (yyn == 3)
    /* "RuleParser.y":263  */
                      { _rules = (( LinkedList )(yystack.valueAt (1))); _rules.addAll((( LinkedList )(yystack.valueAt (0)))); yyval = _rules; };
  break;
    

  case 4:
  if (yyn == 4)
    /* "RuleParser.y":264  */
                      { yyval = (( LinkedList )(yystack.valueAt (0))); };
  break;
    

  case 5:
  if (yyn == 5)
    /* "RuleParser.y":268  */
        { yyval = newRuleList(newRule((( Match )(yystack.valueAt (3))), (( List )(yystack.valueAt (1))), yystack.locationAt (2), (( String )(yystack.valueAt (2))).equals("->"))); };
  break;
    

  case 6:
  if (yyn == 6)
    /* "RuleParser.y":270  */
        { yyval = newRule((( Match )(yystack.valueAt (5))), (( List )(yystack.valueAt (3))), yystack.locationAt (4), (( String )(yystack.valueAt (4))).equals("->")).applyToAll((( LinkedList )(yystack.valueAt (1)))); };
  break;
    

  case 7:
  if (yyn == 7)
    /* "RuleParser.y":272  */
        { yyval = newRule((( Match )(yystack.valueAt (3))), new LinkedList<Action>(), yystack.locationAt (2), true).applyToAll((( LinkedList )(yystack.valueAt (1)))); };
  break;
    

  case 8:
  if (yyn == 8)
    /* "RuleParser.y":273  */
                               { yyval = new LinkedList<RuleProto>(); };
  break;
    

  case 9:
  if (yyn == 9)
    /* "RuleParser.y":276  */
                             { (( LinkedList )(yystack.valueAt (0))).addFirst(new Op('|', (( LinkedList )(yystack.valueAt (1))))); yyval = (( LinkedList )(yystack.valueAt (0))); };
  break;
    

  case 10:
  if (yyn == 10)
    /* "RuleParser.y":277  */
                             { (( LinkedList )(yystack.valueAt (0))).addFirst(new Op('^', (( LinkedList )(yystack.valueAt (1))))); yyval = (( LinkedList )(yystack.valueAt (0))); };
  break;
    

  case 11:
  if (yyn == 11)
    /* "RuleParser.y":278  */
                             { yyval = newOpList('|', (( LinkedList )(yystack.valueAt (0)))); };
  break;
    

  case 12:
  if (yyn == 12)
    /* "RuleParser.y":279  */
                             { yyval = newOpList('^', (( LinkedList )(yystack.valueAt (0)))); };
  break;
    

  case 13:
  if (yyn == 13)
    /* "RuleParser.y":282  */
                          { yyval = new Disjunction((( Match )(yystack.valueAt (2))), (( Match )(yystack.valueAt (0)))); };
  break;
    

  case 15:
  if (yyn == 15)
    /* "RuleParser.y":286  */
                           { yyval = new Conjunction((( Match )(yystack.valueAt (2))), (( Match )(yystack.valueAt (0)))); };
  break;
    

  case 17:
  if (yyn == 17)
    /* "RuleParser.y":291  */
                                { yyval = new FeatVal((( Match )(yystack.valueAt (2))), (( Match )(yystack.valueAt (0)))); };
  break;
    

  case 18:
  if (yyn == 18)
    /* "RuleParser.y":292  */
                                { yyval = new FeatVal((( Match )(yystack.valueAt (1))), null); };
  break;
    

  case 19:
  if (yyn == 19)
    /* "RuleParser.y":293  */
                                { yyval = new VarMatch(new GlobalVar((( String )(yystack.valueAt (3)))), (( Match )(yystack.valueAt (1)))); };
  break;
    

  case 20:
  if (yyn == 20)
    /* "RuleParser.y":294  */
                                { yyval = new VarMatch((( MatchLVal )(yystack.valueAt (3))), (( Match )(yystack.valueAt (1)))); };
  break;
    

  case 21:
  if (yyn == 21)
    /* "RuleParser.y":295  */
                                { yyval = (( Match )(yystack.valueAt (0))); };
  break;
    

  case 22:
  if (yyn == 22)
    /* "RuleParser.y":298  */
                          { yyval = (( Match )(yystack.valueAt (0))); };
  break;
    

  case 23:
  if (yyn == 23)
    /* "RuleParser.y":299  */
                          { yyval = new Conjunction((( Match )(yystack.valueAt (1))),
                                   new FeatVal(DagNode.TYPE_FEAT_ID, (( Match )(yystack.valueAt (0))))); };
  break;
    

  case 24:
  if (yyn == 24)
    /* "RuleParser.y":301  */
                          { yyval = new FeatVal(DagNode.TYPE_FEAT_ID, (( Match )(yystack.valueAt (0)))); };
  break;
    

  case 25:
  if (yyn == 25)
    /* "RuleParser.y":302  */
                          { yyval = new FeatVal(DagNode.PROP_FEAT_ID, (( Match )(yystack.valueAt (0)))); };
  break;
    

  case 26:
  if (yyn == 26)
    /* "RuleParser.y":303  */
                          { yyval = new FeatVal(DagNode.PROP_FEAT_ID,
                                             new Atom((( String )(yystack.valueAt (0))))); };
  break;
    

  case 27:
  if (yyn == 27)
    /* "RuleParser.y":305  */
                          { (( Match )(yystack.valueAt (0))).setNegated(true); yyval = (( Match )(yystack.valueAt (0))); };
  break;
    

  case 28:
  if (yyn == 28)
    /* "RuleParser.y":306  */
                          { yyval = (( Match )(yystack.valueAt (1))); };
  break;
    

  case 29:
  if (yyn == 29)
    /* "RuleParser.y":307  */
                          { yyval = new Coref((( Match )(yystack.valueAt (0)))); };
  break;
    

  case 30:
  if (yyn == 30)
    /* "RuleParser.y":314  */
                     { yyval = new FeatVal(DagNode.ID_FEAT_ID, new Atom((( String )(yystack.valueAt (1))))); };
  break;
    

  case 31:
  if (yyn == 31)
    /* "RuleParser.y":315  */
                     { yyval = new LocalVar((( String )(yystack.valueAt (1))));  };
  break;
    

  case 32:
  if (yyn == 32)
    /* "RuleParser.y":316  */
                     { yyval = new GlobalVar((( String )(yystack.valueAt (1)))); };
  break;
    

  case 33:
  if (yyn == 33)
    /* "RuleParser.y":319  */
                   { yyval = new LocalVar((( String )(yystack.valueAt (0)))); };
  break;
    

  case 34:
  if (yyn == 34)
    /* "RuleParser.y":320  */
                   { yyval = new Atom((( String )(yystack.valueAt (0)))); };
  break;
    

  case 35:
  if (yyn == 35)
    /* "RuleParser.y":322  */
                            { yyval = (( Match )(yystack.valueAt (0))); };
  break;
    

  case 36:
  if (yyn == 36)
    /* "RuleParser.y":323  */
                            { (( Match )(yystack.valueAt (0))).setNegated(true); yyval = (( Match )(yystack.valueAt (0))); };
  break;
    

  case 37:
  if (yyn == 37)
    /* "RuleParser.y":324  */
                            { yyval = (( Match )(yystack.valueAt (1))); };
  break;
    

  case 38:
  if (yyn == 38)
    /* "RuleParser.y":327  */
                                { yyval = (( Match )(yystack.valueAt (0))); };
  break;
    

  case 39:
  if (yyn == 39)
    /* "RuleParser.y":328  */
                                { yyval = new Disjunction((( Match )(yystack.valueAt (2))), (( Match )(yystack.valueAt (0)))); };
  break;
    

  case 40:
  if (yyn == 40)
    /* "RuleParser.y":331  */
                           { yyval = getNewFunCall((( String )(yystack.valueAt (3))), (( List )(yystack.valueAt (1))), yystack.locationAt (3));
                             if (yyval == null) return YYERROR ;
                           };
  break;
    

  case 41:
  if (yyn == 41)
    /* "RuleParser.y":334  */
                           { yyval = getNewFunCall((( String )(yystack.valueAt (2))), null, yystack.locationAt (2));
                             if (yyval == null) return YYERROR ;
                           };
  break;
    

  case 42:
  if (yyn == 42)
    /* "RuleParser.y":357  */
                 {
            List<Action> result  = new LinkedList<Action>();
            result.add((( Action )(yystack.valueAt (0))));
            yyval = result;
          };
  break;
    

  case 43:
  if (yyn == 43)
    /* "RuleParser.y":362  */
                              { (( List )(yystack.valueAt (0))).add(0, (( Action )(yystack.valueAt (2)))); yyval = (( List )(yystack.valueAt (0))); };
  break;
    

  case 44:
  if (yyn == 44)
    /* "RuleParser.y":366  */
       {
         DagNode rval = (((( DagNode )(yystack.valueAt (0))) == null) ? null : (( DagNode )(yystack.valueAt (0))).copyAndInvalidate());
         yyval = new Assignment((( VarDagNode )(yystack.valueAt (2))), rval);
       };
  break;
    

  case 45:
  if (yyn == 45)
    /* "RuleParser.y":371  */
       {
         DagNode rval = (((( DagNode )(yystack.valueAt (0))) == null) ? null : (( DagNode )(yystack.valueAt (0))).copyAndInvalidate());
         yyval = new Addition((( VarDagNode )(yystack.valueAt (2))), rval);
       };
  break;
    

  case 46:
  if (yyn == 46)
    /* "RuleParser.y":378  */
       { yyval = new Deletion((( VarDagNode )(yystack.valueAt (4))), new DagNode((( String )(yystack.valueAt (1))), new DagNode())); };
  break;
    

  case 47:
  if (yyn == 47)
    /* "RuleParser.y":381  */
                 { yyval = new VarDagNode((( String )(yystack.valueAt (0))), Bindings.LOCAL); };
  break;
    

  case 48:
  if (yyn == 48)
    /* "RuleParser.y":382  */
                 { yyval = new VarDagNode((( String )(yystack.valueAt (0))), Bindings.RIGHTLOCAL); };
  break;
    

  case 49:
  if (yyn == 49)
    /* "RuleParser.y":383  */
                 { yyval = new VarDagNode("#", Bindings.LOCAL); };
  break;
    

  case 50:
  if (yyn == 50)
    /* "RuleParser.y":384  */
                 { yyval = new VarDagNode((( String )(yystack.valueAt (1))), (( Path )(yystack.valueAt (0)))); };
  break;
    

  case 51:
  if (yyn == 51)
    /* "RuleParser.y":388  */
                       { yyval = (( Path )(yystack.valueAt (0))).addToFront((( String )(yystack.valueAt (2)))); };
  break;
    

  case 52:
  if (yyn == 52)
    /* "RuleParser.y":389  */
                       { yyval = new Path(); };
  break;
    

  case 53:
  if (yyn == 53)
    /* "RuleParser.y":392  */
                          { (( DagNode )(yystack.valueAt (2))).add((( DagNode )(yystack.valueAt (0)))); (( DagNode )(yystack.valueAt (2))).setNominal(); yyval = (( DagNode )(yystack.valueAt (2))); };
  break;
    

  case 54:
  if (yyn == 54)
    /* "RuleParser.y":393  */
                          { yyval = (( DagNode )(yystack.valueAt (0))); };
  break;
    

  case 55:
  if (yyn == 55)
    /* "RuleParser.y":396  */
                           { yyval = new DagNode((( String )(yystack.valueAt (2))), (( DagNode )(yystack.valueAt (0)))).setNominal(); };
  break;
    

  case 56:
  if (yyn == 56)
    /* "RuleParser.y":397  */
                           {
          yyval = new DagNode(new VarEdge((( String )(yystack.valueAt (2))), Bindings.LOCAL, (( DagNode )(yystack.valueAt (0))))).setNominal();
        };
  break;
    

  case 57:
  if (yyn == 57)
    /* "RuleParser.y":400  */
                           {
          yyval = new DagNode(new VarEdge((( String )(yystack.valueAt (2))), Bindings.RIGHTLOCAL, (( DagNode )(yystack.valueAt (0)))))
                     .setNominal();
        };
  break;
    

  case 58:
  if (yyn == 58)
    /* "RuleParser.y":404  */
                                {
         yyval = new DagNode(new VarEdge((( String )(yystack.valueAt (3))), (( Path )(yystack.valueAt (2))), (( DagNode )(yystack.valueAt (0))))).setNominal();
      };
  break;
    

  case 59:
  if (yyn == 59)
    /* "RuleParser.y":407  */
                          { yyval = (( DagNode )(yystack.valueAt (0))); };
  break;
    

  case 60:
  if (yyn == 60)
    /* "RuleParser.y":411  */
                          { yyval = (( DagNode )(yystack.valueAt (0))); };
  break;
    

  case 61:
  if (yyn == 61)
    /* "RuleParser.y":412  */
                          { (( DagNode )(yystack.valueAt (1))).add(new DagNode(DagNode.TYPE_FEAT_ID, (( DagNode )(yystack.valueAt (0)))));
                            yyval = (( DagNode )(yystack.valueAt (1))); };
  break;
    

  case 62:
  if (yyn == 62)
    /* "RuleParser.y":414  */
                          { yyval = new DagNode(DagNode.TYPE_FEAT_ID, (( DagNode )(yystack.valueAt (0))))
                                    .setNominal();
                          };
  break;
    

  case 63:
  if (yyn == 63)
    /* "RuleParser.y":417  */
                          { yyval = new DagNode(DagNode.PROP_FEAT_ID, (( DagNode )(yystack.valueAt (0)))); };
  break;
    

  case 64:
  if (yyn == 64)
    /* "RuleParser.y":418  */
                          { yyval = new DagNode(DagNode.PROP_FEAT_ID,
                                             new DagNode((( String )(yystack.valueAt (0))))); };
  break;
    

  case 65:
  if (yyn == 65)
    /* "RuleParser.y":420  */
                          { yyval = (( DagNode )(yystack.valueAt (1))).setNominal(); };
  break;
    

  case 66:
  if (yyn == 66)
    /* "RuleParser.y":429  */
                        {
             yyval = new DagNode(DagNode.ID_FEAT_ID, (( DagNode )(yystack.valueAt (1)))).setNominal();
         };
  break;
    

  case 67:
  if (yyn == 67)
    /* "RuleParser.y":434  */
                         { (( List )(yystack.valueAt (0))).add(0, (( DagNode )(yystack.valueAt (2)))); yyval = (( List )(yystack.valueAt (0))); };
  break;
    

  case 68:
  if (yyn == 68)
    /* "RuleParser.y":435  */
                         { List<DagNode> result = new LinkedList<DagNode>();
                           result.add((( DagNode )(yystack.valueAt (0))));
                           yyval = result;
                         };
  break;
    

  case 69:
  if (yyn == 69)
    /* "RuleParser.y":441  */
                  { yyval = (( DagNode )(yystack.valueAt (0))); };
  break;
    

  case 70:
  if (yyn == 70)
    /* "RuleParser.y":442  */
                  { yyval = new DagNode((( String )(yystack.valueAt (0)))); };
  break;
    

  case 71:
  if (yyn == 71)
    /* "RuleParser.y":443  */
                  { yyval = new VarDagNode("#", Bindings.LOCAL); };
  break;
    

  case 72:
  if (yyn == 72)
    /* "RuleParser.y":449  */
                  { yyval = new DagNode((( String )(yystack.valueAt (0)))); };
  break;
    

  case 73:
  if (yyn == 73)
    /* "RuleParser.y":450  */
                  { yyval = new VarDagNode((( String )(yystack.valueAt (0))), Bindings.LOCAL); };
  break;
    

  case 74:
  if (yyn == 74)
    /* "RuleParser.y":451  */
                     { yyval = new VarDagNode((( String )(yystack.valueAt (1))), (( Path )(yystack.valueAt (0)))); };
  break;
    

  case 75:
  if (yyn == 75)
    /* "RuleParser.y":452  */
                  { yyval = new VarDagNode((( String )(yystack.valueAt (0))), Bindings.RIGHTLOCAL); };
  break;
    

  case 76:
  if (yyn == 76)
    /* "RuleParser.y":453  */
                             { yyval = getNewFunCallDagNode((( String )(yystack.valueAt (3))), (( List )(yystack.valueAt (1))), yystack.locationAt (3));
                               if (yyval == null) return YYERROR ;
                             };
  break;
    

  case 77:
  if (yyn == 77)
    /* "RuleParser.y":456  */
                             { yyval = getNewFunCallDagNode((( String )(yystack.valueAt (2))), null, yystack.locationAt (2));
                               if (yyval == null) return YYERROR ;
                             };
  break;
    


/* "RuleParser.java":959  */

        default: break;
      }

    yystack.pop (yylen);
    yylen = 0;

    /* Shift the result of the reduction.  */
    int yystate = yyLRGotoState (yystack.stateAt (0), yyr1_[yyn]);
    yystack.push (yystate, yyval, yyloc);
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




  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse () throws java.io.IOException

  {
    /* @$.  */
    Location yyloc;


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
    /* The location where the error started.  */
    Location yyerrloc = null;

    /* Location. */
    Location yylloc = new Location (null, null);

    /* Semantic value of the lookahead.  */
    Object yylval = null;

    yyerrstatus_ = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval , yylloc);



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:

        /* Accept?  */
        if (yystate == yyfinal_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yyPactValueIsDefault (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == yyempty_)
          {

            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal ();
            yylloc = new Location (yylexer.getStartPos (),
                            yylexer.getEndPos ());

          }

        /* Convert token to internal form.  */
        yytoken = yytranslate_ (yychar);

        /* If the proper action on seeing token YYTOKEN is to reduce or to
           detect an error, take that action.  */
        yyn += yytoken;
        if (yyn < 0 || yylast_ < yyn || yycheck_[yyn] != yytoken)
          label = YYDEFAULT;

        /* <= 0 means reduce or error.  */
        else if ((yyn = yytable_[yyn]) <= 0)
          {
            if (yyTableValueIsError (yyn))
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
            /* Discard the token being shifted.  */
            yychar = yyempty_;

            /* Count tokens shifted since error; after three, turn off error
               status.  */
            if (yyerrstatus_ > 0)
              --yyerrstatus_;

            yystate = yyn;
            yystack.push (yystate, yylval, yylloc);
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
            yyerror (yylloc, yysyntax_error (yystate, yytoken));
          }

        yyerrloc = yylloc;
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
        yyerrloc = yystack.locationAt (yylen - 1);
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
            if (!yyPactValueIsDefault (yyn))
              {
                yyn += yy_error_token_;
                if (0 <= yyn && yyn <= yylast_ && yycheck_[yyn] == yy_error_token_)
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

            yyerrloc = yystack.locationAt (0);
            yystack.pop ();
            yystate = yystack.stateAt (0);
          }

        if (label == YYABORT)
            /* Leave the switch.  */
            break;


        /* Muck with the stack to setup for yylloc.  */
        yystack.push (0, null, yylloc);
        yystack.push (0, null, yyerrloc);
        yyloc = yylloc (yystack, 2);
        yystack.pop (2);

        /* Shift the error token.  */

        yystate = yyn;
        yystack.push (yyn, yylval, yyloc);
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
            if (!yyPactValueIsDefault (yyn))
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
                  if (yycheck_[x + yyn] == x && x != yy_error_token_
                      && !yyTableValueIsError (yytable_[x + yyn]))
                    ++count;
                if (count < 5)
                  {
                    count = 0;
                    for (int x = yyxbegin; x < yyxend; ++x)
                      if (yycheck_[x + yyn] == x && x != yy_error_token_
                          && !yyTableValueIsError (yytable_[x + yyn]))
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
  private static boolean yyPactValueIsDefault (int yyvalue)
  {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yyTableValueIsError (int yyvalue)
  {
    return yyvalue == yytable_ninf_;
  }

  private static final short yypact_ninf_ = -67;
  private static final short yytable_ninf_ = -5;

  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final short yypact_[] = yypact_init();
  private static final short[] yypact_init()
  {
    return new short[]
    {
      50,   -67,     4,    84,    92,   -67,    29,    93,    23,   112,
     154,   120,   -67,    15,    38,   124,   -67,   -67,    23,   -67,
     -67,   -67,   -67,   -67,   -67,   107,    42,    89,    -4,   108,
      23,    23,   -67,   -67,   -67,     4,    84,   -67,   -67,   -67,
       5,    79,   112,   112,   -67,   112,    70,   112,   -67,   112,
     128,   126,   -67,   -67,   134,   -67,   -67,    32,   127,    47,
      50,    50,   143,   124,   -67,   -67,   139,   -67,   134,   -67,
     -67,   -67,   -67,   142,   140,   -67,    64,   109,    23,   -67,
     158,   -67,   -67,    79,     5,   131,   149,   131,    79,    79,
     -67,    76,   -67,   -67,    82,   -67,   -67,   -67,   150,   153,
     -67,   -67,   138,   131,   148,   155,   -67,   -67,   148,   147,
     164,   155,   -67,   -67,   -67,   151,   -67,   134,   -67,   156,
     157,   134,   159,    -7,   -67,   131,   -67,   -67,   160,   -67,
     -67,   131,   131,   161,   131,   -67,   -67,   -67,   -67,   -67,
     131,   -67,   -67
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
       0,     8,    34,    33,     0,    26,     0,     0,     0,     0,
       0,     0,     2,     0,     0,    14,    16,    21,    22,    25,
      30,    31,    32,    34,    33,     0,    34,     0,     0,     0,
       0,     0,    35,    24,    27,     0,     0,    29,     1,     3,
       0,     0,     0,     0,    23,    18,     0,     0,    28,     0,
      38,     0,    36,    47,    52,    48,    49,     0,    42,     0,
       0,     0,     0,    13,    15,    17,    72,    73,    52,    70,
      75,    41,    71,     0,    68,    69,     0,     0,     0,    37,
       0,    50,     5,     0,     0,     0,     0,     0,    11,    12,
       7,     0,    74,    40,     0,    19,    20,    39,     0,     0,
      43,    64,     0,     0,     0,    45,    54,    59,    60,    63,
       0,    44,     9,    10,    77,     0,    67,    52,     6,     0,
       0,    52,     0,     0,    62,     0,    61,    66,     0,    76,
      51,     0,     0,     0,     0,    65,    53,    46,    55,    56,
       0,    57,    58
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final short yypgoto_[] = yypgoto_init();
  private static final short[] yypgoto_init()
  {
    return new short[]
    {
     -67,   -67,   165,    69,    35,    -2,   135,    -5,   -67,   163,
      -6,    -1,   101,   -67,    86,   -67,   -67,   -65,   -66,   -30,
     -67,   -67,   -29,   -67,   -45
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final byte yydefgoto_[] = yydefgoto_init();
  private static final byte[] yydefgoto_init()
  {
    return new byte[]
    {
      -1,    11,    12,    13,    62,    14,    15,    16,    17,    18,
      19,    50,    51,    29,    57,    58,    59,    81,   105,   106,
     107,   108,    73,    74,   109
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final short yytable_[] = yytable_init();
  private static final short[] yytable_init()
  {
    return new short[]
    {
      25,    75,    32,    92,    34,    28,   125,    33,    42,    53,
      54,   135,    32,    55,    48,    -4,     1,    44,     2,     3,
       4,   111,     5,    20,    32,    32,    23,    24,    56,     6,
      52,     7,    23,    24,     8,     9,    10,   123,    64,    30,
      65,    82,    83,    31,    40,    76,    75,    77,    41,    75,
      42,     1,   130,     2,     3,     4,   133,     5,    46,   124,
      85,    20,   115,   126,     6,   116,     7,    86,    87,     8,
       9,    10,    32,    66,    67,    68,    42,    69,    70,    66,
      67,    68,    95,    69,    70,    66,    67,    68,    71,    69,
      70,    60,    61,    72,   114,   136,    26,     3,    27,    72,
       5,   138,   139,    21,   141,    72,    47,     6,    22,     7,
     142,    22,     8,     9,    10,     2,     3,     4,    99,     5,
      38,    42,    45,   112,   113,    49,     6,    96,     7,    88,
      89,     8,     9,    10,    66,    67,    68,    43,   101,    70,
      78,   119,   120,   121,    79,   102,   122,   103,    80,    84,
     104,    66,    67,    68,    90,    91,    70,    35,    36,     4,
      93,    98,    94,   110,   118,   117,   127,   128,   125,   129,
     100,   131,   132,    37,   134,   137,   140,    63,    39,    97
    };
  }

private static final short yycheck_[] = yycheck_init();
  private static final short[] yycheck_init()
  {
    return new short[]
    {
       6,    46,     8,    68,     9,     7,    13,     8,    12,     4,
       5,    18,    18,     8,    18,     0,     1,    18,     3,     4,
       5,    87,     7,    19,    30,    31,     3,     4,    23,    14,
      31,    16,     3,     4,    19,    20,    21,   103,    43,    16,
      45,     9,    10,    20,     6,    47,    91,    49,    10,    94,
      12,     1,   117,     3,     4,     5,   121,     7,    16,   104,
      13,    19,    91,   108,    14,    94,    16,    20,    21,    19,
      20,    21,    78,     3,     4,     5,    12,     7,     8,     3,
       4,     5,    18,     7,     8,     3,     4,     5,    18,     7,
       8,    12,    13,    23,    18,   125,     3,     4,     5,    23,
       7,   131,   132,    19,   134,    23,    17,    14,    19,    16,
     140,    19,    19,    20,    21,     3,     4,     5,    83,     7,
       0,    12,    15,    88,    89,    17,    14,    18,    16,    60,
      61,    19,    20,    21,     3,     4,     5,    13,     7,     8,
      12,     3,     4,     5,    18,    14,     8,    16,    14,    22,
      19,     3,     4,     5,    11,    16,     8,     3,     4,     5,
      18,     3,    22,    14,    11,    15,    19,     3,    13,    18,
      84,    15,    15,    10,    15,    15,    15,    42,    13,    78
    };
  }

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
  private static final byte yystos_[] = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,     1,     3,     4,     5,     7,    14,    16,    19,    20,
      21,    25,    26,    27,    29,    30,    31,    32,    33,    34,
      19,    19,    19,     3,     4,    34,     3,     5,    29,    37,
      16,    20,    34,    35,    31,     3,     4,    33,     0,    26,
       6,    10,    12,    13,    35,    15,    16,    17,    18,    17,
      35,    36,    35,     4,     5,     8,    23,    38,    39,    40,
      12,    13,    28,    30,    31,    31,     3,     4,     5,     7,
       8,    18,    23,    46,    47,    48,    29,    29,    12,    18,
      14,    41,     9,    10,    22,    13,    20,    21,    27,    27,
      11,    16,    41,    18,    22,    18,    18,    36,     3,    28,
      38,     7,    14,    16,    19,    42,    43,    44,    45,    48,
      14,    42,    28,    28,    18,    46,    46,    15,    11,     3,
       4,     5,     8,    42,    48,    13,    48,    19,     3,    18,
      41,    15,    15,    41,    15,    18,    43,    15,    43,    43,
      15,    43,    43
    };
  }

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final byte yyr1_[] = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    24,    25,    26,    26,    27,    27,    27,    27,    28,
      28,    28,    28,    29,    29,    30,    30,    31,    31,    31,
      31,    31,    32,    32,    32,    32,    32,    32,    32,    32,
      33,    33,    33,    34,    34,    35,    35,    35,    36,    36,
      37,    37,    38,    38,    39,    39,    39,    40,    40,    40,
      40,    41,    41,    42,    42,    43,    43,    43,    43,    43,
      44,    44,    44,    44,    44,    44,    45,    46,    46,    47,
      47,    47,    48,    48,    48,    48,    48,    48
    };
  }

/* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
  private static final byte yyr2_[] = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     2,     1,     4,     6,     4,     1,     3,
       3,     2,     2,     3,     1,     3,     1,     4,     3,     5,
       5,     1,     1,     2,     2,     1,     1,     2,     3,     2,
       2,     2,     2,     1,     1,     1,     2,     3,     1,     3,
       4,     3,     1,     3,     3,     3,     5,     1,     1,     1,
       2,     4,     0,     3,     1,     4,     4,     4,     5,     1,
       1,     2,     2,     1,     1,     3,     2,     3,     1,     1,
       1,     1,     1,     1,     2,     1,     4,     3
    };
  }


  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  private static final String yytname_[] = yytname_init();
  private static final String[] yytname_init()
  {
    return new String[]
    {
  "$end", "error", "$undefined", "ID", "VAR", "GVAR", "ARROW", "STRING",
  "RVAR", "'.'", "'{'", "'}'", "'|'", "'^'", "'<'", "'>'", "'('", "'~'",
  "')'", "':'", "'!'", "'='", "','", "'#'", "$accept", "start", "rules",
  "group", "groups", "expr", "eterm", "term", "feature", "nominal",
  "id_lvar", "iv_term", "iv_expr", "funcall", "actions", "action", "lval",
  "path", "rexpr", "rterm", "rfeat", "rnominal", "rargs", "rarg",
  "r_id_var", null
    };
  }



  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final byte yytranslate_ (int t)
  {
    int user_token_number_max_ = 265;
    byte undef_token_ = 2;

    if (t <= 0)
      return Lexer.EOF;
    else if (t <= user_token_number_max_)
      return yytranslate_table_[t];
    else
      return undef_token_;
  }
  private static final byte yytranslate_table_[] = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    20,     2,    23,     2,     2,     2,     2,
      16,    18,     2,     2,    22,     2,     9,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    19,     2,
      14,    21,    15,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,    13,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    10,    12,    11,    17,     2,     2,     2,
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
       5,     2,     6,     7,     8,     2
    };
  }


  private static final byte yy_error_token_ = 1;

  private static final int yylast_ = 179;
  private static final int yynnts_ = 25;
  private static final int yyempty_ = -2;
  private static final int yyfinal_ = 38;
  private static final int yyntokens_ = 24;

/* User implementation code.  */
/* Unqualified %code blocks.  */
/* "RuleParser.y":31  */

  /** A class to collect matches and actions to generate real rules when
   *  done.
   */
  private class RuleProto {
      /** The left hand side of a rule */
    private Match _match;

    /** The right hand side of the rule */
    private LinkedList<Action> _replace;

    /** Where was this rule defined */
    private Location _position;

    /** Is this a recurring or single application rule */
    private boolean _oneShot;

    public RuleProto(Match m, LinkedList<Action> actions, Location loc,
                     boolean oneShot) {
      _match = m;
      _replace = actions;
      _position = loc;
      _oneShot = oneShot;
    }

    public void addActionsToFront(RuleProto r) {
      _replace.addAll(0, r._replace);
    }

    public void setMatch(Match m) { _match = m; }

    public Match getMatch() { return _match ; }

    public LinkedList<RuleProto> applyToAll(List<Op> ops) {
      LinkedList<RuleProto> result = new LinkedList<RuleProto>();
      for (Op op : ops) {
        result.addAll(op.apply(this));
      }
      return result;
    }

    public Rule toRule() {
      return new BasicRule(new VarMatch(null, _match), _replace, _position,
                           _oneShot);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      return appendActions(_replace, appendMatches(_match, sb)
                           .append(_oneShot ? " -> " : " => "))
          .append(" .").toString();
    }

  }


  /** Class to account for in-group initial matches */
  private class Op {
    private char _op;
    private LinkedList<RuleProto> _opRules;

    Op(char op, LinkedList<RuleProto> rules) {
      _op = op;
      _opRules = rules;
    }

    /** r is an outer rule specification, while this represents a list of
     *  matches and actions embedded in a group.
     *
     *  Thus, create a "new" list by prepending the matches and actions of r
     *  to all members of _opRules.
     */
    public LinkedList<RuleProto> apply(RuleProto r) {
      for (RuleProto rule : _opRules) {
        Match arg1 = r.getMatch().deepCopy();
        Match arg2 = rule.getMatch();
        rule.setMatch((_op == '^')
                      ? new Conjunction(arg1, arg2)
                      : new Disjunction(arg1, arg2));
        rule.addActionsToFront(r);
      }
      return _opRules;
    }
  }


  private List _rules;

  public List<Rule> getRules() {
    List<Rule> result = new java.util.ArrayList<Rule>(_rules.size());
    for (RuleProto p : (List<RuleProto>)_rules) result.add(p.toRule());
    return result;
  }

  public de.dfki.lt.tr.dialogue.cplan.Lexer getLexer() {
    return (de.dfki.lt.tr.dialogue.cplan.Lexer)yylexer;
  }

  public void reset() {
    _rules = new LinkedList<RuleProto>();
  }

  public void reset(String inputDescription, Reader input) {
    reset();
    ((de.dfki.lt.tr.dialogue.cplan.Lexer)this.yylexer)
      .setInputReader(inputDescription, input);
  }

  private LinkedList<RuleProto> newRuleList(RuleProto rule) {
    LinkedList<RuleProto> l = new LinkedList<RuleProto>();
    if (rule != null) {
      l.add(rule);
    }
    return l;
  }


  private LinkedList<RuleProto> addTopVarMatches(LinkedList<RuleProto> rules) {
    for (RuleProto rule : rules) {
      rule.setMatch(new VarMatch(null, rule.getMatch()));
    }
    return rules;
  }

  private RuleProto newRule(Match match, List actions, Location loc,
                            boolean oneShot) {
    return new RuleProto(match, (LinkedList<Action>) actions, loc, oneShot);
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

  public LinkedList<Op> newOpList(char op, LinkedList<RuleProto> rules) {
    LinkedList<Op> result = new LinkedList<Op>();
    result.add(new Op(op, rules));
    return result;
  }


  /*
  private class Op {

    private Op _rest;
    private Match _m;
    private List<RuleProto> _rules;

    char _op;

    List<RuleProto> apply(Match m);

    private Op(char op, Match m, List<RuleProto> l, Op rest) {
      _op = op
      _rules = l;
      _m = m;
      _rest = rest;
    }

    public Op(char op, List<RuleProto> l, Op rest) {
      this(op, null, l, rest);
    }

    public Op(char op, Match m, Op rest) {
      this(op, m, null, rest);
    }

    public Op(char op, Match m) {
      this(op, m, null, null );
    }

    public Op(char op, List<RuleProto> l) {
      this(op, null, l, null);
    }

    public List<RuleProto> apply(Match m) {
      if (
    }
  }
  */

/* "RuleParser.java":1821  */

}

