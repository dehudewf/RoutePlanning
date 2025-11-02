package src.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * CSV file reader utility class
 */
public class CSVReader {
    /**
     * Read attractions.csv file
     * @param filePath file path
     * @return mapping from attraction name to city
     */
    public static Map<String, String> readAttractions(String filePath) {
        Map<String, String> attractions = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    String attraction = data[0].trim();
                    String location = data[1].trim();
                    attractions.put(attraction, location);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attraction data: " + e.getMessage());
        }
        
        return attractions;
    }
    
    /**
     * Read roads.csv file
     * @param filePath file path
     * @return adjacency list of distances between cities
     */
    public static Map<String, Map<String, Integer>> readRoads(String filePath) {
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String cityA = data[0].trim();
                    String cityB = data[1].trim();
                    int distance = Integer.parseInt(data[2].trim());
                    
                    // Add bidirectionally since it's an undirected graph
                    graph.putIfAbsent(cityA, new HashMap<>());
                    graph.putIfAbsent(cityB, new HashMap<>());
                    
                    graph.get(cityA).put(cityB, distance);
                    graph.get(cityB).put(cityA, distance);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading road data: " + e.getMessage());
        }
        
        return graph;
    }
}