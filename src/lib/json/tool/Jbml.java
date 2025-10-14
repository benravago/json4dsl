package lib.json.tool;

import lib.json.JbmlListener;
import lib.json.JbmlParser;

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
      .reset(chars(args[0]))
      .parse();
    System.out.println("jbml: "+handler);
  }
}
