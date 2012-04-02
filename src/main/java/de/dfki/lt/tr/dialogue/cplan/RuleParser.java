
/* A Bison parser, made by GNU Bison 2.4.1.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java
   
      Copyright (C) 2007, 2008 Free Software Foundation, Inc.
   
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

/* "%code imports" blocks.  */

/* Line 33 of lalr1.java  */
/* Line 3 of "RuleParser.y"  */

import java.io.Reader;
import java.util.List;
import java.util.LinkedList;

import de.dfki.lt.tr.dialogue.cplan.Path;
import de.dfki.lt.tr.dialogue.cplan.util.Position;
import de.dfki.lt.tr.dialogue.cplan.matches.*;
import de.dfki.lt.tr.dialogue.cplan.actions.*;

@SuppressWarnings({"rawtypes", "unchecked", "fallthrough", "unused"})



/* Line 33 of lalr1.java  */
/* Line 57 of "RuleParser.java"  */

/**
 * A Bison parser, automatically generated from <tt>RuleParser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class RuleParser
{
    /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "2.4.1";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";


  /** True if verbose error messages are enabled.  */
  public boolean errorVerbose = false;


  /**
   * A class defining a pair of positions.  Positions, defined by the
   * <code>Position</code> class, denote a point in the input.
   * Locations represent a part of the input through the beginning
   * and ending positions.  */
  public class Location {
    /** The first, inclusive, position in the range.  */
    public Position begin;

    /** The first position beyond the range.  */
    public Position end;

    /**
     * Create a <code>Location</code> denoting an empty range located at
     * a given point.
     * @param loc The position at which the range is anchored.  */
    public Location (Position loc) {
      this.begin = this.end = loc;
    }

    /**
     * Create a <code>Location</code> from the endpoints of the range.
     * @param begin The first position included in the range.
     * @param end   The first position beyond the range.  */
    public Location (Position begin, Position end) {
      this.begin = begin;
      this.end = end;
    }

    /**
     * Print a representation of the location.  For this to be correct,
     * <code>Position</code> should override the <code>equals</code>
     * method.  */
    public String toString () {
      if (begin.equals (end))
        return begin.toString ();
      else
        return begin.toString () + "-" + end.toString ();
    }
  }



  /** Token returned by the scanner to signal the end of its input.  */
  public static final int EOF = 0;

