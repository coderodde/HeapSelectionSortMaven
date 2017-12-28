package net.coderodde.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Demo {

    public static void main(final String... args) {
        long seed = 1514460446758L; System.currentTimeMillis();
        Random r = new Random(seed);
        System.out.println("seed = " + seed);
        Integer[] arr = createRandomIntegerArray(11, r);
        Integer[] arr2 = arr.clone();
        HeapSelectionSort.sort(arr, Integer::compareTo);
        Arrays.sort(arr2, Integer::compareTo);
        System.out.println(arraysEqual(arr, arr2));
        System.out.println(Arrays.toString(arr));
        
        System.exit(0);
        demo();
    }

    private static Integer[] createRandomIntegerArray(int size, Random random) {
        Integer[] ret = new Integer[size];

        for (int i = 0; i < size; ++i) {
            ret[i] = random.nextInt(size / 2);
        }

        return ret;
    }

    private static Integer[] createPresortedArray(int size, 
                                                  int runs, 
                                                  Random random) {
        Integer[] ret = createRandomIntegerArray(size, random);
        int runLength = size / runs;

        for (int i = 0; i < runs; ++i) {
            Arrays.sort(ret, runLength * i, 
                        Math.min(size, runLength * (i + 1)));
        }

        return ret;
    }

    private static boolean isSorted(Integer[] array, 
                                    int fromIndex, 
                                    int toIndex) {
        for (int i = fromIndex; i < toIndex - 1; ++i) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSorted(Integer[] array) {
        return isSorted(array, 0, array.length);
    }

    private static <T> boolean arraysEqual(T[] array1, T[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; ++i) {
            if (array1[i] != array2[i]) {
//            if (!array1[i].equals(array2[i])) {
                return false;
            }
        }

        return true;
    }    

    private static void demo() {
        long arraysSortTotal = 0L;
        long heapSelectionSortTotal = 0L;
        long seed = System.nanoTime();
        Comparator<Integer> comparator = Integer::compareTo;

        System.out.println("Seed: " + seed);
        System.out.println("-------------------");

        for (int op = 0; op < 10; ++op) {
            Random random = new Random(System.nanoTime());

            Integer[] array1 = createRandomIntegerArray(1_000_000, random);
            Integer[] array2 = array1.clone();

            long ta = System.currentTimeMillis();
            Arrays.sort(array1, comparator);
            long tb = System.currentTimeMillis();

            System.out.println("Random array:");
            System.out.println("Arrays.sort in " + (tb - ta) + " ms, sorted: " +
                               isSorted(array1));
            arraysSortTotal += tb - ta;

            ta = System.currentTimeMillis();
            HeapSelectionSort.sort(array2, comparator);
            tb = System.currentTimeMillis();

            heapSelectionSortTotal += tb - ta;

            System.out.println("Heap insertion sort in " + (tb - ta) + " ms, " +
                               "sorted: " + isSorted(array2));
            System.out.println("Arrays identical: " + 
                               arraysEqual(array1, array2));

            for (int i = 0; i < 80; ++i) {
                System.out.print("-");
            }

            System.out.println();

            array1 = createPresortedArray(1_000_000, 100, random);
            array2 = array1.clone();

            ta = System.currentTimeMillis();
            Arrays.sort(array1);
            tb = System.currentTimeMillis();

            System.out.println("Presorted array:");
            System.out.println("Arrays.sort in " + (tb - ta) + " ms, sorted: " +
                               isSorted(array1));
            arraysSortTotal += tb - ta;

            ta = System.currentTimeMillis();
            HeapSelectionSort.sort(array2, comparator);
            tb = System.currentTimeMillis();

            heapSelectionSortTotal += tb - ta;

            System.out.println("Heap insertion sort in " + (tb - ta) + " ms, " +
                               "sorted: " + isSorted(array2));
            System.out.println("Arrays identical: " +
                               arraysEqual(array1, array2));

            for (int i = 0; i < 80; ++i) {
                System.out.print("-");
            }

            System.out.println();
        }

        System.out.println("Total of Arrays.sort: " + arraysSortTotal + " ms.");
        System.out.println("Total of heap insertion sort: " + 
                           heapSelectionSortTotal + " ms.");
    }
}
