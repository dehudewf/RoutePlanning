package src.algorithm;

/**
 * Quick sort algorithm implementation
 */
public class QuickSort {
    /**
     * Sort an array using quick sort algorithm
     * @param array array to sort
     * @param <T> array element type, must implement Comparable
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        quickSort(array, 0, array.length - 1);
    }
    
    /**
     * Quick sort recursive implementation
     * @param array array to sort
     * @param low lower bound index
     * @param high upper bound index
     * @param <T> array element type
     */
    private static <T extends Comparable<T>> void quickSort(T[] array, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(array, low, high);
            
            quickSort(array, low, partitionIndex - 1);
            quickSort(array, partitionIndex + 1, high);
        }
    }
    
    /**
     * Partition operation
     * @param array array to partition
     * @param low lower bound index
     * @param high upper bound index
     * @param <T> array element type
     * @return partition point index
     */
    private static <T extends Comparable<T>> int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        
        swap(array, i + 1, high);
        return i + 1;
    }
    
    /**
     * Swap two elements in an array
     * @param array array
     * @param i first element index
     * @param j second element index
     * @param <T> array element type
     */
    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}