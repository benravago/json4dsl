package lib.util;

import java.util.Comparator;

public class BinarySearch {

    public static <T> int binarySearch(T[] list, T key) {
        return binarySearch(list, key, (a,b) -> ((Comparable)a).compareTo(b));
    }

    public static <T> int binarySearch(T[] list, T key, Comparator<T> To) {
        var low = 0;
        var high = list.length - 1;

        while (low <= high) {
            var mid = (low + high) >>> 1;
            var probe = To.compare(list[mid],key);
            if (probe < 0) {
                low = mid + 1;
            } else {
                if (probe > 0) {
                    high = mid - 1;
                } else {
                    return mid;  // key found
                }
            }
        }
        return - (low + 1);  // key not found
    }
}