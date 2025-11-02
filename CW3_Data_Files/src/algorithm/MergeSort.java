package src.algorithm;

/**
 * Merge sort algorithm implementation
 */
public class MergeSort {
    /**
     * Sort an array using merge sort algorithm
     * @param array array to sort
     * @param <T> array element type, must implement Comparable
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        if (array.length < 2) {
            return;
        }
        
        int mid = array.length / 2;
        
        @SuppressWarnings("unchecked")
        T[] left = (T[]) new Comparable[mid];
        @SuppressWarnings("unchecked")
        T[] right = (T[]) new Comparable[array.length - mid];
        
        System.arraycopy(array, 0, left, 0, mid);
        System.arraycopy(array, mid, right, 0, array.length - mid);
        
        sort(left);
        sort(right);
        
        merge(array, left, right);
    }
    
    /**
     * Merge two sorted arrays
     * @param result result array
     * @param left left sorted array
     * @param right right sorted array
     * @param <T> array element type
     */
    private static <T extends Comparable<T>> void merge(T[] result, T[] left, T[] right) {
        int i = 0, j = 0, k = 0;
        
        while (i < left.length && j < right.length) {
            if (left[i].compareTo(right[j]) <= 0) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }
        
        while (i < left.length) {
            result[k++] = left[i++];
        }
        
        while (j < right.length) {
            result[k++] = right[j++];
        }
    }
}