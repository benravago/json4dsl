package lib.json.tool;

import java.nio.file.Files;
import java.nio.file.Paths;

import lib.json.JbmlListener;
import lib.json.JbmlParser;
import lib.json.ParseInput;

public class Jbml extends Jsl implements JbmlListener {

  public Jbml() {
    this(0);
  }

  public Jbml(int indent) {
    super(indent);
  }

  @Override
  public void directive(String name, String target) {
    buf.append("\n! ").append(name).append(' ').append(target);
  }


  public static void main(String...args) throws Exception {
    if (args.length != 1) return;
    var handler = new Jbml(2);
    new JbmlParser()
      .handler(handler)
      .reset(new ParseInput(Files.readString(Paths.get(args[0])).toCharArray()))
      .parse();
    System.out.println("jbml: "+handler);
  }
}
