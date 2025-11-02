import java.util.*;

/**
 * Test program for the new algorithm architecture
 */
public class RunAlgorithmTest {
    public static void main(String[] args) {
        // Create a new RoadTripPlanner instance
        RoadTripPlanner planner = new RoadTripPlanner();
        
        System.out.println("US Road Trip Planner - Algorithm Test");
        System.out.println("=====================================");
        
        // Test case 1: Simple path - Houston TX to Philadelphia PA (no attractions)
        System.out.println("\nTest Case 1: Houston TX to Philadelphia PA (direct path)");
        planner.runTestCase("Houston TX", "Philadelphia PA", new ArrayList<>());
        
        // Test case 2: With one attraction - Philadelphia PA to San Antonio TX via Hollywood Sign
        System.out.println("\nTest Case 2: Philadelphia PA to San Antonio TX via Hollywood Sign");
        List<String> attractions2 = new ArrayList<>();
        attractions2.add("Hollywood Sign");
        planner.runTestCase("Philadelphia PA", "San Antonio TX", attractions2);
        
        // Test case 3: Multiple attractions - San Jose CA to Phoenix AZ via multiple attractions
        System.out.println("\nTest Case 3: San Jose CA to Phoenix AZ via multiple attractions");
        List<String> attractions3 = new ArrayList<>();
        attractions3.add("Liberty Bell");
        attractions3.add("Millennium Park");
        planner.runTestCase("San Jose CA", "Phoenix AZ", attractions3);
        
        // Test case 4: Round trip - Boston MA to Boston MA via Mount Rushmore
        System.out.println("\nTest Case 4: Round trip from Boston MA to Boston MA via Mount Rushmore");
        List<String> attractions4 = new ArrayList<>();
        attractions4.add("Mount Rushmore");
        planner.runTestCase("Boston MA", "Boston MA", attractions4);
    }
} 