package lib.json;

public class JslParser<T extends JslListener> extends JsonParser<T>{

  @Override
  boolean specialKey(int c) {
    if (isIdentifierStart(c) || isIdentifierSymbol(c)) {
      special();
      length = position - offset;
      if (nonWhitespace() == ':') {
        name = buf.substring(offset,offset+length);
        return true;
      }
    }
    return false;
  }

  @Override
  boolean specialValue(int c) {
    return identifier(c) || block(c);
  }

  boolean identifier(int c) {
    if (isIdentifierStart(c) || isIdentifierSymbol(c)) {
      special();
      var ident = buf.substring(offset,position);
      entity(ident);
      return true;
    }
    return false;
  }

  void entity(String ident) {
    var c = nonWhitespace();
    if (c == '(') {
      handler.entityStart(name,ident);
      parameters();
      handler.entityEnd();
      c = nonWhitespace();
    } else {
      if (c == '{') {
        handler.entityStart(name,ident);
        handler.entityEnd();
      } else {
        handler.stringValue(name,ident);
      }
    }
    if (c == '{') {
      name = null;
      object();
    } else {
      position--;
    }
  }

  void parameters() {
    if (!empty(')')) {
      for (;;) {
        tag();
        value();
        var c = nonWhitespace();
        if (c == ',') continue;
        if (c == ')') break;
        throw new ParseException(invalid());
      }
      name = null;
    }
  }

  void tag() {
    name = null;
    var mark = position;
    var c = nonWhitespace();
    if (specialKey(c)) return;
    position = mark;
  }

  void special() {
    int c;
    offset = position - 1;
    do { c = buf.codePointAt(position++); }
    while (isIdentifierPart(c) || isIdentifierSymbol(c));
    position--;
  }

  static final int K = '"';

  boolean blockquote(int c) {
    return K == c && position < limit - 2 && K == buf.codePointAt(position+0) && K == buf.codePointAt(position+1);
  }

  boolean block(int c) {
    if (blockquote(c)) {
      position += 2;
      offset = position;
      while (c != EOF) {
        c = buf.codePointAt(position++);
        if (blockquote(c)) {
          length = (position - 1) - offset;
          handler.stringValue(name, buf.substring(offset, offset+length));
          position += 2;
          return true;
        }
      }
      throw new ParseException("non-terminated block" + location());
    }
    return false;
  }

  static boolean isIdentifierStart(int c) {
    return (c >= 'A' && 'Z' >= c) || (c >= 'a' && 'z' >= c) || (c == '$') || (c == '_');
  } // A-Z a-z or $ or _

  static boolean isIdentifierPart(int c) {
    return (c >= '0' && '9' >= c) || isIdentifierStart(c);
  } // 0-9 or A-Z a-z or $ or _

  static boolean isIdentifierSymbol(int c) {
    return switch (c) {
      case '!', '#', '$', '%', '&', '*', '.', '/', '<', '=', '>', '?', '@', '^', '|', '~' -> true;
      default -> false; // excludes ' ( ) + , - : ; [ ] _ ` { }
    };
  }

}