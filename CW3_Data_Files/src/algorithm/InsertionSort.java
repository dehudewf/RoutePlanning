package src.algorithm;

/**
 * Insertion sort algorithm implementation
 */
public class InsertionSort {
    /**
     * Sort an array using insertion sort algorithm
     * @param array array to sort
     * @param <T> array element type, must implement Comparable
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        for (int i = 1; i < array.length; i++) {
            T key = array[i];
            int j = i - 1;
            
            while (j >= 0 && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            
            array[j + 1] = key;
        }
    }
}