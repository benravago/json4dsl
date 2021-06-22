package lib.json.tool;

import java.nio.file.Files;
import java.nio.file.Paths;

import lib.json.JslListener;
import lib.json.JslParser;
import lib.json.ParseInput;

public class Jsl extends Json implements JslListener {

  public Jsl() {
    this(0);
  }

  public Jsl(int indent) {
    super(indent);
  }

  @Override
  void quote(String name) {
    if (name.matches("\\w*")) {
      buf.append(name);
    } else {
      super.quote(name);
    }
  }

  @Override
  public void entityStart(String name, String ident) {
    tag(name);
    buf.append(ident);
    gap();
    buf.append('(');
    push();
  }

  @Override
  public void entityEnd() {
    clip();
    pop();
    tag(null);
    buf.append("),");
  }

  @Override
  public void objectStart(String name) {
    if (name == null) {
      var n = buf.length();
      if (n > 2 && buf.charAt(n - 2) == ')' && buf.charAt(n - 1) == ',') {
        buf.replace(n - 1, n, " {");
        push();
        return;
      }
    }
    super.objectStart(name);
  }


  public static void main(String...args) throws Exception {
    if (args.length != 1) return;
    var handler = new Jsl(2);
    new JslParser<Jsl>()
      .handler(handler)
      .reset(new ParseInput(Files.readString(Paths.get(args[0])).toCharArray()))
      .parse();
    System.out.println("jsl: "+handler);
  }
}
