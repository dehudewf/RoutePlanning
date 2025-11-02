package src.algorithm;

import java.util.*;

/**
 * Implementation of PathFindingAlgorithm using A* algorithm for solving TSP.
 * This algorithm combines Dijkstra with a heuristic function to more efficiently
 * explore the solution space while still guaranteeing an optimal solution.
 */
public class AStarTSP extends BasePathFindingAlgorithm {
    // Distance matrix between all cities
    private int[][] distanceMatrix;
    
    // List of cities
    private List<String> cities;
    
    // Map city names to indices
    private Map<String, Integer> cityIndices;
    
    /**
     * Constructor
     * @param graph adjacency list representation of the graph
     */
    public AStarTSP(Map<String, Map<String, Integer>> graph) {
        super(graph);
    }
    
    /**
     * Get the name of the algorithm
     * @return algorithm name
     */
    @Override
    public String getName() {
        return "A* Search TSP";
    }
    
    /**
     * Find path between cities using A* search algorithm
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

        // Use A* algorithm to solve the TSP problem
        Result result = solveWithAStar(startCity, endCity, citiesToVisit);
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
        // For round trips, we'll solve it as a standard round-trip TSP problem
        // Create list of cities without duplicates
        Set<String> uniqueCities = new HashSet<>(citiesToVisit);
        uniqueCities.add(startCity);
        List<String> citiesList = new ArrayList<>(uniqueCities);
        
        // Get all cities that need to be visited
        int n = citiesList.size();
        
        // Special case: only one city (the starting point)
        if (n == 1) {
            List<String> path = new ArrayList<>();
            path.add(startCity);
            return new Result(path, 0);
        }
        
        // Special case: round trip with only two cities
        if (n == 2) {
            String otherCity = null;
            for (String city : citiesList) {
                if (!city.equals(startCity)) {
                    otherCity = city;
                    break;
                }
            }
            
            ShortestPath.Result toOther = shortestPath.findShortestPath(startCity, otherCity);
            ShortestPath.Result backToStart = shortestPath.findShortestPath(otherCity, startCity);
            
            List<String> path = new ArrayList<>();
            path.addAll(toOther.getPath());
            // Skip duplicate otherCity
            path.addAll(backToStart.getPath().subList(1, backToStart.getPath().size()));
            
            return new Result(path, toOther.getDistance() + backToStart.getDistance());
        }
        
        // Normal case: round trip TSP problem
        return solveWithAStar(startCity, startCity, citiesList);
    }
    
    /**
     * Solve the routing problem using the A* search algorithm
     * @param startCity starting city
     * @param endCity ending city
     * @param citiesToVisit list of cities that must be visited
     * @return result containing the path and distance
     */
    private Result solveWithAStar(String startCity, String endCity, List<String> citiesToVisit) {
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
        distanceMatrix = new int[n][n];
        
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
        
        // Initialize the A* search
        PriorityQueue<SearchNode> openSet = new PriorityQueue<>();
        Map<SearchState, SearchNode> stateMap = new HashMap<>();
        
        // Create initial state: only start city visited
        int initialMask = 1 << startIndex;
        SearchState initialState = new SearchState(initialMask, startIndex);
        SearchNode initialNode = new SearchNode(
            initialState,
            0, // g(n) = 0 for start node
            calculateImprovedHeuristic(initialMask, startIndex, endIndex), // h(n)
            null, // no parent
            startIndex // current city
        );
        
        openSet.add(initialNode);
        stateMap.put(initialState, initialNode);
        
        // Target state: all cities visited, ending at endIndex
        int targetMask = (1 << n) - 1; // all cities visited
        
        // A* search loop
        while (!openSet.isEmpty()) {
            // Get node with lowest f(n) = g(n) + h(n)
            SearchNode current = openSet.poll();
            
            // If we've found the target state
            if (current.state.mask == targetMask && 
                (current.currentCity == endIndex || (startIndex == endIndex && Integer.bitCount(current.state.mask) == n))) {
                // Reconstruct the path
                return reconstructPath(current, startCity, endCity);
            }
            
            // Explore all possible next cities
            for (int nextCity = 0; nextCity < n; nextCity++) {
                // Skip if already visited or it's the same city
                if ((current.state.mask & (1 << nextCity)) != 0 || nextCity == current.currentCity) {
                    continue;
                }
                
                // Calculate cost to move to next city
                int moveCost = distanceMatrix[current.currentCity][nextCity];
                int newCost = current.g + moveCost;
                
                // Create new state
                int newMask = current.state.mask | (1 << nextCity);
                SearchState newState = new SearchState(newMask, nextCity);
                
                // Calculate heuristic using improved function
                int heuristic = calculateImprovedHeuristic(newMask, nextCity, endIndex);
                
                // Check if we've found a better path to this state
                SearchNode existingNode = stateMap.get(newState);
                if (existingNode == null || newCost < existingNode.g) {
                    SearchNode newNode = new SearchNode(
                        newState,
                        newCost,
                        heuristic,
                        current,
                        nextCity
                    );
                    
                    // Add to open set and update state map
                    if (existingNode != null) {
                        openSet.remove(existingNode);
                    }
                    openSet.add(newNode);
                    stateMap.put(newState, newNode);
                }
            }
            
            // Special case: If we've visited all cities except end city,
            // consider going directly to the end
            if (Integer.bitCount(current.state.mask) == n - 1 && 
                (current.state.mask & (1 << endIndex)) == 0) {
                
                int moveCost = distanceMatrix[current.currentCity][endIndex];
                int newCost = current.g + moveCost;
                
                int newMask = current.state.mask | (1 << endIndex);
                SearchState newState = new SearchState(newMask, endIndex);
                
                SearchNode existingNode = stateMap.get(newState);
                if (existingNode == null || newCost < existingNode.g) {
                    SearchNode newNode = new SearchNode(
                        newState,
                        newCost,
                        0, // heuristic is 0 since we reached the goal
                        current,
                        endIndex
                    );
                    
                    if (existingNode != null) {
                        openSet.remove(existingNode);
                    }
                    openSet.add(newNode);
                    stateMap.put(newState, newNode);
                }
            }
        }
        
        // If we get here, no path found
        System.out.println("No valid path found connecting all specified cities");
        return new Result(new ArrayList<>(), -1);
    }
    
