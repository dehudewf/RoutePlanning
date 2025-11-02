import java.util.*;
import src.algorithm.PathFindingAlgorithm;

/**
 * Test program for comparing different path finding algorithms
 */
public class CompareAlgorithms {
    public static void main(String[] args) {
        // Create a new RoadTripPlanner instance
        RoadTripPlanner planner = new RoadTripPlanner();
        
        System.out.println("US Road Trip Planner - Algorithm Comparison");
        System.out.println("==========================================");
        
        // Test Case 1: Simple direct path
        runTest(planner, "Test 1: Simple direct path", 
                "Houston TX", "Philadelphia PA", new ArrayList<>());
        
        // Test Case 2: Path with one attraction
        List<String> attractions2 = new ArrayList<>();
        attractions2.add("Hollywood Sign");
        runTest(planner, "Test 2: Path with one attraction",
                "Philadelphia PA", "San Antonio TX", attractions2);
        
        // Test Case 3: Path with multiple attractions
        List<String> attractions3 = new ArrayList<>();
        attractions3.add("Liberty Bell");
        attractions3.add("Millennium Park");
        attractions3.add("Hollywood Sign");
        runTest(planner, "Test 3: Path with multiple attractions",
                "San Jose CA", "Phoenix AZ", attractions3);
        
        // Test Case 4: Round trip 
        List<String> attractions4 = new ArrayList<>();
        attractions4.add("Statue of Liberty");
        attractions4.add("The Sixth Floor Museum");
        runTest(planner, "Test 4: Round trip",
                "Chicago IL", "Chicago IL", attractions4);
        
        // Test Case 5: Complex routing scenario
        List<String> attractions5 = new ArrayList<>();
        attractions5.add("Liberty Bell");
        attractions5.add("The Sixth Floor Museum");
        attractions5.add("Hollywood Sign");
        attractions5.add("Millennium Park");
        attractions5.add("Texas State Capitol");
        attractions5.add("NASCAR Hall of Fame");
        attractions5.add("Balboa Park");
        attractions5.add("The Alamo");
        attractions5.add("NASA Space Center");
        attractions5.add("Jacksonville Zoo and Gardens");
        runTest(planner, "Test 5: Complex routing scenario (many cities)",
                "New York NY", "Charlotte NC", attractions5);
    }
    
    /**
     * Run a test case with all algorithms and display the comparison
     * @param planner the RoadTripPlanner instance
     * @param testName name of the test case
     * @param startCity starting city
     * @param endCity ending city
     * @param attractions list of attractions to visit
     */
    private static void runTest(RoadTripPlanner planner, String testName, 
                                String startCity, String endCity, List<String> attractions) {
        System.out.println("\n=== " + testName + " ===");
        System.out.println("Start: " + startCity);
        System.out.println("End: " + endCity);
        if (attractions.isEmpty()) {
            System.out.println("No attractions");
        } else {
            System.out.println("Attractions to visit: " + String.join(", ", attractions));
        }
        
        // Compare algorithms
        List<PathFindingAlgorithm.Result> results = planner.compareAlgorithms(startCity, endCity, attractions);
        
        // Create a summary of the comparison
        System.out.println("\nResults Summary:");
        System.out.println("----------------------------------");
        System.out.println(String.format("%-30s | %-15s | %-15s", "Algorithm", "Distance (miles)", "Time (ns)"));
        System.out.println("----------------------------------");
        
        for (PathFindingAlgorithm.Result result : results) {
            String distStr = result.getDistance() == -1 ? "No valid path" : String.valueOf(result.getDistance());
            System.out.println(String.format("%-30s | %-15s | %-15d", 
                    result.getAlgorithmName(), distStr, result.getComputationTime()));
        }
        
        // Find best algorithm in terms of distance
        PathFindingAlgorithm.Result bestDistanceResult = null;
        for (PathFindingAlgorithm.Result result : results) {
            if (result.getDistance() != -1) {
                if (bestDistanceResult == null || result.getDistance() < bestDistanceResult.getDistance()) {
                    bestDistanceResult = result;
                }
            }
        }
        
        // Find fastest algorithm in terms of computation time
        PathFindingAlgorithm.Result fastestResult = null;
        for (PathFindingAlgorithm.Result result : results) {
            if (result.getDistance() != -1) {
                if (fastestResult == null || result.getComputationTime() < fastestResult.getComputationTime()) {
                    fastestResult = result;
                }
            }
        }
        
        System.out.println("----------------------------------");
        if (bestDistanceResult != null) {
            System.out.println("Best distance: " + bestDistanceResult.getAlgorithmName() + 
                    " (" + bestDistanceResult.getDistance() + " miles)");
        }
        if (fastestResult != null) {
            System.out.println("Fastest algorithm: " + fastestResult.getAlgorithmName() + 
                    " (" + fastestResult.getComputationTime() + " ns)");
        }
        
        // Display detailed results if requested
        // planner.displayComparisonResults(results);
        
        System.out.println("\n");
    }
} 