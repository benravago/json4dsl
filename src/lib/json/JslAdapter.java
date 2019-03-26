package lib.json;

public interface JslAdapter extends ContentHandler {
    
    // name: identifier(parameters) {}
    
    default void identifierStart(String name, String ident) {} // -> name: ident(
    default void identifierEnd() {}              // -> )
    
}
