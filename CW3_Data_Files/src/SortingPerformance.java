package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.algorithm.InsertionSort;
import src.algorithm.MergeSort;
import src.algorithm.QuickSort;

/**
 * Sorting algorithm performance test class
 */
public class SortingPerformance {
    /**
     * Read place names from CSV file
     * @param filePath file path
     * @return array of place names
     */
    public static String[] readPlacesFromCSV(String filePath) {
        List<String> places = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                places.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading place data: " + e.getMessage());
        }
        
        return places.toArray(new String[0]);
    }
    
    /**
     * Test sorting algorithm performance
     * @param algorithm algorithm name
     * @param array array to sort
     * @return sorting time (nanoseconds)
     */
    public static long testSortingAlgorithm(String algorithm, String[] array) {
        String[] copy = array.clone();
        
        long startTime = System.nanoTime();
        
        switch (algorithm) {
            case "insertion":
                InsertionSort.sort(copy);
                break;
            case "quick":
                QuickSort.sort(copy);
                break;
            case "merge":
                MergeSort.sort(copy);
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
        
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        String[] datasets = {
            "1000places_sorted.csv",
            "1000places_random.csv",
            "10000places_sorted.csv",
            "10000places_random.csv"
        };
        
        String[] algorithms = {"insertion", "quick", "merge"};
        
        System.out.println("Dataset\t\t\t\tInsertion Sort(ns)\tQuick Sort(ns)\t\tMerge Sort(ns)");
        System.out.println("--------------------------------------------------------------------");
        
        for (String dataset : datasets) {
            String[] places = readPlacesFromCSV(dataset);
            System.out.print(dataset + "\t\t");
            
            for (String algorithm : algorithms) {
                long time = testSortingAlgorithm(algorithm, places);
                System.out.print(time + "\t\t");
            }
            
            System.out.println();
        }
    }
}