    /**
     * Calculate improved heuristic for A* search
     * Simpler and more efficient than full MST calculation
     * @param mask bitmask of visited cities
     * @param currentCity current city index
     * @param endIndex end city index
     * @return heuristic value
     */
    private int calculateImprovedHeuristic(int mask, int currentCity, int endIndex) {
        int n = cities.size();
        
        // Find unvisited cities
        List<Integer> unvisitedCities = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) == 0) {
                unvisitedCities.add(i);
            }
        }
        
        // If all cities are visited, heuristic is just the distance to end city
        if (unvisitedCities.isEmpty()) {
            return distanceMatrix[currentCity][endIndex];
        }
        
        // Calculate minimum distance from current city to any unvisited city
        int minFromCurrent = Integer.MAX_VALUE;
        for (int city : unvisitedCities) {
            minFromCurrent = Math.min(minFromCurrent, distanceMatrix[currentCity][city]);
        }
        
        // Calculate minimum distance from any unvisited city to the end city
        int minToEnd = Integer.MAX_VALUE;
        if (!unvisitedCities.contains(endIndex)) {
            for (int city : unvisitedCities) {
                minToEnd = Math.min(minToEnd, distanceMatrix[city][endIndex]);
            }
        } else {
            // If end city is among unvisited cities, set to 0 (will be handled in city connections)
            minToEnd = 0;
        }
        
        // Calculate minimum distance between any two unvisited cities
        int minBetweenUnvisited = Integer.MAX_VALUE;
        if (unvisitedCities.size() > 1) {
            for (int i = 0; i < unvisitedCities.size(); i++) {
                for (int j = i + 1; j < unvisitedCities.size(); j++) {
                    int city1 = unvisitedCities.get(i);
                    int city2 = unvisitedCities.get(j);
                    minBetweenUnvisited = Math.min(minBetweenUnvisited, 
                                                  distanceMatrix[city1][city2]);
                }
            }
        } else {
            minBetweenUnvisited = 0; // Only one unvisited city, no need to calculate distances between cities
        }
        
        // Calculate heuristic value
        int heuristic;
        
        if (unvisitedCities.size() == 1) {
            // If only one unvisited city
            if (unvisitedCities.get(0) == endIndex) {
                // If it's the end city, heuristic is just the distance to end
                heuristic = distanceMatrix[currentCity][endIndex];
            } else {
                // If not the end city, heuristic is distance to unvisited city plus distance from that city to end
                int onlyCity = unvisitedCities.get(0);
                heuristic = distanceMatrix[currentCity][onlyCity] + distanceMatrix[onlyCity][endIndex];
            }
        } else {
            // Multiple unvisited cities
            // 1. Distance from current city to nearest unvisited city
            heuristic = minFromCurrent;
            
            // 2. Minimum spanning tree approximation for unvisited cities
            // Use minimum distance multiplied by number of edges needed (n-1)
            heuristic += minBetweenUnvisited * (unvisitedCities.size() - 1);
            
            // 3. If end city is not in unvisited list, add distance from some unvisited city to end
            if (!unvisitedCities.contains(endIndex)) {
                heuristic += minToEnd;
            }
        }
        
        // Ensure heuristic doesn't overestimate actual cost (admissibility)
        // Apply 0.95 factor to guarantee admissibility
        return (int)(heuristic * 0.95);
    }
    
    /**
     * Original MST-based heuristic (kept for reference)
     */
    private int calculateMSTHeuristic(int mask, int currentCity, int endIndex) {
        int n = cities.size();
        
        // If all cities are visited, heuristic is just the distance to end city
        if (Integer.bitCount(mask) == n) {
            return distanceMatrix[currentCity][endIndex];
        }
        
        // Find unvisited cities
        List<Integer> unvisitedCities = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) == 0) {
                unvisitedCities.add(i);
            }
        }
        
        // If only one city is unvisited, it must be the end city
        if (unvisitedCities.size() == 1) {
            return distanceMatrix[currentCity][unvisitedCities.get(0)];
        }
        
        // Calculate MST of unvisited cities + current city using Prim's algorithm
        int totalMSTCost = 0;
        boolean[] inMST = new boolean[n];
        int[] minEdge = new int[n];
        Arrays.fill(minEdge, Integer.MAX_VALUE);
        
        // Start with lowest cost edge from current city to any unvisited city
        int minToUnvisited = Integer.MAX_VALUE;
        for (int city : unvisitedCities) {
            minToUnvisited = Math.min(minToUnvisited, distanceMatrix[currentCity][city]);
        }
        
        // Add min edge from current to MST cost
        totalMSTCost += minToUnvisited;
        
        // Now calculate MST of remaining unvisited cities
        if (unvisitedCities.size() > 1) {
            // Use first unvisited city as start for MST calculation
            int start = unvisitedCities.get(0);
            minEdge[start] = 0;
            
            for (int i = 0; i < unvisitedCities.size(); i++) {
                // Find minimum edge
                int minVertex = -1;
                int minCost = Integer.MAX_VALUE;
                
                for (int city : unvisitedCities) {
                    if (!inMST[city] && minEdge[city] < minCost) {
                        minVertex = city;
                        minCost = minEdge[city];
                    }
                }
                
                // If no vertex found, all remaining are disconnected
                if (minVertex == -1) break;
                
                // Add to MST
                inMST[minVertex] = true;
                totalMSTCost += minCost;
                
                // Update min edges
                for (int city : unvisitedCities) {
                    if (!inMST[city] && distanceMatrix[minVertex][city] < minEdge[city]) {
                        minEdge[city] = distanceMatrix[minVertex][city];
                    }
                }
            }
        }
        
        // Also add minimum edge to connect to end city if it's not already visited
        if ((mask & (1 << endIndex)) == 0) {
            int minToEnd = Integer.MAX_VALUE;
            for (int city : unvisitedCities) {
                if (city != endIndex) { // Skip end city itself
                    minToEnd = Math.min(minToEnd, distanceMatrix[city][endIndex]);
                }
            }
            // If end city is not alone
            if (unvisitedCities.size() > 1) {
                totalMSTCost += minToEnd;
            }
        }
        
        return totalMSTCost;
    }
    
    /**
     * Reconstruct path from A* search result
     * @param finalNode final search node from A* search
     * @param startCity starting city
     * @param endCity ending city
     * @return result containing the path and distance
     */
    private Result reconstructPath(SearchNode finalNode, String startCity, String endCity) {
        List<Integer> cityIndicesPath = new ArrayList<>();
        int distance = finalNode.g;
        
        // Build path in reverse
        SearchNode current = finalNode;
        while (current != null) {
            cityIndicesPath.add(current.currentCity);
            current = current.parent;
        }
        
        // Reverse to get correct order
        Collections.reverse(cityIndicesPath);
        
        // Convert indices to city names
        List<String> path = new ArrayList<>();
        for (int cityIndex : cityIndicesPath) {
            path.add(cities.get(cityIndex));
        }
        
        // Handle round trip: if start and end cities are the same but the last city in path is not the start city, add it
        if (startCity.equals(endCity) && !path.get(path.size() - 1).equals(startCity)) {
            path.add(startCity);
            
            // Update distance by adding the distance from the last city to the start city
            int lastCityIndex = cityIndices.get(path.get(path.size() - 2));
            int startCityIndex = cityIndices.get(startCity);
            distance += distanceMatrix[lastCityIndex][startCityIndex];
        }
        
        // Expand the path to include intermediate cities
        List<String> expandedPath = expandPath(path);
        
        return new Result(expandedPath, distance);
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
    
    /**
     * Class representing the state in A* search
     */
    private static class SearchState {
        // Bitmask representing visited cities
        final int mask;
        // Current city index
        final int currentCity;
        
        SearchState(int mask, int currentCity) {
            this.mask = mask;
            this.currentCity = currentCity;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchState that = (SearchState) o;
            return mask == that.mask && currentCity == that.currentCity;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(mask, currentCity);
        }
    }
    
    /**
     * Class representing a node in the A* search
     */
    private static class SearchNode implements Comparable<SearchNode> {
        // Current state
        final SearchState state;
        // g(n): cost so far
        final int g;
        // h(n): heuristic estimate to goal
        final int h;
        // Parent node
        final SearchNode parent;
        // Current city index
        final int currentCity;
        
        SearchNode(SearchState state, int g, int h, SearchNode parent, int currentCity) {
            this.state = state;
            this.g = g;
            this.h = h;
            this.parent = parent;
            this.currentCity = currentCity;
        }
        
        // f(n) = g(n) + h(n)
        int f() {
            return g + h;
        }
        
        @Override
        public int compareTo(SearchNode other) {
            return Integer.compare(this.f(), other.f());
        }
    }
} 