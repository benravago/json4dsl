package lib.json;

public class JslPrinter extends JsonPrinter implements JslAdapter {

    public JslPrinter() {
        this(0);
    }
    public JslPrinter(int indent) {
        super(indent);
    }

    @Override
    void quote(String name) {
        buf.append(name);
    }

    @Override
    public void identifierStart(String name, String ident) {
        tag(name); buf.append(ident); gap(); buf.append('('); push();
    }
    @Override
    public void identifierEnd() {
        clip(); pop(); tag(null); buf.append("),");
    }

    @Override
    public void objectStart(String name) {
        if (name == null) {
            var n = buf.length();
            if (n > 2 && buf.charAt(n-2) == ')' && buf.charAt(n-1) == ',') {
                buf.replace(n-1,n," {");
                push();
                return;
            }
        }
        super.objectStart(name);
    }

}
