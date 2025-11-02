package src.algorithm;

import java.util.*;

/**
 * Implementation of PathFindingAlgorithm using Dijkstra's algorithm for shortest paths
 * and Nearest Neighbor heuristic for TSP
 */
public class DijkstraTSP extends BasePathFindingAlgorithm {
    /**
     * Constructor
     * @param graph adjacency list representation of the graph
     */
    public DijkstraTSP(Map<String, Map<String, Integer>> graph) {
        super(graph);
    }
    
    /**
     * Get the name of the algorithm
     * @return algorithm name
     */
    @Override
    public String getName() {
        return "Dijkstra+TSP(Nearest Neighbor)";
    }
    
    /**
     * Find path between cities using Dijkstra's algorithm and Nearest Neighbor heuristic
     * @param startCity starting city
     * @param endCity ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and distance
     */
    @Override
    protected Result findPathInternal(String startCity, String endCity, List<String> citiesToVisit) {
        long startTime = System.nanoTime();
        
        // Special handling for the case where the start and end cities are the same
        if (startCity.equals(endCity)) {
            Result result = handleSameStartAndEnd(startCity, citiesToVisit);
            result.setAlgorithmName(getName());
            result.setComputationTime(System.nanoTime() - startTime);
            return result;
        }
        
        // If only start and end points, return the shortest path between them
        if (citiesToVisit.size() == 2) {
            ShortestPath.Result pathResult = shortestPath.findShortestPath(startCity, endCity);
            Result result = new Result(pathResult.getPath(), pathResult.getDistance(), 
                                      System.nanoTime() - startTime, getName());
            return result;
        }

        // Use nearest neighbor algorithm to solve the TSP problem
        Result result = solveWithNearestNeighbor(startCity, endCity, citiesToVisit);
        result.setAlgorithmName(getName());
        result.setComputationTime(System.nanoTime() - startTime);
        
        return result;
    }
    
    /**
     * Handle the case where start and end cities are the same
     * @param startCity starting and ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and distance
     */
    private Result handleSameStartAndEnd(String startCity, List<String> citiesToVisit) {
        List<String> result = new ArrayList<>();
        result.add(startCity);
        
        Set<String> visited = new HashSet<>();
        visited.add(startCity);
        
        String currentCity = startCity;
        
        // Visit all attraction cities, then return to the start/end
        while (visited.size() < citiesToVisit.size()) {
            // Find the nearest unvisited city
            String nearest = null;
            int minDistance = Integer.MAX_VALUE;
            
            for (String city : citiesToVisit) {
                if (!visited.contains(city)) {
                    ShortestPath.Result pathToCityResult = shortestPath.findShortestPath(currentCity, city);
                    if (pathToCityResult.getDistance() != -1 && pathToCityResult.getDistance() < minDistance) {
                        minDistance = pathToCityResult.getDistance();
                        nearest = city;
                    }
                }
            }
            
            // If no next city can be found, return an empty list
            if (nearest == null) {
                System.out.println("Cannot find a path connecting all specified cities");
                return new Result(new ArrayList<>(), -1);
            }
            
            // Add the path (except the first city, which is already in the result)
            ShortestPath.Result pathToNearest = shortestPath.findShortestPath(currentCity, nearest);
            List<String> nearestPath = pathToNearest.getPath();
            nearestPath.remove(0);
            result.addAll(nearestPath);
            
            currentCity = nearest;
            visited.add(nearest);
        }
        
        // All attraction cities have been visited, return to the start/end
        if (!currentCity.equals(startCity)) {
            ShortestPath.Result pathToStart = shortestPath.findShortestPath(currentCity, startCity);
            List<String> startPath = pathToStart.getPath();
            startPath.remove(0);
            result.addAll(startPath);
        }
        
        // Calculate total distance
        int totalDistance = calculateTotalDistance(result);
        
        return new Result(result, totalDistance);
    }
    
    /**
     * Solve the routing problem using the Nearest Neighbor algorithm
     * @param startCity starting city
     * @param endCity ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and distance
     */
    private Result solveWithNearestNeighbor(String startCity, String endCity, List<String> citiesToVisit) {
        List<String> result = new ArrayList<>();
        result.add(startCity);
        
        Set<String> visited = new HashSet<>();
        visited.add(startCity);
        
        String currentCity = startCity;
        
        // Visit all cities and finally reach the destination
        while (visited.size() < citiesToVisit.size() || !currentCity.equals(endCity)) {
            if (visited.size() == citiesToVisit.size() - 1 && !visited.contains(endCity)) {
                // If all other cities have been visited, only the destination remains
                ShortestPath.Result pathToEnd = shortestPath.findShortestPath(currentCity, endCity);
                List<String> endPath = pathToEnd.getPath();
                // Remove the first city as it's already in the result
                endPath.remove(0);
                result.addAll(endPath);
                currentCity = endCity;
                visited.add(endCity);
            } else {
                // Find the nearest unvisited city
                String nearest = null;
                int minDistance = Integer.MAX_VALUE;
                
                for (String city : citiesToVisit) {
                    if (!visited.contains(city) && !city.equals(endCity)) {
                        ShortestPath.Result pathToCityResult = shortestPath.findShortestPath(currentCity, city);
                        if (pathToCityResult.getDistance() != -1 && pathToCityResult.getDistance() < minDistance) {
                            minDistance = pathToCityResult.getDistance();
                            nearest = city;
                        }
                    }
                }
                
                // If no next city can be found, the graph might be disconnected
                if (nearest == null) {
                    System.out.println("Cannot find a path connecting all specified cities");
                    return new Result(new ArrayList<>(), -1);
                }
                
                // Add the path (except the first city, which is already in the result)
                ShortestPath.Result pathToNearest = shortestPath.findShortestPath(currentCity, nearest);
                List<String> nearestPath = pathToNearest.getPath();
                nearestPath.remove(0);
                result.addAll(nearestPath);
                
                currentCity = nearest;
                visited.add(nearest);
            }
        }
        
        // Calculate total distance
        int totalDistance = calculateTotalDistance(result);
        
        return new Result(result, totalDistance);
    }
    
    /**
     * Calculate total distance for a given path
     * @param path list of cities in the path
     * @return total distance (miles), or -1 if path is invalid
     */
    private int calculateTotalDistance(List<String> path) {
        int totalDistance = 0;
        
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            
            if (graph.containsKey(current) && graph.get(current).containsKey(next)) {
                totalDistance += graph.get(current).get(next);
            } else {
                // If there's no direct connection between two cities, return -1 to indicate invalid path
                return -1;
            }
        }
        
        return totalDistance;
    }
} 