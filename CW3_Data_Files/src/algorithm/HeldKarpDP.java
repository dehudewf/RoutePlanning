package src.algorithm;

import java.util.*;

/**
 * Implementation of PathFindingAlgorithm using Held-Karp dynamic programming algorithm
 * for solving TSP exactly. This algorithm is suitable for small to medium size problems
 * (up to 20 cities) where finding the exact optimal solution is desired.
 */
public class HeldKarpDP extends BasePathFindingAlgorithm {
    // Map of city indices for bit manipulation
    private Map<String, Integer> cityIndices;
    
    // List of cities for index to name conversion
    private List<String> cities;
    
    /**
     * Constructor
     * @param graph adjacency list representation of the graph
     */
    public HeldKarpDP(Map<String, Map<String, Integer>> graph) {
        super(graph);
    }
    
    /**
     * Get the name of the algorithm
     * @return algorithm name
     */
    @Override
    public String getName() {
        return "Held-Karp Dynamic Programming";
    }
    
    /**
     * Find path between cities using Held-Karp dynamic programming algorithm
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
            Result result = handleRoundTrip(startCity, citiesToVisit);
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

        // Use Held-Karp algorithm to solve the TSP problem
        Result result = solveWithHeldKarp(startCity, endCity, citiesToVisit);
        result.setAlgorithmName(getName());
        result.setComputationTime(System.nanoTime() - startTime);
        
        return result;
    }
    
    /**
     * Handle the case where start and end cities are the same (round trip)
     * @param startCity starting and ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and distance
     */
    private Result handleRoundTrip(String startCity, List<String> citiesToVisit) {
        // For round trips, we can use Held-Karp with the fixed starting point
        // We'll select intermediate cities and then return to start
        
        // Create list of cities without duplicates
        Set<String> uniqueCities = new HashSet<>(citiesToVisit);
        
        // Make sure start city is in the set
        uniqueCities.add(startCity);
        
        // Convert to list for indexing
        cities = new ArrayList<>(uniqueCities);
        
        // Create city indices map
        cityIndices = new HashMap<>();
        for (int i = 0; i < cities.size(); i++) {
            cityIndices.put(cities.get(i), i);
        }
        
        // Calculate distance matrix between all cities
        int n = cities.size();
        int[][] distanceMatrix = new int[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    String cityA = cities.get(i);
                    String cityB = cities.get(j);
                    ShortestPath.Result pathResult = shortestPath.findShortestPath(cityA, cityB);
                    distanceMatrix[i][j] = pathResult.getDistance();
                }
            }
        }
        
        // Solve TSP with Held-Karp algorithm
        int startIndex = cityIndices.get(startCity);
        int[][] dp = new int[1 << n][n];
        int[][] parent = new int[1 << n][n];
        
        // Initialize dp table
        for (int i = 0; i < (1 << n); i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE / 2); // Avoid overflow
            Arrays.fill(parent[i], -1);
        }
        
        // Base case: start city alone
        dp[1 << startIndex][startIndex] = 0;
        
        // Fill dp table
        for (int mask = 1; mask < (1 << n); mask++) {
            // Skip if start city is not in the subset
            if ((mask & (1 << startIndex)) == 0) continue;
            
            for (int i = 0; i < n; i++) {
                // Skip if city i is not in the current subset
                if ((mask & (1 << i)) == 0) continue;
                
                // If the subset contains only the city i
                if (mask == (1 << i)) continue;
                
                // Try all cities as the predecessor of i
                int prevMask = mask ^ (1 << i); // Remove city i from mask
                for (int j = 0; j < n; j++) {
                    // Skip if j is not in the previous subset
                    if ((prevMask & (1 << j)) == 0) continue;
                    
                    // Check if we can get a better path to i
                    if (dp[prevMask][j] + distanceMatrix[j][i] < dp[mask][i]) {
                        dp[mask][i] = dp[prevMask][j] + distanceMatrix[j][i];
                        parent[mask][i] = j;
                    }
                }
            }
        }
        
        // Calculate total distance for the round trip
        int finalMask = (1 << n) - 1; // All cities visited
        int minDistance = Integer.MAX_VALUE;
        int lastCity = -1;
        
        for (int i = 0; i < n; i++) {
            if (i == startIndex) continue;
            
            int totalDist = dp[finalMask][i] + distanceMatrix[i][startIndex];
            if (totalDist < minDistance) {
                minDistance = totalDist;
                lastCity = i;
            }
        }
        
        // If no valid path found
        if (lastCity == -1) {
            System.out.println("Cannot find a valid path connecting all specified cities");
            return new Result(new ArrayList<>(), -1);
        }
        
        // Reconstruct the path
        List<String> optimalPath = new ArrayList<>();
        
        // Add the start city (will be added again at the end)
        optimalPath.add(startCity);
        
        // Reconstruct the path from parent pointers
        int mask = finalMask;
        int current = lastCity;
        List<Integer> reversePath = new ArrayList<>();
        reversePath.add(current);
        
        while (current != startIndex) {
            int prev = parent[mask][current];
            reversePath.add(prev);
            mask = mask ^ (1 << current);
            current = prev;
        }
        
        // Reverse to get the correct order (excluding start)
        for (int i = reversePath.size() - 2; i >= 0; i--) {
            optimalPath.add(cities.get(reversePath.get(i)));
        }
        
        // Add start city again to complete the round trip
        optimalPath.add(startCity);
        
        // Expand the path to include intermediate cities along Dijkstra's shortest paths
        List<String> expandedPath = expandPath(optimalPath);
        
        return new Result(expandedPath, minDistance);
    }
    
    /**
     * Solve the routing problem using the Held-Karp dynamic programming algorithm
     * @param startCity starting city
     * @param endCity ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and distance
     */
    private Result solveWithHeldKarp(String startCity, String endCity, List<String> citiesToVisit) {
        // Create list of cities without duplicates
        Set<String> uniqueCities = new HashSet<>(citiesToVisit);
        
        // Make sure start and end cities are in the set
        uniqueCities.add(startCity);
        uniqueCities.add(endCity);
        
        // Convert to list for indexing
        cities = new ArrayList<>(uniqueCities);
        
        // Create city indices map
        cityIndices = new HashMap<>();
        for (int i = 0; i < cities.size(); i++) {
            cityIndices.put(cities.get(i), i);
        }
        
        // Calculate distance matrix between all cities
        int n = cities.size();
        int[][] distanceMatrix = new int[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    String cityA = cities.get(i);
                    String cityB = cities.get(j);
                    ShortestPath.Result pathResult = shortestPath.findShortestPath(cityA, cityB);
                    distanceMatrix[i][j] = pathResult.getDistance();
                }
            }
        }
        
        // Get indices for start and end cities
        int startIndex = cityIndices.get(startCity);
        int endIndex = cityIndices.get(endCity);
        
        // Solve TSP with Held-Karp but modified for fixed start and end points
        // We'll use a slightly different approach than for the round trip
        
        // We need to:
        // 1. Start at startCity
        // 2. Visit all intermediate cities exactly once
        // 3. End at endCity
        
        // We can model this by:
        // - Removing the edge from end to start city (since we don't need to return)
        // - Using the standard Held-Karp but with fixed start and last cities
        
        // Create dp table: dp[mask][city] = min cost to visit cities in mask ending at city
        int[][] dp = new int[1 << n][n];
        int[][] parent = new int[1 << n][n];
        
        // Initialize dp table
        for (int i = 0; i < (1 << n); i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE / 2);
            Arrays.fill(parent[i], -1);
        }
        
        // Base case: start at startCity
        dp[1 << startIndex][startIndex] = 0;
        
        // Fill dp table
        for (int mask = 1; mask < (1 << n); mask++) {
            // Skip if startCity is not in the subset
            if ((mask & (1 << startIndex)) == 0) continue;
            
            for (int i = 0; i < n; i++) {
                // Skip if city i is not in current subset
                if ((mask & (1 << i)) == 0) continue;
                
                // Skip if we're considering only the start city
                if (mask == (1 << i)) continue;
                
                // Get the subset without city i
                int prevMask = mask ^ (1 << i);
                
                // Try all possible previous cities
                for (int j = 0; j < n; j++) {
                    // Skip if j is not in the previous subset
                    if ((prevMask & (1 << j)) == 0) continue;
                    
                    // Calculate potential new distance
                    int newDist = dp[prevMask][j] + distanceMatrix[j][i];
                    
                    // Update if better
                    if (newDist < dp[mask][i]) {
                        dp[mask][i] = newDist;
                        parent[mask][i] = j;
                    }
                }
            }
        }
        
        // Calculate final result: all cities visited, ending at endCity
        int finalMask = (1 << n) - 1;
        int optimalDistance = dp[finalMask][endIndex];
        
        // If no valid path
        if (optimalDistance >= Integer.MAX_VALUE / 2) {
            System.out.println("Cannot find a path connecting all specified cities");
            return new Result(new ArrayList<>(), -1);
        }
        
        // Reconstruct the path
        List<String> optimalPath = new ArrayList<>();
        
        // Start with end city
        int current = endIndex;
        int mask = finalMask;
        
        // Build path in reverse
        while (current != -1) {
            optimalPath.add(cities.get(current));
            
            // If we've reached the start, we're done
            if (current == startIndex) break;
            
            // Move to previous city in optimal path
            int prev = parent[mask][current];
            mask = mask ^ (1 << current);
            current = prev;
        }
        
        // Reverse to get the correct order
        Collections.reverse(optimalPath);
        
        // Expand the path to include intermediate cities along Dijkstra's shortest paths
        List<String> expandedPath = expandPath(optimalPath);
        
        return new Result(expandedPath, optimalDistance);
    }
    
    /**
     * Expand a high-level path to include all intermediate cities along Dijkstra's shortest paths
     * @param highLevelPath list of main cities to visit in order
     * @return expanded path with all intermediate cities
     */
    private List<String> expandPath(List<String> highLevelPath) {
        List<String> expandedPath = new ArrayList<>();
        
        // Add first city
        expandedPath.add(highLevelPath.get(0));
        
        // Connect each pair of cities with shortest path
        for (int i = 0; i < highLevelPath.size() - 1; i++) {
            String current = highLevelPath.get(i);
            String next = highLevelPath.get(i + 1);
            
            // Get shortest path between these cities
            ShortestPath.Result pathResult = shortestPath.findShortestPath(current, next);
            List<String> connection = pathResult.getPath();
            
            // Skip the first city in the connection as it's already in the path
            for (int j = 1; j < connection.size(); j++) {
                expandedPath.add(connection.get(j));
            }
        }
        
        return expandedPath;
    }
} 