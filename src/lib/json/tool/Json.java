package lib.json.tool;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import lib.json.JsonListener;
import lib.json.JsonParser;
import lib.json.ParseInput;

public class Json implements JsonListener {

  int indent;
  StringBuilder lead = new StringBuilder("\n");
  StringBuilder buf = new StringBuilder();

  public Json() {
    this(0);
  }

  public Json(int indent) {
    this.indent = indent;
  }

  void push() {
    if (indent > 0) {
      var i = indent;
      while (i-- > 0) lead.append(' ');
    }
  }

  void pop() {
    if (indent > 0) {
      lead.setLength(lead.length() - indent);
    }
  }

  void clip() {
    var n = buf.length();
    if (n > 0 && buf.charAt(--n) == ',') {
      buf.setLength(n);
    }
  }

  void tag(String name) {
    if (indent > 0) buf.append(lead);
    if (name != null) {
      quote(name);
      buf.append(':');
      gap();
    }
  }

  void gap() {
    if (indent > 0) buf.append(' ');
  }

  void quote(String name) {
    buf.append('"').append(name).append('"');
  }

  public void reset() {
    buf.setLength(0);
  }

  @Override
  public String toString() {
    var start = 0;
    var end = buf.length();
    if (end > 0) {
      if (buf.charAt(end - 1) == ',') end--;
      if (buf.charAt(start) == '\n') start++;
    }
    return buf.substring(start, end);
  }

  @Override
  public void objectStart(String name) {
    tag(name);
    buf.append('{');
    push();
  }

  @Override
  public void objectEnd() {
    clip();
    pop();
    tag(null);
    buf.append("},");
  }

  @Override
  public void arrayStart(String name) {
    tag(name);
    buf.append('[');
    push();
  }

  @Override
  public void arrayEnd() {
    clip();
    pop();
    tag(null);
    buf.append("],");
  }

  @Override
  public void booleanValue(String name, Boolean value) {
    tag(name);
    buf.append(value).append(',');
  }

  @Override
  public void nullValue(String name) {
    tag(name);
    buf.append("null,");
  }

  @Override
  public void stringValue(String name, String value) {
    tag(name);
    buf.append('"').append(value).append("\",");
  }

  @Override
  public void numberValue(String name, Number value) {
    tag(name);
    if (value instanceof BigDecimal decimal) {
      buf.append(decimal.toEngineeringString());
    } else {
      buf.append(value);
    }
    buf.append(',');
  }


  public static void main(String...args) throws Exception {
    if (args.length != 1) return;
    var handler = new Json(2);
    new JsonParser<Json>()
      .handler(handler)
      .reset(new ParseInput(Files.readString(Paths.get(args[0])).toCharArray()))
      .parse();
    System.out.println("json: "+handler);
  }
}
