package lib.util;

import java.util.Enumeration;
import java.util.Iterator;

/*
 *  import static lib.util.Iteration.*;
 *
 *  for ( Object o : each(enumeration) ) { ... }
 */
public interface Iteration {

    static <T> Iterable<T> each(Iterator<T> iterator) {
        return () -> iterator;
    }

    static <T> Iterable<T> each(Enumeration<T> enumeration) {
        return each(enumeration.asIterator());
    }
}