/* Tokens.  */
  /** Token number, to be returned by the scanner.  */
  public static final int ID = 258;
  /** Token number, to be returned by the scanner.  */
  public static final int VAR = 259;
  /** Token number, to be returned by the scanner.  */
  public static final int GVAR = 260;
  /** Token number, to be returned by the scanner.  */
  public static final int ARROW = 262;
  /** Token number, to be returned by the scanner.  */
  public static final int STRING = 263;
  /** Token number, to be returned by the scanner.  */
  public static final int RVAR = 264;



  
  private Location yylloc (YYStack rhs, int n)
  {
    if (n > 0)
      return new Location (rhs.locationAt (1).begin, rhs.locationAt (n).end);
    else
      return new Location (rhs.locationAt (0).end);
  }

  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>RuleParser</tt>.
   */
  public interface Lexer {
    /**
     * Method to retrieve the beginning position of the last scanned token.
     * @return the position at which the last scanned token starts.  */
    Position getStartPos ();

    /**
     * Method to retrieve the ending position of the last scanned token.
     * @return the first position beyond the last scanned token.  */
    Position getEndPos ();

    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.  */
    Object getLVal ();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * and beginning/ending positions of the token. 
     * @return the token identifier corresponding to the next token. */
    int yylex () throws java.io.IOException;

    /**
     * Entry point for error reporting.  Emits an error
     * referring to the given location in a user-defined way.
     *
     * @param loc The location of the element to which the
     *                error message is related
     * @param s The string for the error message.  */
     void yyerror (Location loc, String s);
  }

  /** The object doing lexical analysis for us.  */
  private Lexer yylexer;
  
  



  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public RuleParser (Lexer yylexer) {
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

  private final int yylex () throws java.io.IOException {
    return yylexer.yylex ();
  }
  protected final void yyerror (Location loc, String s) {
    yylexer.yyerror (loc, s);
  }

  
  protected final void yyerror (String s) {
    yylexer.yyerror ((Location)null, s);
  }
  protected final void yyerror (Position loc, String s) {
    yylexer.yyerror (new Location (loc), s);
  }

  protected final void yycdebug (String s) {
    if (yydebug > 0)
      yyDebugStream.println (s);
  }

  private final class YYStack {
    private int[] stateStack = new int[16];
    private Location[] locStack = new Location[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;
    
    public final void push (int state, Object value    	   	      	    , Location loc) {
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
      height--;
    }

    public final void pop (int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (num > 0) {
	java.util.Arrays.fill (valueStack, height - num + 1, height, null);
        java.util.Arrays.fill (locStack, height - num + 1, height, null);
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
    public void print (java.io.PrintStream out)
    {
      out.print ("Stack now");
      
      for (int i = 0; i < height; i++)
        {
	  out.print (' ');
	  out.print (stateStack[i]);
        }
      out.println ();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).  */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).  */
  public static final int YYABORT = 1;

  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.  */
  public static final int YYERROR = 2;

  /**
   * Returned by a Bison action in order to print an error message and start
   * error recovery.  */
  public static final int YYFAIL = 3;

  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;

  private int yyerrstatus_ = 0;

  /**
   * Return whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.  */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  private int yyaction (int yyn, YYStack yystack, int yylen) 
  {
    Object yyval;
    Location yyloc = yylloc (yystack, yylen);

    /* If YYLEN is nonzero, implement the default value of the action:
       `$$ = $1'.  Otherwise, use the top of the stack.
    
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
    
/* Line 353 of lalr1.java  */
/* Line 207 of "RuleParser.y"  */
    { _rules = (( LinkedList )(yystack.valueAt (1-(1)))); };
  break;
    

  case 3:
  if (yyn == 3)
    
/* Line 353 of lalr1.java  */
/* Line 209 of "RuleParser.y"  */
    { _rules = (( LinkedList )(yystack.valueAt (2-(1)))); _rules.addAll((( LinkedList )(yystack.valueAt (2-(2))))); yyval = _rules; };
  break;
    

  case 4:
  if (yyn == 4)
    
/* Line 353 of lalr1.java  */
/* Line 210 of "RuleParser.y"  */
    { yyval = (( LinkedList )(yystack.valueAt (1-(1)))); };
  break;
    

  case 5:
  if (yyn == 5)
    
/* Line 353 of lalr1.java  */
/* Line 213 of "RuleParser.y"  */
    { yyval = newRuleList((( BasicRule )(yystack.valueAt (2-(1))))); };
  break;
    

  case 6:
  if (yyn == 6)
    
/* Line 353 of lalr1.java  */
/* Line 214 of "RuleParser.y"  */
    { yyval = applyToAll((( Match )(yystack.valueAt (4-(1)))), (( LinkedList )(yystack.valueAt (4-(3))))); };
  break;
    

  case 7:
  if (yyn == 7)
    
/* Line 353 of lalr1.java  */
/* Line 217 of "RuleParser.y"  */
    { (( LinkedList )(yystack.valueAt (3-(3)))).addFirst(new Op('|', (( LinkedList )(yystack.valueAt (3-(2)))))); yyval = (( LinkedList )(yystack.valueAt (3-(3)))); };
  break;
    

  case 8:
  if (yyn == 8)
    
/* Line 353 of lalr1.java  */
/* Line 218 of "RuleParser.y"  */
    { (( LinkedList )(yystack.valueAt (3-(3)))).addFirst(new Op('^', (( LinkedList )(yystack.valueAt (3-(2)))))); yyval = (( LinkedList )(yystack.valueAt (3-(3)))); };
  break;
    

  case 9:
  if (yyn == 9)
    
/* Line 353 of lalr1.java  */
/* Line 219 of "RuleParser.y"  */
    { yyval = newOpList('|', (( LinkedList )(yystack.valueAt (2-(2))))); };
  break;
    

  case 10:
  if (yyn == 10)
    
/* Line 353 of lalr1.java  */
/* Line 220 of "RuleParser.y"  */
    { yyval = newOpList('^', (( LinkedList )(yystack.valueAt (2-(2))))); };
  break;
    

  case 11:
  if (yyn == 11)
    
/* Line 353 of lalr1.java  */
/* Line 223 of "RuleParser.y"  */
    { yyval = newRuleList((( BasicRule )(yystack.valueAt (2-(1))))); };
  break;
    

  case 12:
  if (yyn == 12)
    
/* Line 353 of lalr1.java  */
/* Line 224 of "RuleParser.y"  */
    { yyval = applyToAll((( Match )(yystack.valueAt (4-(1)))), (( LinkedList )(yystack.valueAt (4-(3))))); };
  break;
    

  case 13:
  if (yyn == 13)
    
/* Line 353 of lalr1.java  */
/* Line 233 of "RuleParser.y"  */
    { yyval = newRule(new VarMatch(null, (( Match )(yystack.valueAt (3-(1))))), (( List )(yystack.valueAt (3-(3)))), yystack.locationAt (3-(2))); };
  break;
    

  case 14:
  if (yyn == 14)
    
/* Line 353 of lalr1.java  */
/* Line 234 of "RuleParser.y"  */
    { yyval = null; };
  break;
    

  case 15:
  if (yyn == 15)
    
/* Line 353 of lalr1.java  */
/* Line 237 of "RuleParser.y"  */
    { yyval = new Disjunction((( Match )(yystack.valueAt (3-(1)))), (( Match )(yystack.valueAt (3-(3))))); };
  break;
    

  case 17:
  if (yyn == 17)
    
/* Line 353 of lalr1.java  */
/* Line 241 of "RuleParser.y"  */
    { yyval = new Conjunction((( Match )(yystack.valueAt (3-(1)))), (( Match )(yystack.valueAt (3-(3))))); };
  break;
    

  case 19:
  if (yyn == 19)
    
/* Line 353 of lalr1.java  */
/* Line 246 of "RuleParser.y"  */
    { yyval = new FeatVal((( Match )(yystack.valueAt (4-(2)))), (( Match )(yystack.valueAt (4-(4))))); };
  break;
    

  case 20:
  if (yyn == 20)
    
/* Line 353 of lalr1.java  */
/* Line 247 of "RuleParser.y"  */
    { yyval = new FeatVal((( Match )(yystack.valueAt (3-(2)))), null); };
  break;
    

  case 21:
  if (yyn == 21)
    
/* Line 353 of lalr1.java  */
/* Line 248 of "RuleParser.y"  */
    { yyval = new VarMatch(new GlobalVar((( String )(yystack.valueAt (5-(2))))), (( Match )(yystack.valueAt (5-(4))))); };
  break;
    

  case 22:
  if (yyn == 22)
    
/* Line 353 of lalr1.java  */
/* Line 249 of "RuleParser.y"  */
    { yyval = new VarMatch((( MatchLVal )(yystack.valueAt (5-(2)))), (( Match )(yystack.valueAt (5-(4))))); };
  break;
    

  case 23:
  if (yyn == 23)
    
/* Line 353 of lalr1.java  */
/* Line 250 of "RuleParser.y"  */
    { yyval = (( Match )(yystack.valueAt (1-(1)))); };
  break;
    

  case 24:
  if (yyn == 24)
    
/* Line 353 of lalr1.java  */
/* Line 253 of "RuleParser.y"  */
    { yyval = (( Match )(yystack.valueAt (1-(1)))); };
  break;
    

  case 25:
  if (yyn == 25)
    
/* Line 353 of lalr1.java  */
/* Line 254 of "RuleParser.y"  */
    { yyval = new Conjunction((( Match )(yystack.valueAt (2-(1)))),
                                   new FeatVal(DagNode.TYPE_FEAT_ID, (( Match )(yystack.valueAt (2-(2)))))); };
  break;
    

  case 26:
  if (yyn == 26)
    
/* Line 353 of lalr1.java  */
/* Line 256 of "RuleParser.y"  */
    { yyval = new FeatVal(DagNode.TYPE_FEAT_ID, (( Match )(yystack.valueAt (2-(2))))); };
  break;
    

  case 27:
  if (yyn == 27)
    
/* Line 353 of lalr1.java  */
/* Line 257 of "RuleParser.y"  */
    { yyval = new FeatVal(DagNode.PROP_FEAT_ID, (( Match )(yystack.valueAt (1-(1))))); };
  break;
    

  case 28:
  if (yyn == 28)
    
/* Line 353 of lalr1.java  */
/* Line 258 of "RuleParser.y"  */
    { yyval = new FeatVal(DagNode.PROP_FEAT_ID,
                                             new Atom((( String )(yystack.valueAt (1-(1)))))); };
  break;
    

  case 29:
  if (yyn == 29)
    
/* Line 353 of lalr1.java  */
/* Line 260 of "RuleParser.y"  */
    { (( Match )(yystack.valueAt (2-(2)))).setNegated(true); yyval = (( Match )(yystack.valueAt (2-(2)))); };
  break;
    

  case 30:
  if (yyn == 30)
    
/* Line 353 of lalr1.java  */
/* Line 261 of "RuleParser.y"  */
    { yyval = (( Match )(yystack.valueAt (3-(2)))); };
  break;
    

  case 31:
  if (yyn == 31)
    
/* Line 353 of lalr1.java  */
/* Line 262 of "RuleParser.y"  */
    { yyval = new Coref((( Match )(yystack.valueAt (2-(2))))); };
  break;
    

  case 32:
  if (yyn == 32)
    
/* Line 353 of lalr1.java  */
/* Line 269 of "RuleParser.y"  */
    { yyval = new FeatVal(DagNode.ID_FEAT_ID, new Atom((( String )(yystack.valueAt (2-(1)))))); };
  break;
    

  case 33:
  if (yyn == 33)
    
/* Line 353 of lalr1.java  */
/* Line 270 of "RuleParser.y"  */
    { yyval = new LocalVar((( String )(yystack.valueAt (2-(1)))));  };
  break;
    

  case 34:
  if (yyn == 34)
    
/* Line 353 of lalr1.java  */
/* Line 271 of "RuleParser.y"  */
    { yyval = new GlobalVar((( String )(yystack.valueAt (2-(1))))); };
  break;
    

  case 35:
  if (yyn == 35)
    
/* Line 353 of lalr1.java  */
/* Line 274 of "RuleParser.y"  */
    { yyval = new LocalVar((( String )(yystack.valueAt (1-(1))))); };
  break;
    

  case 36:
  if (yyn == 36)
    
/* Line 353 of lalr1.java  */
/* Line 275 of "RuleParser.y"  */
    { yyval = new Atom((( String )(yystack.valueAt (1-(1))))); };
  break;
    

  case 37:
  if (yyn == 37)
    
/* Line 353 of lalr1.java  */
/* Line 277 of "RuleParser.y"  */
    { yyval = (( Match )(yystack.valueAt (1-(1)))); };
  break;
    

  case 38:
  if (yyn == 38)
    
/* Line 353 of lalr1.java  */
/* Line 278 of "RuleParser.y"  */
    { (( Match )(yystack.valueAt (2-(2)))).setNegated(true); yyval = (( Match )(yystack.valueAt (2-(2)))); };
  break;
    

  case 39:
  if (yyn == 39)
    
/* Line 353 of lalr1.java  */
/* Line 279 of "RuleParser.y"  */
    { yyval = (( Match )(yystack.valueAt (3-(2)))); };
  break;
    

  case 40:
  if (yyn == 40)
    
/* Line 353 of lalr1.java  */
/* Line 282 of "RuleParser.y"  */
    { yyval = (( Match )(yystack.valueAt (1-(1)))); };
  break;
    

  case 41:
  if (yyn == 41)
    
/* Line 353 of lalr1.java  */
/* Line 283 of "RuleParser.y"  */
    { yyval = new Disjunction((( Match )(yystack.valueAt (3-(1)))), (( Match )(yystack.valueAt (3-(3))))); };
  break;
    

  case 42:
  if (yyn == 42)
    
/* Line 353 of lalr1.java  */
/* Line 286 of "RuleParser.y"  */
    { yyval = getNewFunCall((( String )(yystack.valueAt (4-(1)))), (( List )(yystack.valueAt (4-(3)))), yystack.locationAt (4-(1)));
                             if (yyval == null) return YYERROR ;
                           };
  break;
    

  case 43:
  if (yyn == 43)
    
/* Line 353 of lalr1.java  */
/* Line 289 of "RuleParser.y"  */
    { yyval = getNewFunCall((( String )(yystack.valueAt (3-(1)))), null, yystack.locationAt (3-(1)));
                             if (yyval == null) return YYERROR ;
                           };
  break;
    

  case 44:
  if (yyn == 44)
    
/* Line 353 of lalr1.java  */
/* Line 312 of "RuleParser.y"  */
    {
            List<Action> result  = new LinkedList<Action>();
            result.add((( Action )(yystack.valueAt (1-(1)))));
            yyval = result;
          };
  break;
    

  case 45:
  if (yyn == 45)
    
/* Line 353 of lalr1.java  */
/* Line 317 of "RuleParser.y"  */
    { (( List )(yystack.valueAt (3-(3)))).add(0, (( Action )(yystack.valueAt (3-(1))))); yyval = (( List )(yystack.valueAt (3-(3)))); };
  break;
    

  case 46:
  if (yyn == 46)
    
/* Line 353 of lalr1.java  */
/* Line 321 of "RuleParser.y"  */
    {
         DagNode rval = (((( DagNode )(yystack.valueAt (3-(3)))) == null) ? null : (( DagNode )(yystack.valueAt (3-(3)))).copyAndInvalidate());
         yyval = new Assignment((( VarDagNode )(yystack.valueAt (3-(1)))), rval);
       };
  break;
    

  case 47:
  if (yyn == 47)
    
/* Line 353 of lalr1.java  */
/* Line 326 of "RuleParser.y"  */
    {
         DagNode rval = (((( DagNode )(yystack.valueAt (3-(3)))) == null) ? null : (( DagNode )(yystack.valueAt (3-(3)))).copyAndInvalidate());
         yyval = new Addition((( VarDagNode )(yystack.valueAt (3-(1)))), rval);
       };
  break;
    

  case 48:
  if (yyn == 48)
    
/* Line 353 of lalr1.java  */
/* Line 333 of "RuleParser.y"  */
    { yyval = new Deletion((( VarDagNode )(yystack.valueAt (5-(1)))), new DagNode((( String )(yystack.valueAt (5-(4)))), new DagNode())); };
  break;
    

  case 49:
  if (yyn == 49)
    
/* Line 353 of lalr1.java  */
/* Line 336 of "RuleParser.y"  */
    { yyval = new VarDagNode((( String )(yystack.valueAt (1-(1)))), Bindings.LOCAL); };
  break;
    

  case 50:
  if (yyn == 50)
    
/* Line 353 of lalr1.java  */
/* Line 337 of "RuleParser.y"  */
    { yyval = new VarDagNode((( String )(yystack.valueAt (1-(1)))), Bindings.RIGHTLOCAL); };
  break;
    

  case 51:
  if (yyn == 51)
    
/* Line 353 of lalr1.java  */
/* Line 338 of "RuleParser.y"  */
    { yyval = new VarDagNode("#", Bindings.LOCAL); };
  break;
    

  case 52:
  if (yyn == 52)
    
/* Line 353 of lalr1.java  */
/* Line 339 of "RuleParser.y"  */
    { yyval = new VarDagNode((( String )(yystack.valueAt (2-(1)))), (( Path )(yystack.valueAt (2-(2))))); };
  break;
    

  case 53:
  if (yyn == 53)
    
/* Line 353 of lalr1.java  */
/* Line 343 of "RuleParser.y"  */
    { yyval = (( Path )(yystack.valueAt (4-(4)))).addToFront((( String )(yystack.valueAt (4-(2))))); };
  break;
    

  case 54:
  if (yyn == 54)
    
/* Line 353 of lalr1.java  */
/* Line 344 of "RuleParser.y"  */
    { yyval = new Path(); };
  break;
    

  case 55:
  if (yyn == 55)
    
/* Line 353 of lalr1.java  */
/* Line 347 of "RuleParser.y"  */
    { (( DagNode )(yystack.valueAt (3-(1)))).add((( DagNode )(yystack.valueAt (3-(3))))); (( DagNode )(yystack.valueAt (3-(1)))).setNominal(); yyval = (( DagNode )(yystack.valueAt (3-(1)))); };
  break;
    

  case 56:
  if (yyn == 56)
    
/* Line 353 of lalr1.java  */
/* Line 348 of "RuleParser.y"  */
    { yyval = (( DagNode )(yystack.valueAt (1-(1)))); };
  break;
    

  case 57:
  if (yyn == 57)
    
/* Line 353 of lalr1.java  */
/* Line 351 of "RuleParser.y"  */
    { yyval = new DagNode((( String )(yystack.valueAt (4-(2)))), (( DagNode )(yystack.valueAt (4-(4))))).setNominal(); };
  break;
    

  case 58:
  if (yyn == 58)
    
/* Line 353 of lalr1.java  */
/* Line 352 of "RuleParser.y"  */
    { yyval = (( DagNode )(yystack.valueAt (1-(1)))); };
  break;
    

  case 59:
  if (yyn == 59)
    
/* Line 353 of lalr1.java  */
/* Line 356 of "RuleParser.y"  */
    { yyval = (( DagNode )(yystack.valueAt (1-(1)))); };
  break;
    

  case 60:
  if (yyn == 60)
    
/* Line 353 of lalr1.java  */
/* Line 357 of "RuleParser.y"  */
    { (( DagNode )(yystack.valueAt (2-(1)))).add(new DagNode(DagNode.TYPE_FEAT_ID, (( DagNode )(yystack.valueAt (2-(2))))));
                            yyval = (( DagNode )(yystack.valueAt (2-(1)))); };
  break;
    

  case 61:
  if (yyn == 61)
    
/* Line 353 of lalr1.java  */
/* Line 359 of "RuleParser.y"  */
    { yyval = new DagNode(DagNode.TYPE_FEAT_ID, (( DagNode )(yystack.valueAt (2-(2)))))
                                    .setNominal();
                          };
  break;
    

  case 62:
  if (yyn == 62)
    
/* Line 353 of lalr1.java  */
/* Line 362 of "RuleParser.y"  */
    { yyval = new DagNode(DagNode.PROP_FEAT_ID, (( DagNode )(yystack.valueAt (1-(1))))); };
  break;
    

  case 63:
  if (yyn == 63)
    
/* Line 353 of lalr1.java  */
/* Line 363 of "RuleParser.y"  */
    { yyval = new DagNode(DagNode.PROP_FEAT_ID,
                                             new DagNode((( String )(yystack.valueAt (1-(1)))))); };
  break;
    

  case 64:
  if (yyn == 64)
    
/* Line 353 of lalr1.java  */
/* Line 365 of "RuleParser.y"  */
    { yyval = (( DagNode )(yystack.valueAt (3-(2)))).setNominal(); };
  break;
    

  case 65:
  if (yyn == 65)
    
/* Line 353 of lalr1.java  */
/* Line 374 of "RuleParser.y"  */
    {
             yyval = new DagNode(DagNode.ID_FEAT_ID, (( DagNode )(yystack.valueAt (2-(1))))).setNominal();
         };
  break;
    

  case 66:
  if (yyn == 66)
    
/* Line 353 of lalr1.java  */
/* Line 379 of "RuleParser.y"  */
    { (( List )(yystack.valueAt (3-(3)))).add(0, (( DagNode )(yystack.valueAt (3-(1))))); yyval = (( List )(yystack.valueAt (3-(3)))); };
  break;
    

  case 67:
  if (yyn == 67)
    
/* Line 353 of lalr1.java  */
/* Line 380 of "RuleParser.y"  */
    { List<DagNode> result = new LinkedList<DagNode>();
                           result.add((( DagNode )(yystack.valueAt (1-(1)))));
                           yyval = result;
                         };
  break;
    

  case 68:
  if (yyn == 68)
    
/* Line 353 of lalr1.java  */
/* Line 386 of "RuleParser.y"  */
    { yyval = (( DagNode )(yystack.valueAt (1-(1)))); };
  break;
    

  case 69:
  if (yyn == 69)
    
/* Line 353 of lalr1.java  */
/* Line 387 of "RuleParser.y"  */
    { yyval = new DagNode((( String )(yystack.valueAt (1-(1))))); };
  break;
    

  case 70:
  if (yyn == 70)
    
/* Line 353 of lalr1.java  */
/* Line 388 of "RuleParser.y"  */
    { yyval = new VarDagNode("#", Bindings.LOCAL); };
  break;
    

  case 71:
  if (yyn == 71)
    
/* Line 353 of lalr1.java  */
/* Line 391 of "RuleParser.y"  */
    { yyval = new DagNode((( String )(yystack.valueAt (1-(1))))); };
  break;
    

  case 72:
  if (yyn == 72)
    
/* Line 353 of lalr1.java  */
/* Line 392 of "RuleParser.y"  */
    { yyval = new VarDagNode((( String )(yystack.valueAt (1-(1)))), Bindings.LOCAL); };
  break;
    

  case 73:
  if (yyn == 73)
    
/* Line 353 of lalr1.java  */
/* Line 393 of "RuleParser.y"  */
    { yyval = new VarDagNode((( String )(yystack.valueAt (2-(1)))), (( Path )(yystack.valueAt (2-(2))))); };
  break;
    

  case 74:
  if (yyn == 74)
    
/* Line 353 of lalr1.java  */
/* Line 394 of "RuleParser.y"  */
    { yyval = new VarDagNode((( String )(yystack.valueAt (1-(1)))), Bindings.RIGHTLOCAL); };
  break;
    

  case 75:
  if (yyn == 75)
    
/* Line 353 of lalr1.java  */
/* Line 395 of "RuleParser.y"  */
    { yyval = getNewFunCallDagNode((( String )(yystack.valueAt (4-(1)))), (( List )(yystack.valueAt (4-(3)))), yystack.locationAt (4-(1)));
                               if (yyval == null) return YYERROR ;
                             };
  break;
    

  case 76:
  if (yyn == 76)
    
/* Line 353 of lalr1.java  */
/* Line 398 of "RuleParser.y"  */
    { yyval = getNewFunCallDagNode((( String )(yystack.valueAt (3-(1)))), null, yystack.locationAt (3-(1)));
                               if (yyval == null) return YYERROR ;
                             };
  break;
    



/* Line 353 of lalr1.java  */
/* Line 1067 of "RuleParser.java"  */
	default: break;
      }

    yy_symbol_print ("-> $$ =", yyr1_[yyn], yyval, yyloc);

    yystack.pop (yylen);
    yylen = 0;

    /* Shift the result of the reduction.  */
    yyn = yyr1_[yyn];
    int yystate = yypgoto_[yyn - yyntokens_] + yystack.stateAt (0);
    if (0 <= yystate && yystate <= yylast_
	&& yycheck_[yystate] == yystack.stateAt (0))
      yystate = yytable_[yystate];
    else
      yystate = yydefgoto_[yyn - yyntokens_];

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

  /*--------------------------------.
  | Print this symbol on YYOUTPUT.  |
  `--------------------------------*/

  private void yy_symbol_print (String s, int yytype,
			         Object yyvaluep				 , Object yylocationp)
  {
    if (yydebug > 0)
    yycdebug (s + (yytype < yyntokens_ ? " token " : " nterm ")
	      + yytname_[yytype] + " ("
	      + yylocationp + ": "
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
    /// Lookahead and lookahead in internal form.
    int yychar = yyempty_;
    int yytoken = 0;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;

    YYStack yystack = new YYStack ();

    /* Error handling.  */
    int yynerrs_ = 0;
    /// The location where the error started.
    Location yyerrloc = null;

    /// Location of the lookahead.
    Location yylloc = new Location (null, null);

    /// @$.
    Location yyloc;

    /// Semantic value of the lookahead.
    Object yylval = null;

    int yyresult;

    yycdebug ("Starting parse\n");
    yyerrstatus_ = 0;


    /* Initialize the stack.  */
    yystack.push (yystate, yylval, yylloc);

    int label = YYNEWSTATE;
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
        if (yyn == yypact_ninf_)
          {
            label = YYDEFAULT;
	    break;
          }
    
        /* Read a lookahead token.  */
        if (yychar == yyempty_)
          {
	    yycdebug ("Reading a token: ");
	    yychar = yylex ();
            
	    yylloc = new Location(yylexer.getStartPos (),
	    		   	            yylexer.getEndPos ());
            yylval = yylexer.getLVal ();
          }
    
        /* Convert token to internal form.  */
        if (yychar <= EOF)
          {
	    yychar = yytoken = EOF;
	    yycdebug ("Now at end of input.\n");
          }
        else
          {
	    yytoken = yytranslate_ (yychar);
	    yy_symbol_print ("Next token is", yytoken,
	    		     yylval, yylloc);
          }
    
        /* If the proper action on seeing token YYTOKEN is to reduce or to
           detect an error, take that action.  */
        yyn += yytoken;
        if (yyn < 0 || yylast_ < yyn || yycheck_[yyn] != yytoken)
          label = YYDEFAULT;
    
        /* <= 0 means reduce or error.  */
        else if ((yyn = yytable_[yyn]) <= 0)
          {
	    if (yyn == 0 || yyn == yytable_ninf_)
	      label = YYFAIL;
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
	    		     yylval, yylloc);
    
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
          label = YYFAIL;
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
      case YYFAIL:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
	    ++yynerrs_;
	    yyerror (yylloc, yysyntax_error (yystate, yytoken));
          }
    
        yyerrloc = yylloc;
        if (yyerrstatus_ == 3)
          {
	    /* If just tried and failed to reuse lookahead token after an
	     error, discard it.  */
    
	    if (yychar <= EOF)
	      {
	      /* Return failure if at end of input.  */
	      if (yychar == EOF)
	        return false;
	      }
	    else
	      yychar = yyempty_;
          }
    
        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;
    
      /*---------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `---------------------------------------------------*/
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
        yyerrstatus_ = 3;	/* Each real token shifted decrements this.  */
    
        for (;;)
          {
	    yyn = yypact_[yystate];
	    if (yyn != yypact_ninf_)
	      {
	        yyn += yyterror_;
	        if (0 <= yyn && yyn <= yylast_ && yycheck_[yyn] == yyterror_)
	          {
	            yyn = yytable_[yyn];
	            if (0 < yyn)
		      break;
	          }
	      }
    
	    /* Pop the current state because it cannot handle the error token.  */
	    if (yystack.height == 1)
	      return false;
    
	    yyerrloc = yystack.locationAt (0);
	    yystack.pop ();
	    yystate = yystack.stateAt (0);
	    if (yydebug > 0)
	      yystack.print (yyDebugStream);
          }
    
	
	/* Muck with the stack to setup for yylloc.  */
	yystack.push (0, null, yylloc);
	yystack.push (0, null, yyerrloc);
        yyloc = yylloc (yystack, 2);
	yystack.pop (2);

        /* Shift the error token.  */
        yy_symbol_print ("Shifting", yystos_[yyn],
			 yylval, yyloc);
    
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
    if (errorVerbose)
      {
        int yyn = yypact_[yystate];
        if (yypact_ninf_ < yyn && yyn <= yylast_)
          {
	    StringBuffer res;

	    /* Start YYX at -YYN if negative to avoid negative indexes in
	       YYCHECK.  */
	    int yyxbegin = yyn < 0 ? -yyn : 0;

	    /* Stay within bounds of both yycheck and yytname.  */
	    int yychecklim = yylast_ - yyn + 1;
	    int yyxend = yychecklim < yyntokens_ ? yychecklim : yyntokens_;
	    int count = 0;
	    for (int x = yyxbegin; x < yyxend; ++x)
	      if (yycheck_[x + yyn] == x && x != yyterror_)
	        ++count;

	    // FIXME: This method of building the message is not compatible
	    // with internationalization.
	    res = new StringBuffer ("syntax error, unexpected ");
	    res.append (yytnamerr_ (yytname_[tok]));
	    if (count < 5)
	      {
	        count = 0;
	        for (int x = yyxbegin; x < yyxend; ++x)
	          if (yycheck_[x + yyn] == x && x != yyterror_)
		    {
		      res.append (count++ == 0 ? ", expecting " : " or ");
		      res.append (yytnamerr_ (yytname_[x]));
		    }
	      }
	    return res.toString ();
          }
      }

    return "syntax error";
  }


  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
     STATE-NUM.  */
  private static final short yypact_ninf_ = -118;
  private static final short yypact_[] =
  {
        16,  -118,     3,    15,    32,  -118,    40,   107,     8,   126,
     131,    83,  -118,    65,    69,    35,    48,  -118,  -118,     8,
    -118,  -118,  -118,  -118,  -118,  -118,    86,    -1,   100,    36,
      99,     8,     8,  -118,  -118,  -118,     3,    15,  -118,  -118,
    -118,  -118,    95,   112,   126,   126,  -118,   126,    84,   126,
    -118,   126,   110,   114,  -118,  -118,   125,  -118,  -118,  -118,
     119,    18,    16,    16,   132,    48,  -118,  -118,   128,  -118,
     125,  -118,  -118,  -118,  -118,   133,   134,  -118,    64,    78,
       8,  -118,   151,  -118,    95,   145,   141,   145,   112,   148,
      61,   112,  -118,    90,  -118,  -118,    52,  -118,  -118,  -118,
     143,  -118,  -118,   157,   145,   101,   149,  -118,  -118,   101,
     144,   162,   149,  -118,  -118,   112,  -118,  -118,   150,  -118,
     125,   152,   102,  -118,   145,  -118,  -118,   154,   155,  -118,
    -118,   145,  -118,  -118,  -118,  -118,  -118
  };

  /* YYDEFACT[S] -- default rule to reduce with in state S when YYTABLE
     doesn't specify something else to do.  Zero means the default is an
     error.  */
  private static final byte yydefact_[] =
  {
         0,    14,    36,    35,     0,    28,     0,     0,     0,     0,
       0,     0,     2,     0,     0,     0,    16,    18,    23,    24,
      27,    32,    33,    34,    36,    35,     0,    36,     0,     0,
       0,     0,     0,    37,    26,    29,     0,     0,    31,     1,
       3,     5,     0,     0,     0,     0,    25,    20,     0,     0,
      30,     0,    40,     0,    38,    49,    54,    50,    51,    13,
      44,     0,     0,     0,     0,    15,    17,    19,    71,    72,
      54,    69,    74,    43,    70,     0,    67,    68,     0,     0,
       0,    39,     0,    52,     0,     0,     0,     0,     9,     0,
       0,    10,     6,     0,    73,    42,     0,    21,    22,    41,
       0,    45,    63,     0,     0,     0,    47,    56,    58,    59,
      62,     0,    46,     7,    11,     0,     8,    76,     0,    66,
      54,     0,     0,    61,     0,    60,    65,     0,     0,    75,
      53,     0,    64,    55,    48,    12,    57
  };

  /* YYPGOTO[NTERM-NUM].  */
  private static final short yypgoto_[] =
  {
      -118,  -118,   158,  -118,   -82,   109,    75,     1,   129,    -5,
    -118,   160,    -6,    -3,    94,  -118,    91,  -118,  -118,   -67,
     -77,  -117,  -118,  -118,   -16,  -118,   -47
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  private static final byte
  yydefgoto_[] =
  {
        -1,    11,    12,    13,    64,    88,    14,    15,    16,    17,
      18,    19,    20,    52,    53,    30,    59,    60,    61,    83,
     106,   107,   108,   109,    75,    76,   110
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If zero, do what YYDEFACT says.  */
  private static final short yytable_ninf_ = -5;
  private static final short
  yytable_[] =
  {
        26,    77,    33,    94,    35,    34,   113,   133,    29,   116,
     112,    24,    25,    33,   136,    48,    46,     1,    21,     2,
       3,     4,    21,     5,    31,    33,    33,   122,    32,    54,
       6,    85,     7,   128,    22,     8,     9,    10,    86,    87,
      66,    42,    67,    24,    25,    43,    77,    44,    44,    77,
      78,    23,    79,   130,    50,    68,    69,    70,   123,    71,
      72,    45,   125,    90,    90,    -4,     1,    42,     2,     3,
       4,   115,     5,    44,    33,    74,    44,   118,    41,     6,
     119,     7,    97,    39,     8,     9,    10,    68,    69,    70,
      44,    71,    72,    68,    69,    70,    98,    71,    72,    55,
      56,    47,    73,    57,    68,    69,    70,    74,   117,    72,
      27,     3,    28,    74,     5,   124,    51,    49,    58,    23,
     132,     6,    80,     7,    62,    63,     8,     9,    10,     2,
       3,     4,    81,     5,    36,    37,     4,    89,    89,    82,
       6,    84,     7,    92,    93,     8,     9,    10,    68,    69,
      70,    95,   102,    72,   100,   111,    96,   114,   120,   103,
     121,   104,   124,   126,   105,   127,   135,   131,   129,   134,
      38,    40,    91,    65,    99,   101
  };

  /* YYCHECK.  */
  private static final short
  yycheck_[] =
  {
         6,    48,     8,    70,     9,     8,    88,   124,     7,    91,
      87,     3,     4,    19,   131,    16,    19,     1,    19,     3,
       4,     5,    19,     7,    16,    31,    32,   104,    20,    32,
      14,    13,    16,   115,    19,    19,    20,    21,    20,    21,
      45,     6,    47,     3,     4,    10,    93,    12,    12,    96,
      49,    19,    51,   120,    18,     3,     4,     5,   105,     7,
       8,    13,   109,    62,    63,     0,     1,     6,     3,     4,
       5,    10,     7,    12,    80,    23,    12,    93,     9,    14,
      96,    16,    18,     0,    19,    20,    21,     3,     4,     5,
      12,     7,     8,     3,     4,     5,    18,     7,     8,     4,
       5,    15,    18,     8,     3,     4,     5,    23,    18,     8,
       3,     4,     5,    23,     7,    13,    17,    17,    23,    19,
      18,    14,    12,    16,    12,    13,    19,    20,    21,     3,
       4,     5,    18,     7,     3,     4,     5,    62,    63,    14,
      14,    22,    16,    11,    16,    19,    20,    21,     3,     4,
       5,    18,     7,     8,     3,    14,    22,     9,    15,    14,
       3,    16,    13,    19,    19,     3,    11,    15,    18,    15,
      10,    13,    63,    44,    80,    84
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  private static final byte
  yystos_[] =
  {
         0,     1,     3,     4,     5,     7,    14,    16,    19,    20,
      21,    25,    26,    27,    30,    31,    32,    33,    34,    35,
      36,    19,    19,    19,     3,     4,    36,     3,     5,    31,
      39,    16,    20,    36,    37,    33,     3,     4,    35,     0,
      26,     9,     6,    10,    12,    13,    37,    15,    16,    17,
      18,    17,    37,    38,    37,     4,     5,     8,    23,    40,
      41,    42,    12,    13,    28,    32,    33,    33,     3,     4,
       5,     7,     8,    18,    23,    48,    49,    50,    31,    31,
      12,    18,    14,    43,    22,    13,    20,    21,    29,    30,
      31,    29,    11,    16,    43,    18,    22,    18,    18,    38,
       3,    40,     7,    14,    16,    19,    44,    45,    46,    47,
      50,    14,    44,    28,     9,    10,    28,    18,    48,    48,
      15,     3,    44,    50,    13,    50,    19,     3,    28,    18,
      43,    15,    18,    45,    15,    11,    45
  };

  /* TOKEN_NUMBER_[YYLEX-NUM] -- Internal symbol number corresponding
     to YYLEX-NUM.  */
  private static final short
  yytoken_number_[] =
  {
         0,   256,   265,   258,   259,   260,   262,   263,   264,    46,
     123,   125,   124,    94,    60,    62,    40,   126,    41,    58,
      33,    61,    44,    35
  };

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final byte
  yyr1_[] =
  {
         0,    24,    25,    26,    26,    27,    27,    28,    28,    28,
      28,    29,    29,    30,    30,    31,    31,    32,    32,    33,
      33,    33,    33,    33,    34,    34,    34,    34,    34,    34,
      34,    34,    35,    35,    35,    36,    36,    37,    37,    37,
      38,    38,    39,    39,    40,    40,    41,    41,    41,    42,
      42,    42,    42,    43,    43,    44,    44,    45,    45,    46,
      46,    46,    46,    46,    46,    47,    48,    48,    49,    49,
      49,    50,    50,    50,    50,    50,    50
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  private static final byte
  yyr2_[] =
  {
         0,     2,     1,     2,     1,     2,     4,     3,     3,     2,
       2,     2,     4,     3,     1,     3,     1,     3,     1,     4,
       3,     5,     5,     1,     1,     2,     2,     1,     1,     2,
       3,     2,     2,     2,     2,     1,     1,     1,     2,     3,
       1,     3,     4,     3,     1,     3,     3,     3,     5,     1,
       1,     1,     2,     4,     0,     3,     1,     4,     1,     1,
       2,     2,     1,     1,     3,     2,     3,     1,     1,     1,
       1,     1,     1,     2,     1,     4,     3
  };

  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  private static final String yytname_[] =
  {
    "$end", "error", "$undefined", "ID", "VAR", "GVAR", "ARROW", "STRING",
  "RVAR", "'.'", "'{'", "'}'", "'|'", "'^'", "'<'", "'>'", "'('", "'~'",
  "')'", "':'", "'!'", "'='", "','", "'#'", "$accept", "start", "rules",
  "rulegroup", "groups", "group", "rule", "expr", "eterm", "term",
  "feature", "nominal", "id_lvar", "iv_term", "iv_expr", "funcall",
  "actions", "action", "lval", "path", "rexpr", "rterm", "rfeat",
  "rnominal", "rargs", "rarg", "r_id_var", null
  };

  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  private static final byte yyrhs_[] =
  {
        25,     0,    -1,    26,    -1,    27,    26,    -1,    27,    -1,
      30,     9,    -1,    31,    10,    28,    11,    -1,    12,    29,
      28,    -1,    13,    29,    28,    -1,    12,    29,    -1,    13,
      29,    -1,    30,     9,    -1,    31,    10,    28,    11,    -1,
      31,     6,    40,    -1,     1,    -1,    31,    12,    32,    -1,
      32,    -1,    32,    13,    33,    -1,    33,    -1,    14,    36,
      15,    33,    -1,    14,    36,    15,    -1,    16,     5,    17,
      31,    18,    -1,    16,    39,    17,    31,    18,    -1,    34,
      -1,    35,    -1,    35,    37,    -1,    19,    37,    -1,    36,
      -1,     7,    -1,    20,    33,    -1,    16,    31,    18,    -1,
      21,    35,    -1,     3,    19,    -1,     4,    19,    -1,     5,
      19,    -1,     4,    -1,     3,    -1,    36,    -1,    20,    37,
      -1,    16,    38,    18,    -1,    37,    -1,    37,    12,    38,
      -1,     3,    16,    48,    18,    -1,     3,    16,    18,    -1,
      41,    -1,    41,    22,    40,    -1,    42,    21,    44,    -1,
      42,    13,    44,    -1,    42,    20,    14,     3,    15,    -1,
       4,    -1,     8,    -1,    23,    -1,     5,    43,    -1,    14,
       3,    15,    43,    -1,    -1,    44,    13,    45,    -1,    45,
      -1,    14,     3,    15,    45,    -1,    46,    -1,    47,    -1,
      47,    50,    -1,    19,    50,    -1,    50,    -1,     7,    -1,
      16,    44,    18,    -1,    50,    19,    -1,    49,    22,    48,
      -1,    49,    -1,    50,    -1,     7,    -1,    23,    -1,     3,
      -1,     4,    -1,     5,    43,    -1,     8,    -1,     3,    16,
      48,    18,    -1,     3,    16,    18,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  private static final short yyprhs_[] =
  {
         0,     0,     3,     5,     8,    10,    13,    18,    22,    26,
      29,    32,    35,    40,    44,    46,    50,    52,    56,    58,
      63,    67,    73,    79,    81,    83,    86,    89,    91,    93,
      96,   100,   103,   106,   109,   112,   114,   116,   118,   121,
     125,   127,   131,   136,   140,   142,   146,   150,   154,   160,
     162,   164,   166,   169,   174,   175,   179,   181,   186,   188,
     190,   193,   196,   198,   200,   204,   207,   211,   213,   215,
     217,   219,   221,   223,   226,   228,   233
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  private static final short yyrline_[] =
  {
         0,   207,   207,   209,   210,   213,   214,   217,   218,   219,
     220,   223,   224,   233,   234,   237,   238,   241,   243,   246,
     247,   248,   249,   250,   253,   254,   256,   257,   258,   260,
     261,   262,   269,   270,   271,   274,   275,   277,   278,   279,
     282,   283,   286,   289,   312,   317,   320,   325,   332,   336,
     337,   338,   339,   343,   344,   347,   348,   351,   352,   356,
     357,   359,   362,   363,   365,   374,   379,   380,   386,   387,
     388,   391,   392,   393,   394,   395,   398
  };

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
		       yyrhs_[yyprhs_[yyrule] + yyi],
		       ((yystack.valueAt (yynrhs-(yyi + 1)))), 
		       yystack.locationAt (yynrhs-(yyi + 1)));
  }

  /* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
  private static final byte yytranslate_table_[] =
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

  private static final byte yytranslate_ (int t)
  {
    if (t >= 0 && t <= yyuser_token_number_max_)
      return yytranslate_table_[t];
    else
      return yyundef_token_;
  }

  private static final int yylast_ = 175;
  private static final int yynnts_ = 27;
  private static final int yyempty_ = -2;
  private static final int yyfinal_ = 39;
  private static final int yyterror_ = 1;
  private static final int yyerrcode_ = 256;
  private static final int yyntokens_ = 24;

  private static final int yyuser_token_number_max_ = 265;
  private static final int yyundef_token_ = 2;

/* User implementation code.  */
/* Unqualified %code blocks.  */

/* Line 875 of lalr1.java  */
/* Line 26 of "RuleParser.y"  */

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



/* Line 875 of lalr1.java  */
/* Line 1904 of "RuleParser.java"  */

}


