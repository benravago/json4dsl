package lib.util;

import java.util.Comparator;

public class CombSort {

    public static <T> void combSort(T[] list) {
        combSort(list, (a,b) -> ((Comparable)a).compareTo(b));
    }

    public static <T> void combSort(T[] list, Comparator To) {
        var size = list.length;
        var gap = size;

        var swapped = true;
        while (gap > 1 || swapped) {
            if (gap > 1) gap = (int)(gap / 1.3);
            if (gap == 9 || gap == 10) gap = 11;

            swapped = false;
            int i=0, j;
            while ((j = i + gap) < size) {
                var x = list[i];
                var y = list[j];
                if (To.compare(x,y) > 0) {
                    list[i] = y;
                    list[j] = x;
                    swapped = true;
                }
                i++;
            }
        }
    }

}
