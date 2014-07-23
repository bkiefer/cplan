package de.dfki.lt.tr.dialogue.cplan;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.dfki.lt.tr.dialogue.cplan.RuleParser.Location;
import de.dfki.lt.tr.dialogue.cplan.util.Position;

public class Lexer
implements RuleParser.Lexer, LFParser.Lexer, ExtLFParser.Lexer  {
  private static String[] tokenNames = {
    "ID", "VAR", "GVAR", "COMPARISON", "ARROW", "STRING", "RVAR"
  };

  private static final int ID_TOKEN = 258;

  private static final String charTokens = "^|!()<>:=@.#,~{}";

  private int _pushBack = -1;

  private Reader _in;
  /** current stream position */
  private int _line;
  private int _column;

  /** Start position of the last token */
  private Position _startPos;

  /** print errors or only collect them */
  private boolean _quiet = false;

  /** log parsing errors to this logger, if not null */
  private Logger _logger = null;

  /** Information to generate useful error messages: input location */
  private String _inputDescription;

  /** All errors since the last reset */
  private List<Position> _lastErrors;

  private int _nextChar;

  private String _lval;

  private HashMap<Integer, String> token2String;

  public Lexer() {
    token2String = new HashMap<Integer, String>();
    for (int i = 0; i < charTokens.length(); ++i) {
      char c = charTokens.charAt(i);
      token2String.put(((int) c),  Character.toString(c));
    }

    for (int i = 0; i < tokenNames.length; ++i) {
      token2String.put(ID_TOKEN + i, tokenNames[i]);
    }
    _in = null;
    _inputDescription = null;
    _lastErrors = new ArrayList<Position>();
  }

  public Lexer(String inputDescription, Reader in) {
    this();
    setInputReader(inputDescription, in);
  }

  public Lexer(Reader in) {
    this("Error", in);
  }

  public void setInputReader(String inputDescription, Reader in) {
    _in = in;
    _inputDescription = inputDescription;
    _line = 1;
    _column = 0;
    _nextChar = ' ';
    _lastErrors.clear();
  }

  /** for lexer debugging purposes */
  public String getTokenName(int token) {
    return token2String.get(token);
  }

  /* We have our own position tracking
  public Position getStartPos() { return new Position(); }
  public Position getEndPos() { return new Position(); }
  */

  public void setErrorsQuiet(boolean q) {
    _quiet = q;
  }

  public void setErrorLogger(Logger logger) {
    _logger = logger;
  }

  public void yyerror (String msg) {
    _lastErrors.add(new Position(_line, _column,
        _inputDescription + ":" + _line + ":" + _column + ": " + msg));
    if (! _quiet && _logger == null) {
      System.out.println(getLastErrorPosition().msg);
    }
    else if (_logger != null) {
      _logger.error(getLastErrorPosition().msg);
    }
  }


  @Override
  public void yyerror(Location loc, String msg) {
    Position beg = loc.begin;
    _lastErrors.add(new Position(beg.line, beg.column,
        beg.msg + ":" + beg.line + ":" + beg.column + ":  " + msg));
    if (! _quiet && _logger == null) {
      System.out.println(getLastErrorPosition().msg);
    }
    else if (_logger != null) {
      _logger.error(getLastErrorPosition().msg);
    }
  }

  /** Skip comments. Comments are C++/Java style line and block comments.
   *  Currently, there is no such thing as a Documentation comment.
   *  \todo eventually store comments, and attach them to the rules for
   *  automatic documentation or GUI help purposes.
   */
  void skipComment() throws IOException {
    readNext();
    // line comment
    if (_nextChar == '/') {
      int currline = _line;
      while (currline == _line && _nextChar != RuleParser.Lexer.EOF) {
        readNext();
      }
      return;
    }
    if (_nextChar == '*') {
      while (true) {
        while (_nextChar == '*') {
          readNext();
        }
        if (_nextChar == '/') {
          readNext();
          return;
        }
        while (_nextChar != RuleParser.Lexer.EOF && _nextChar != '*') {
          readNext();
        }
      }
    }
    yyerror("wrong or runaway comment");
  }

  void skipws() throws IOException {
    do {
      while (_nextChar == ' ') {
        readNext();
      }
      if (_nextChar == '/') {
        skipComment();
      }
    } while (_nextChar == '/' || _nextChar == ' ');
  }

  @SuppressWarnings("fallthrough")
  private void readNext() throws IOException {
    if (_pushBack >= 0) {
      _nextChar = _pushBack;
      _pushBack = -1;
    } else {
      _nextChar = _in.read();
    }
    ++_column;
    switch (_nextChar) {
    case -1: _nextChar = RuleParser.Lexer.EOF; break;
    case '\n': _column = 0; ++_line; // fall through is intended
    case ' ':
    case '\t':
    case '\u000C':
    case '\r': _nextChar = ' ' ; break;
    }
  }

  public String readLine() throws IOException {
    StringBuilder sb = new StringBuilder();
    int currentLine = _line;
    skipws();
    while (_nextChar != RuleParser.Lexer.EOF && _line == currentLine) {
      sb.append((char) _nextChar);
      readNext();
    }
    return sb.toString();
  }

  public int peek() throws IOException {
    skipws();
    return _nextChar;
  }

  public int peekFollowing() throws IOException {
    _pushBack = _in.read();
    return _pushBack;
  }

  public boolean atEOF() {
    return _nextChar == RuleParser.Lexer.EOF;
  }

  /*
    --------- Tokens:
    ID     [a-zA-Z][-_a-zA-Z0-9]*
    VAR    #ID
    GVAR   ##ID
    RVAR   ###ID
    ARROW  ->
    STRING "([^\\"]|\\")*"

    --------- single-char tokens
    CONJ   ^
    ALT    |
    OPAREN (
    CPAREN )
    NOT    !
    OANGLE <
    CANGLE >
    DDOT   :
    ASSIGN =
    AT     @
    DOT    .
    HASH   #
    COMMA  ,
    TILDE  ~
    OCURLY {
    CCURLY }
  */

  public int yylex () throws java.io.IOException {
    _lval = null;
    skipws();
    _startPos = getCurrentPosition();
    switch (_nextChar) {
    case RuleParser.Lexer.EOF:
    case '(':
    case ')':
    case '^':
    case '|':
    case '<':
    case '>':
    case ':':
    case '!':
    // case '=':
    case '@':
    case '.':
    case ',':
    case '~':
    case '{':
    case '}': {
      int result = _nextChar;
      readNext();
      return result;
    }
    case '"': {
      StringBuffer sb = new StringBuffer();
      readNext();
      while (_nextChar != '"') {
        if (_nextChar == '\\') {
          readNext();
        }
        if (_nextChar == RuleParser.Lexer.EOF) {
          yyerror("unexpected end of input in string");
          return RuleParser.Lexer.EOF;
        }
        sb.append((char) _nextChar);
        readNext();
      }
      readNext();
      _lval = sb.toString();
      return RuleParser.Lexer.STRING;
    }
    case '#': {
      int what = RuleParser.Lexer.VAR;
      readNext();
      if (_nextChar == '#') {
        what = RuleParser.Lexer.GVAR;
        readNext();
      }
      if (_nextChar == '#') {
        what = RuleParser.Lexer.RVAR;
        readNext();
      }
      StringBuffer sb = new StringBuffer();
      while (Character.isLetterOrDigit(_nextChar)
             || _nextChar == '-' || _nextChar == '_') {
        sb.append((char) _nextChar);
        readNext();
      }
      _lval = sb.toString();
      if (_lval.isEmpty()) {
        _lval = null;
        return '#'; // specification of current location on RHS
      }
      return what;
    }
    case '=':
      int result = _nextChar;
      if (peekFollowing() == '>') {
        _lval = "=>";
        readNext();
        result = RuleParser.Lexer.ARROW;
      }
      readNext();
      return result;
    case '-':
      readNext();
      if (_nextChar != '>') {
        yyerror("unexpected character, expected '>': '" + (char)_nextChar +"'");
        return RuleParser.Lexer.EOF;
      }
      _lval = "->";
      readNext();
      return RuleParser.Lexer.ARROW;
    }

    StringBuffer sb = new StringBuffer();
    while (Character.isLetterOrDigit(_nextChar)
        || _nextChar == '-' || _nextChar == '_' || _nextChar == '\''
        || _nextChar == '+') {
      sb.append((char) _nextChar);
      readNext();
    }
    _lval = sb.toString();
    if (_lval.isEmpty()) {
      yyerror("Empty identifier, possibly illegal character");
      readNext();
    }
    return RuleParser.Lexer.ID;
  }

  public Object getLVal () {
    return _lval;
  }

  private Position getCurrentPosition() {
    return new Position(_line, _column, _inputDescription);
  }

  public Position getLastErrorPosition() {
    if (_lastErrors.isEmpty()) {
      return null;
    }
    return _lastErrors.get(_lastErrors.size() - 1);
  }

  /** Return all errors since the last reset */
  public Collection<? extends Position> getAllErrorPositions() {
    return _lastErrors;
  }

  @Override
  public Position getEndPos() {
    return getCurrentPosition();
  }

  @Override
  public Position getStartPos() {
    return _startPos;
  }
}
