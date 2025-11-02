package src.algorithm;

import java.util.*;

/**
 * Abstract base class for path finding algorithms
 * Provides common functionality for path finding algorithms
 */
public abstract class BasePathFindingAlgorithm implements PathFindingAlgorithm {
    // Adjacency list representation of the graph
    protected Map<String, Map<String, Integer>> graph;
    
    // Shortest path algorithm for city-to-city routes
    protected ShortestPath shortestPath;
    
    /**
     * Constructor
     * @param graph adjacency list representation of the graph
     */
    public BasePathFindingAlgorithm(Map<String, Map<String, Integer>> graph) {
        this.graph = graph;
        this.shortestPath = new ShortestPath(graph);
    }
    
    /**
     * Execute the algorithm with time measurement
     * @param startCity starting city
     * @param endCity ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and additional information
     */
    @Override
    public Result findPath(String startCity, String endCity, List<String> citiesToVisit) {
        long startTime = System.nanoTime();
        Result result = findPathInternal(startCity, endCity, citiesToVisit);
        long endTime = System.nanoTime();
        
        // Set algorithm name and computation time
        result.setAlgorithmName(getName());
        result.setComputationTime(endTime - startTime);
        
        return result;
    }
    
    /**
     * Internal implementation of the path finding algorithm
     * @param startCity starting city
     * @param endCity ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and distance
     */
    protected abstract Result findPathInternal(String startCity, String endCity, List<String> citiesToVisit);
} 