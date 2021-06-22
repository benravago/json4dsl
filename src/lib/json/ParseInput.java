package lib.json;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public class InputSource {

    private final char[] cbuf;

    public InputSource(char[] cbuf) {
        this.cbuf = cbuf;
    }

    public char[] getChars() {
        return cbuf;
    }

    @Override
    public String toString() {
        return new String(cbuf);
    }

    public InputSource(InputStream in) {
        this(readAllBytes(in));
    }

    private static char[] readAllBytes(InputStream in) {
        try {
            var b = in.readAllBytes();
            return new String(b).toCharArray();
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public InputSource(Reader in) {
        this(readAllChars(in));
    }

    private static char[] readAllChars(Reader in) {
        try {
            var s = new StringBuilder();
            var b = new char[512];
            int n;
            while ((n = in.read(b)) != -1) s.append(b,0,n);
            return s.toString().toCharArray();
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}