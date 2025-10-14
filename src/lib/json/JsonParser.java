package lib.json;

import java.math.BigDecimal;

public class JsonParser<CH extends JsonListener> {

  CH handler;

  String buf;
  int position, limit;
  int offset, length;
  int lines, mark;
  String name;

  final static int EOF = -1;

  public JsonParser<CH> handler(CH handler) {
    this.handler = handler;
    return this;
  }

  public JsonParser<CH> reset(String input) {
    buf = input;
    limit = buf.length();
    position = 0;
    lines = mark = 0;
    name = null;
    return this;
  }

  public void parse() {
    try {
      prologue();
      var c = nonWhitespace();
      switch (c) {
        case '{' -> object();
        case '[' -> array();
        default -> { if (c != EOF) throw new ParseException(invalid()); }
      }
    }
    catch (IndexOutOfBoundsException e) {
      handler.error(new ParseException("unexpected end of data", e));
    }
    catch (ParseException e) {
      handler.error(e);
    }
  }

  String invalid() {
    return "invalid character <" + buf.codePointAt(position - 1) + ">" + location();
  }

  String location() {
    return " at " + (lines + 1) + ':' + (position - mark) + " (" + position + ')';
  }

  int nonWhitespace() {
    while (position < limit) {
      var c = buf.codePointAt(position++);
      if (c == ' ' || c == '\r' || c == '\t') continue;
      if (c == '\n') {
        lines += 1;
        mark = position;
        continue;
      }
      return c; // c is at position-1
    }
    return EOF;
  }

  void object() {
    handler.objectStart(name);
    if (!empty('}')) {
      for (;;) {
        key();
        value();
        var c = nonWhitespace();
        if (c == ',') continue;
        if (c == '}') break;
        throw new ParseException(invalid());
      }
      name = null;
    }
    handler.objectEnd();
  }

  void array() {
    handler.arrayStart(name);
    if (!empty(']')) {
      name = null;
      for (;;) {
        value();
        var c = nonWhitespace();
        if (c == ',') continue;
        if (c == ']') break;
        throw new ParseException(invalid());
      }
    }
    handler.arrayEnd();
  }

  void key() {
    name = null;
    var c = nonWhitespace();
    if (c == '"') {
      string();
      if (nonWhitespace() == ':') {
        name = buf.substring(offset, offset+length);
      }
    } else if (!specialKey(c)) {
      throw new ParseException("missing name" + location());
    }
  }

  void value() {
    var c = nonWhitespace();
    switch (c) {
      case '{' -> object();
      case '[' -> array();
      case '"' -> {
        string();
        handler.stringValue(name, buf.substring(offset, offset+length));
      }
      default -> {
        if (numeric(c)) {
          number();
          handler.numberValue(name, new BigDecimal(buf.substring(offset,position).toCharArray()));
        } else
        if (token("true")) handler.booleanValue(name, Boolean.TRUE); else
        if (token("false")) handler.booleanValue(name, Boolean.FALSE); else
        if (token("null")) handler.nullValue(name); else
        if (!specialValue(c)) throw new ParseException(invalid());
      }
    }
  }

  boolean token(String s) {
    var mark = position;
    var n = s.length();
    for (var i = 1; i < n; i++) {
      if (s.charAt(i) != buf.codePointAt(position++)) {
        position = mark;
        return false;
      }
    }
    return true;
  }

  void string() {
    offset = position;
    loop: for (;;) {
      var c = buf.codePointAt(position++);
      switch (c) {
        case '"' -> {
          length = (position - 1) - offset;
          return;
        }
        case '\b', '\f', '\n', '\r', '\t' -> {
          break loop;
        }
        case '\\' -> {
          c = buf.codePointAt(position++);
          switch (c) {
            case '"', '\\', '/', 'b', 'f', 'n', 'r', 't' -> {
              continue loop;
            }
            default -> {
              if (c == 'u' && utf16()) {
                continue loop;
              } else break loop;
            }
          }
        }
      }
    }
    throw new ParseException("invalid character" + location());
  }

  boolean utf16() {
    for (var i = 0; i < 4; i++) {
      var c = buf.codePointAt(position++);
      if ((c >= '0' && '9' >= c) || (c >= 'A' && 'F' >= c) || (c >= 'a' && 'f' >= c)) continue;
      return false;
    }
    return true;
  }

  boolean numeric(int c) {
    if ('-' == c) {
      offset = position - 1;
      return true;
    }
    if (c >= '0' && '9' >= c) {
      offset = --position;
      return true;
    }
    return false;
  }

  void number() {
    if (!digits()) {
      throw new ParseException(invalid());
    }
    var c = buf.codePointAt(position);
    if (c == '.') {
      position++;
      if (!digits()) {
        throw new ParseException(invalid());
      }
      c = buf.codePointAt(position);
    }
    if (c == 'e' || c == 'E') {
      c = buf.codePointAt(++position);
      if (c == '+' || c == '-') {
        position++;
      }
      if (!digits()) {
        throw new ParseException(invalid());
      }
    }
  }

  boolean digits() {
    var p = position;
    for (;;) {
      var c = buf.codePointAt(position);
      if (c < '0' || '9' < c) break;
      position++;
    }
    return p < position;
  }

  boolean empty(int c) {
    if (c == nonWhitespace()) {
      return true;
    } else {
      position--;
      return false;
    }
  }

  void prologue() {
    // any initial processing
  }

  boolean specialKey(int c) {
    return false;
  }

  boolean specialValue(int c) {
    return false;
  }

}
