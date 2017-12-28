package net.coderodde.util;

import java.util.Arrays;
import java.util.Random;

public class Demo {

    public static void main(String[] args) {
        Integer[] array1 = createRandomIntegerArray(20, new Random());
        Integer[] array2 = array1.clone();
        
        // Note that we do not sort the entire array! The first and the last 
        // array components are not taken into account!
        HeapSelectionSort.sort(array1, 1, array1.length - 1);
        Arrays.sort(array2, 1, array2.length - 1);
        
        if (arraysEqual(array1, array2)) {
            System.out.println("Algorithms agree: true");
            System.out.println(Arrays.toString(array1));
        } else {
            throw new IllegalStateException("Should not be thrown.");
        }
    }

    private static Integer[] createRandomIntegerArray(int size, Random random) {
        Integer[] ret = new Integer[size];

        for (int i = 0; i < size; ++i) {
            ret[i] = random.nextInt(10 * size);
        }

        return ret;
    }

    private static <T> boolean arraysEqual(T[] array1, T[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; ++i) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }

        return true;
    }
}
