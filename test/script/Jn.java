package script;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import lib.json.InputSource;
import lib.json.JsonParser;
import lib.json.JsonPrinter;

import org.junit.Test;
import static org.junit.Assert.*;

public class Jn {

    String file = "test/script/test.json";
    
    @Test
    public void test() throws Exception {
        var in = new InputSource(source(file));
        var out = new JsonPrinter(2);
        new JsonParser().handler(out).reset(in).parse();
        var text = out.toString();
        var buf = in.toString().trim();
        System.out.println(""+text.length()+' '+buf.length());
        System.out.println(text);
        assertEquals(text,buf);
    }

    InputStream source(String filename) throws IOException {
        return Files.newInputStream(Paths.get(filename));
    }
}
