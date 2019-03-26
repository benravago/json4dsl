package lib.json;

import static java.lang.Character.*;

public class JslParser extends JsonParser<JslAdapter> {

    @Override
    boolean specialName(int c) {
        if (isJavaIdentifierStart(c) || isIdentifierPart(c)) {
            special();
            length = position - offset;
            if (nonWhitespace() == ':') {
                name = new String(buf,offset,length);
                return true;
            }
        }
        return false;
    }

    @Override
    boolean specialValue(int c) {
        if (isJavaIdentifierStart(c) || isIdentifierPart(c)) {
            identifier();
            return true;
        }
        return false;
    }

    void identifier() {
        special();
        var ident = new String(buf,offset,(position-offset));
        var c = nonWhitespace();
        if (c == '(') {
            handler.identifierStart(name,ident);
            parameters();
            handler.identifierEnd();
            c = nonWhitespace();
        } else {
            if (c == '{') {
                handler.identifierStart(name,ident);
                handler.identifierEnd();
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
                name();
                value();
                var c = nonWhitespace();
                if (c == ',') continue;
                if (c == ')') break;
                throw new ParseException(invalid());
            }
            name = null;
        }
    }

    void special() {
        int c;
        offset = position - 1;
        do { c = buf[position++]; }
        while (isJavaIdentifierPart(c) || isIdentifierPart(c));
        position--;
    }

    boolean isIdentifierPart(int c) {
        return 0 < c && c < 128 && SYM[c] == 1;
    }

    final static byte[] SYM = new byte[128];
    static {  // excludes ' ( ) + , - : ; [ ] ` { }
        char[] c = {'!','#','$','%','&','*','.','/','<','=','>','?','@','^','_','|','~'};
        for (var i:c) SYM[i] = 1;
    }
}
