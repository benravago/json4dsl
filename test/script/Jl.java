package script;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import lib.json.InputSource;
import lib.json.JslParser;
import lib.json.JslPrinter;

import org.junit.Test;

public class Jl {

    String file = "test/script/test.jsl";
    
    @Test
    public void test() throws Exception {
        var in = new InputSource(source(file));
        var out = new JslPrinter(2);
        new JslParser().handler(out).reset(in).parse();
        var text = out.toString();
        System.out.println(text);
    }

    InputStream source(String filename) throws IOException {
        return Files.newInputStream(Paths.get(filename));
    }

}
