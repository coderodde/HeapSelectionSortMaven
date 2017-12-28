package net.coderodde.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * This class implements heap selection sort.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 16, 2017)
 */
public final class HeapSelectionSort {
    
    /**
     * The natural comparator delegating to possible {@code compareTo} method
     * of the input objects.
     */
    private static final Comparator NATURAL_COMPARATOR = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            Comparable c1 = (Comparable) o1;
            return c1.compareTo(o2);
        }
    };
    
    private HeapSelectionSort() {}
    
    public static <T> void sort(T[] array, 
                                int fromIndex, 
                                int toIndex, 
                                Comparator<? super T> comparator) {
        Objects.requireNonNull(array);
        checkIndices(array.length, fromIndex, toIndex);
        
        if (toIndex - fromIndex < 2) {
            // Trivially sorted.
            return;
        }
        
        if (comparator == null) {
            comparator = (Comparator<? super T>) NATURAL_COMPARATOR;
        }
        
        T[] aux = Arrays.copyOfRange(array, fromIndex, toIndex);
        RunHeapBuilder<T> runHeapBuilder = new RunHeapBuilder<>(aux,
                                                                comparator);
        
        RunHeap<T> runHeap = runHeapBuilder.build();
        runHeap.heapify();
        
        for (; fromIndex < toIndex; ++fromIndex) {
            array[fromIndex] = runHeap.popHead();
        }
    }
    
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array);
        sort(array, 0, array.length, comparator);
    }
    
    /**
     * Makes sure that the indices specify a valid range.
     * 
     * @param arrayLength the length of the target array.
     * @param fromIndex   the index of the leftmost array component belonging to
     *                    the range to sort.
     * @param toIndex     the index one past the last array component belonging
     *                    to the range to sort.
     */
    private static void checkIndices(int arrayLength, 
                                     int fromIndex,
                                     int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException(
                    "fromIndex(" + fromIndex + ") < 0");
        }
        
        if (toIndex > arrayLength) {
            throw new IndexOutOfBoundsException(
                    "toIndex(" + toIndex + ") >= arrayLength(" + 
                    arrayLength + ")");
        }
        
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
    }
    
    /**
     * This class implements a run heap.
     * 
     * @param <T> the array component type.
     */
    private static final class RunHeap<T> {
        private int size;
        private final T[] array;
        private final int[] fromIndexArray;
        private final int[] toIndexArray;
        private final Comparator<? super T> comparator;
        
        RunHeap(T[] array, Comparator<? super T> comparator) {
            this.array = array;
            this.fromIndexArray = new int[array.length / 2 + 1];
            this.toIndexArray = new int[array.length / 2 + 1];
            this.comparator = comparator;
        }
        
        /**
         * Removes and returns the minimum element stored in the heap.
         * 
         * @return the minimum element.
         */
        T popHead() {
            T ret = array[fromIndexArray[0]];
            
            if (fromIndexArray[0] == toIndexArray[0]) {
                // The head run is exhausted.
                int last = fromIndexArray[--size];
                fromIndexArray[0] = last;
                last = toIndexArray[size];
                toIndexArray[0] = last;
            } else {
                // Increment to the next element.
                fromIndexArray[0]++;
            }
            
            // Possibly sift down the top element in order to restore the heap
            // invariant.
            siftDown(0);
            return ret;
        }
        
        /**
         * Appends a run to the end of this heap.
         * 
         * @param fromIndex the starting inclusive index of the run.
         * @param toIndex   the ending inclusive index of the run.
         */
        void pushRun(int fromIndex, int toIndex) {
            int nodeIndex = size++;
            fromIndexArray[nodeIndex] = fromIndex;
            toIndexArray[nodeIndex] = toIndex;
        }
        
        /**
         * Appends a run to the most recently added run.
         * 
         * @param fromIndex the starting inclusive index of a run.
         * @param toIndex   the ending inclusive index of a run.
         */
        void appendRun(int runLength) {
            toIndexArray[size - 1] += runLength;
        }
        
        /**
         * Heapifies the entire heap of runs. Runs in time linear to the number 
         * of runs.
         */
        void heapify() {
            for (int i = size / 2; i >= 0; --i) {
                siftDown(i);
            }
        }
        
        /**
         * Compares two runs' head elements and returns {@code true} only if 
         * the first run should take precedence.
         * 
         * @param runIndex1 the index of the first run.
         * @param runIndex2 the index of the second run.
         * @return {@code true} only if the first run should take precedence.
         */
        private boolean isLessThan(int runIndex1, int runIndex2) {
            T element1 = array[fromIndexArray[runIndex1]];
            T element2 = array[fromIndexArray[runIndex2]];
            
            int cmp = comparator.compare(element1, element2);
            
            if (cmp != 0) {
                return cmp < 0;
            }
            
            return fromIndexArray[runIndex1] < fromIndexArray[runIndex2];
        }
        
        private void siftDown(int index) {
            int leftChildIndex = (index << 1) + 1;
            int rightChildIndex = leftChildIndex + 1;
            int minIndex = index;
            int targetFromIndex = fromIndexArray[index];
            int targetToIndex = toIndexArray[index];
            
            while (true) {
                if (leftChildIndex < size 
                        && isLessThan(leftChildIndex, index)) {
                    minIndex = leftChildIndex;
                }
                
                if (minIndex == index) {
                    if (rightChildIndex < size
                            && isLessThan(rightChildIndex, index)) {
                        minIndex = rightChildIndex;
                    }
                } else {
                    if (rightChildIndex < size
                            && isLessThan(rightChildIndex, leftChildIndex)) {
                        minIndex = rightChildIndex;
                    }
                }
                
                if (minIndex == index) {
                    // No swap. Just insert the run.
                    fromIndexArray[minIndex] = targetFromIndex;
                    toIndexArray[minIndex] = targetToIndex;
                    return;
                }
                
                // No swap here either. Just move up the maximum to current 
                // position.
                fromIndexArray[index] = fromIndexArray[minIndex];
                toIndexArray[index] = toIndexArray[minIndex];
                index = minIndex;
                leftChildIndex = (index << 1) + 1;
                rightChildIndex = leftChildIndex + 1;
            }
        }
    }
    
    private static final class RunHeapBuilder<T> {
        private final RunHeap<T> runHeap;
        private final Comparator<? super T> comparator;
        private final T[] array;
        private int head;
        private int left;
        private int right;
        private final int last;
        private boolean previousRunWasDescending;
        
        RunHeapBuilder(T[] array, Comparator<? super T> comparator) {
            this.runHeap = new RunHeap<>(array, comparator);
            this.comparator = comparator;
            this.array = array;
            this.right = 1;
            this.last = array.length - 1;
        }
        
        RunHeap<T> build() {
            while (left < last) {
                head = left;
                
                if (comparator.compare(array[left++], array[right++]) <= 0) {
                    // The next run is ascending:
                    scanAscendingRun();
                } else {
                    // The next run is descending:
                    scanDescendingRun();
                }
                
                ++left;
                ++right;
            }
            
            handleLastElement();
            return runHeap;
        }
        
        // Pushes or appends a newly found run.
        private void addRun() {
            if (previousRunWasDescending) {
                if (comparator.compare(array[head - 1], array[head]) <= 0) {
                    runHeap.appendRun(right - head);
                } else {
                    runHeap.pushRun(head, left);
                }
            } else {
                runHeap.pushRun(head, left);
            }
        }
        
        // Scans an ascending run.
        private void scanAscendingRun() {
            while (left < last 
                    && comparator.compare(array[left], array[right]) <= 0) {
                ++left;
                ++right;
            }
            
            addRun();
            previousRunWasDescending = false;
        }
        
        // Scans an descending run.
        private void scanDescendingRun() {
            while (left != last 
                    && comparator.compare(array[left], array[right]) > 0) {
                ++left;
                ++right;
            }
            
            reverseRun();
            addRun();
            previousRunWasDescending = true;
        }
        
        // Reverses a run. This method must be called only on strictly 
        // descending runs.
        private void reverseRun() {
            for (int i = head, j = left; i < j; ++i, --j) {
                T tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
        }
        
        // Handles a possible leftover component at the very end of the input
        // array range.
        private void handleLastElement() {
            if (left == last) {
                // Once here, we have a leftover component.
                if (comparator.compare(array[last - 1], array[last]) <= 0) {
                    runHeap.appendRun(1);
                } else {
                    runHeap.pushRun(left, left);
                }
            }
        }
    }
}
