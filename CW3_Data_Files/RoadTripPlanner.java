import java.util.*;
import src.util.CSVReader;
import src.algorithm.ShortestPath;
import src.algorithm.PathFindingAlgorithm;
import src.algorithm.DijkstraTSP;
import src.algorithm.HeldKarpDP;
import src.algorithm.AStarTSP;
import src.model.City;

/**
 * US Road Trip Planning System Main Class
 */
public class RoadTripPlanner {
    // City network graph
    private Map<String, Map<String, Integer>> cityGraph;
    
    // Mapping from attractions to cities
    private Map<String, String> attractions;
    
    // List of path finding algorithms
    private List<PathFindingAlgorithm> algorithms;
    
    /**
     * Constructor, initialize the system
     */
    public RoadTripPlanner() {
        // Load data from CSV files
        cityGraph = CSVReader.readRoads("roads.csv");
        attractions = CSVReader.readAttractions("attractions.csv");
        
        // Initialize algorithms
        algorithms = new ArrayList<>();
        algorithms.add(new DijkstraTSP(cityGraph));
        algorithms.add(new HeldKarpDP(cityGraph));
        algorithms.add(new AStarTSP(cityGraph));
        // Additional algorithms can be added here
    }
    
    /**
     * Calculate route from starting city to ending city, via all specified attractions
     * @param startingCity starting city
     * @param endingCity ending city
     * @param attractionsList list of attractions
     * @return complete route of cities
     */
    public List<?> route(String startingCity, String endingCity, List<String> attractionsList) {
        // Convert attractions to cities
        List<String> citiesToVisit = convertAttractionsToCities(attractionsList);
        
        // Ensure starting and ending cities are in the list
        if (!citiesToVisit.contains(startingCity)) {
            citiesToVisit.add(0, startingCity);
        }
        if (!citiesToVisit.contains(endingCity)) {
            citiesToVisit.add(endingCity);
        }
        
        // Use the first algorithm by default (original behavior)
        PathFindingAlgorithm.Result result = algorithms.get(0).findPath(startingCity, endingCity, citiesToVisit);
        
        return convertStringListToCityList(result.getPath());
    }
    
    /**
     * Compare results from all algorithms
     * @param startingCity starting city
     * @param endingCity ending city
     * @param attractionsList list of attractions
     * @return list of results from each algorithm
     */
    public List<PathFindingAlgorithm.Result> compareAlgorithms(String startingCity, String endingCity, List<String> attractionsList) {
        // Convert attractions to cities
        List<String> citiesToVisit = convertAttractionsToCities(attractionsList);
        
        // Ensure starting and ending cities are in the list
        if (!citiesToVisit.contains(startingCity)) {
            citiesToVisit.add(0, startingCity);
        }
        if (!citiesToVisit.contains(endingCity)) {
            citiesToVisit.add(endingCity);
        }
        
        List<PathFindingAlgorithm.Result> results = new ArrayList<>();
        
        // Run each algorithm
        for (PathFindingAlgorithm algorithm : algorithms) {
            long startTime = System.nanoTime();
            PathFindingAlgorithm.Result result = algorithm.findPath(startingCity, endingCity, citiesToVisit);
            long endTime = System.nanoTime();
            
            // Set algorithm name and computation time if not already set
            if (result.getAlgorithmName().isEmpty()) {
                result.setAlgorithmName(algorithm.getName());
            }
            if (result.getComputationTime() == 0) {
                result.setComputationTime(endTime - startTime);
            }
            
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * Display results from algorithm comparison
     * @param results list of results to display
     */
    public void displayComparisonResults(List<PathFindingAlgorithm.Result> results) {
        System.out.println("\n=== Algorithm Comparison Results ===");
        for (PathFindingAlgorithm.Result result : results) {
            List<City> cityPath = convertStringListToCityList(result.getPath());
            int totalDistance = calculateTotalDistance(cityPath);
            
            System.out.println("Algorithm: " + result.getAlgorithmName());
            System.out.println("Distance: " + totalDistance + " miles");
            System.out.println("Computation time: " + result.getComputationTime() + " ns");
            
            if (cityPath.isEmpty() || totalDistance == -1) {
                System.out.println("Path: Could not find a valid path");
            } else {
                List<String> routeStrings = new ArrayList<>();
                for (City city : cityPath) {
                    routeStrings.add(city.toString());
                }
                System.out.println("Path: " + String.join(" -> ", routeStrings));
            }
            System.out.println("-----------------------");
        }
    }
    
    /**
     * Convert attractions to cities
     * @param attractionsList list of attractions
     * @return list of cities
     */
    private List<String> convertAttractionsToCities(List<String> attractionsList) {
        List<String> citiesToVisit = new ArrayList<>();
        for (String attraction : attractionsList) {
            if (attractions.containsKey(attraction)) {
                citiesToVisit.add(attractions.get(attraction));
            } else {
                System.out.println("Attraction '" + attraction + "' not found");
            }
        }
        return citiesToVisit;
    }
    
    /**
     * Convert a list of city strings to a list of City objects
     * @param stringList list of city strings in format "City State"
     * @return list of City objects
     */
    private List<City> convertStringListToCityList(List<String> stringList) {
        List<City> cityList = new ArrayList<>();
        for (String cityString : stringList) {
            String[] parts = cityString.split(" ");
            if (parts.length >= 2) {
                String state = parts[parts.length - 1];
                String name = cityString.substring(0, cityString.length() - state.length() - 1);
                cityList.add(new City(name, state));
            }
        }
        return cityList;
    }
    
    /**
     * Calculate total distance for a given path
     * @param path city path
     * @return total distance (miles)
     */
    public int calculateTotalDistance(List<City> path) {
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i).toString();
            String next = path.get(i + 1).toString();
            
            if (cityGraph.containsKey(current) && cityGraph.get(current).containsKey(next)) {
                totalDistance += cityGraph.get(current).get(next);
            } else {
                // If there's no direct connection between two cities, return -1 to indicate invalid path
                return -1;
            }
        }
        return totalDistance;
    }
    
