package src.algorithm;

import java.util.*;

/**
 * Interface for path finding algorithms
 * Allows different implementations of routing algorithms
 */
public interface PathFindingAlgorithm {
    /**
     * Get the name of the algorithm
     * @return algorithm name
     */
    String getName();
    
    /**
     * Find path between cities
     * @param startCity starting city
     * @param endCity ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and additional information
     */
    Result findPath(String startCity, String endCity, List<String> citiesToVisit);
    
    /**
     * Result class, containing path information and metrics
     */
    public static class Result {
        private List<String> path;
        private int distance;
        private long computationTime;
        private String algorithmName;
        
        public Result(List<String> path, int distance) {
            this.path = path;
            this.distance = distance;
            this.computationTime = 0;
            this.algorithmName = "";
        }
        
        public Result(List<String> path, int distance, long computationTime, String algorithmName) {
            this.path = path;
            this.distance = distance;
            this.computationTime = computationTime;
            this.algorithmName = algorithmName;
        }
        
        public List<String> getPath() {
            return path;
        }
        
        public int getDistance() {
            return distance;
        }
        
        public long getComputationTime() {
            return computationTime;
        }
        
        public String getAlgorithmName() {
            return algorithmName;
        }
        
        public void setComputationTime(long computationTime) {
            this.computationTime = computationTime;
        }
        
        public void setAlgorithmName(String algorithmName) {
            this.algorithmName = algorithmName;
        }
    }
} 