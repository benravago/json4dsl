package lib.util;

import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public final class Dictionaries {
    private Dictionaries() {}

    public static <K,V> Dictionary<K,V> newDictionary() {
        return newDictionary((a,b) -> ((Comparable)a).compareTo(b));
    }

    public static <K,V> Dictionary<K,V> newDictionary(Comparator<K> comparator) {
        return new Dictionary<K,V>() {
            Object[][] array = new Object[0][];

            @Override
            public int size() { return array.length; }

            @Override
            public boolean isEmpty() { return size() < 1; }

            @Override
            public Enumeration<K> keys() { return enumeration(array,KEY); }

            @Override
            public Enumeration<V> elements() { return enumeration(array,VALUE); }

            @Override
            public V get(Object key) {
                var index = binarySearch(array,key,comparator);
                return index < 0 ? null : (V) array[index][VALUE];
            }

            @Override
            public V put(K key, V value) {
                var index = binarySearch(array,key,comparator);
                if (index < 0) {
                    index = - index - 1;
                    array = insert(array,index,key,value);
                    return null;
                } else {
                    var previous = (V) array[index][VALUE];
                    array[index][VALUE] = value;
                    return previous;
                }
            }

            @Override
            public V remove(Object key) {
                var index = binarySearch(array,key,comparator);
                if (index < 0) {
                    return null;
                } else {
                    var previous = (V) array[index][VALUE];
                    array = delete(array,index);
                    return previous;
                }
            }
        };
    }

    static final int KEY = 0;
    static final int VALUE = 1;

    static int binarySearch(Object[][] src, Object key, Comparator comparator) {
        var low = 0;
        var high = src.length - 1;

        while (low <= high) {
            var mid = (low + high) >>> 1;
            var probe = comparator.compare(src[mid][KEY],key);
            if (probe < 0) {
                low = mid + 1;
            } else {
                if (probe > 0) {
                    high = mid - 1;
                } else {
                    return mid; // key found
                }
            }
        }
        return - (low + 1); // key not found.
    }

    static Object[][] insert(Object[][] src, int index, Object ... element) {
        var dest = new Object[src.length+1][];
        if (index > 0) System.arraycopy(src,0,dest,0,index);
        if (index < src.length) System.arraycopy(src,index,dest,index+1,src.length-index);
        dest[index] = element;
        return dest;
    }

    static Object[][] delete(Object[][] src, int index) {
        var dest = new Object[src.length-1][];
        if (index > 0) System.arraycopy(src,0,dest,0,index);
        if (index < dest.length) System.arraycopy(src,index+1,dest,index,dest.length-index);
        return dest;
    }

    static <T> Enumeration<T> enumeration(Object[][] src, int offset) {
        return new Enumeration<T>() {
            int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < src.length;
            }
            @Override
            public T nextElement() {
                if (hasMoreElements()) {
                    return (T) src[index++][offset];
                }
                throw new NoSuchElementException();
            }
        };
    }

}
