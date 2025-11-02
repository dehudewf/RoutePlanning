package src.algorithm;

import java.util.*;

/**
 * Shortest path algorithm class, implements Dijkstra's algorithm
 */
public class ShortestPath {
    // Adjacency list representation of the graph
    private Map<String, Map<String, Integer>> graph;
    
    /**
     * Constructor
     * @param graph adjacency list representation of the graph
     */
    public ShortestPath(Map<String, Map<String, Integer>> graph) {
        this.graph = graph;
    }
    
    /**
     * Calculate shortest path between two cities using Dijkstra's algorithm
     * @param startCity starting city
     * @param endCity destination city
     * @return path and distance result
     */
    public Result findShortestPath(String startCity, String endCity) {
        // Priority queue, sorted by distance
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        
        // Store shortest distance to each city
        Map<String, Integer> distances = new HashMap<>();
        
        // Store path predecessor nodes
        Map<String, String> previous = new HashMap<>();
        
        // Initialize distances to infinity
        for (String city : graph.keySet()) {
            distances.put(city, Integer.MAX_VALUE);
        }
        
        // Starting city distance is 0
        distances.put(startCity, 0);
        pq.add(new Node(startCity, 0));
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            
            // If destination is found, end search
            if (current.city.equals(endCity)) {
                break;
            }
            
            // If current distance is greater than known shortest distance, skip
            if (current.distance > distances.get(current.city)) {
                continue;
            }
            
            // Check all adjacent cities
            for (Map.Entry<String, Integer> neighbor : graph.get(current.city).entrySet()) {
                String nextCity = neighbor.getKey();
                int newDistance = current.distance + neighbor.getValue();
                
                // If a shorter path is found
                if (newDistance < distances.get(nextCity)) {
                    distances.put(nextCity, newDistance);
                    previous.put(nextCity, current.city);
                    pq.add(new Node(nextCity, newDistance));
                }
            }
        }
        
        // Build path
        List<String> path = new ArrayList<>();
        String current = endCity;
        
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        
        // Reverse path, from start to end
        Collections.reverse(path);
        
        // If path is empty or first element is not the start city, no path was found
        if (path.isEmpty() || !path.get(0).equals(startCity)) {
            return new Result(new ArrayList<>(), -1);
        }
        
        return new Result(path, distances.get(endCity));
    }
    
    /**
     * Node class, used for Dijkstra's algorithm priority queue
     */
    private static class Node {
        String city;
        int distance;
        
        Node(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }
    }
    
    /**
     * Result class, containing path and total distance
     */
    public static class Result {
        private List<String> path;
        private int distance;
        
        public Result(List<String> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
        
        public List<String> getPath() {
            return path;
        }
        
        public int getDistance() {
            return distance;
        }
    }
}