    /**
     * Run test case
     */
    public void runTestCase(String startCity, String endCity, List<String> attractions) {
        System.out.println("\n=== Test Case ===");
        System.out.println("Start: " + startCity);
        System.out.println("End: " + endCity);
        if (attractions.isEmpty()) {
            System.out.println("No attractions");
        } else {
            System.out.println("Attractions to visit: " + String.join(", ", attractions));
        }
        
        // Compare algorithms
        List<PathFindingAlgorithm.Result> results = compareAlgorithms(startCity, endCity, attractions);
        displayComparisonResults(results);
    }
    
    /**
     * Check if a city exists in the road network
     * @param cityName city name
     * @return true if city exists, false otherwise
     */
    public boolean cityExists(String cityName) {
        return cityGraph.containsKey(cityName);
    }
    
    /**
     * Check if an attraction exists in the attractions list
     * @param attractionName attraction name
     * @return true if attraction exists, false otherwise
     */
    public boolean attractionExists(String attractionName) {
        return attractions.containsKey(attractionName);
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        RoadTripPlanner planner = new RoadTripPlanner();
        
        // Test case 1: Houston TX to Philadelphia PA (no attractions)
        planner.runTestCase("Houston TX", "Philadelphia PA", new ArrayList<>());
        
        // Test case 2: Philadelphia PA to San Antonio TX (via Hollywood Sign)
        List<String> attractions2 = new ArrayList<>();
        attractions2.add("Hollywood Sign");
        planner.runTestCase("Philadelphia PA", "San Antonio TX", attractions2);
        
        // Test case 3: San Jose CA to Phoenix AZ (via Liberty Bell and Millennium Park)
        List<String> attractions3 = new ArrayList<>();
        attractions3.add("Liberty Bell");
        attractions3.add("Millennium Park");
        planner.runTestCase("San Jose CA", "Phoenix AZ", attractions3);
        
        // Interactive mode
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== US Road Trip Planning System ===");
            System.out.println("1. Plan a trip");
            System.out.println("2. Exit");
            System.out.print("Choose: ");
            
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid choice, please enter a number.");
                scanner.nextLine();  // Consume invalid input
                continue;
            }
            
            if (choice == 1) {
                // Get starting city
                String startCity;
                while (true) {
                    System.out.print("Enter starting city (City Name State, e.g.: New York NY): ");
                    startCity = scanner.nextLine().trim();
                    if (planner.cityExists(startCity)) {
                        break;
                    } else {
                        System.out.println("City '" + startCity + "' not found in the road network. Please try again.");
                    }
                }
                
                // Get destination city
                String endCity;
                while (true) {
                    System.out.print("Enter destination city (City Name State, e.g.: Los Angeles CA): ");
                    endCity = scanner.nextLine().trim();
                    if (planner.cityExists(endCity)) {
                        break;
                    } else {
                        System.out.println("City '" + endCity + "' not found in the road network. Please try again.");
                    }
                }
                
                // Get number of attractions
                int attractionCount;
                while (true) {
                    System.out.print("Enter number of attractions to visit: ");
                    try {
                        attractionCount = scanner.nextInt();
                        scanner.nextLine();  // Consume newline
                        if (attractionCount >= 0) {
                            break;
                        } else {
                            System.out.println("Please enter a non-negative number.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine();  // Consume invalid input
                    }
                }
                
                List<String> attractionsList = new ArrayList<>();
                
                for (int i = 0; i < attractionCount; i++) {
                    String attraction;
                    while (true) {
                        System.out.print("Enter attraction " + (i+1) + " name: ");
                        attraction = scanner.nextLine().trim();
                        if (planner.attractionExists(attraction)) {
                            attractionsList.add(attraction);
                            break;
                        } else {
                            System.out.println("Attraction '" + attraction + "' not found. Please try again or enter 'skip' to skip this attraction.");
                            if (attraction.equalsIgnoreCase("skip")) {
                                i--; // Decrement counter to compensate for the skipped attraction
                                break;
                            }
                        }
                    }
                }
                
                planner.runTestCase(startCity, endCity, attractionsList);
            } else if (choice == 2) {
                System.out.println("Thank you for using the US Road Trip Planning System. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice, please try again.");
            }
        }
        
        scanner.close();
    }
}