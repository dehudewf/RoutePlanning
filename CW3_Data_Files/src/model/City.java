package src.model;

/**
 * City class, representing nodes in the road network
 */
public class City {
    private String name;     // City name
    private String state;    // State abbreviation
    
    /**
     * Constructor
     * @param name City name
     * @param state State abbreviation
     */
    public City(String name, String state) {
        this.name = name;
        this.state = state;
    }
    
    /**
     * Get city name
     * @return City name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set city name
     * @param name City name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get state abbreviation
     * @return State abbreviation
     */
    public String getState() {
        return state;
    }
    
    /**
     * Set state abbreviation
     * @param state State abbreviation
     */
    public void setState(String state) {
        this.state = state;
    }
    
    /**
     * Get full city description (e.g. "New York NY")
     * @return Full city description
     */
    public String getFullName() {
        return name + " " + state;
    }
    
    /**
     * Override equals method
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        City city = (City) obj;
        
        if (!name.equals(city.name)) return false;
        return state.equals(city.state);
    }
    
    /**
     * Override hashCode method
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }
    
    /**
     * Override toString method
     */
    @Override
    public String toString() {
        return getFullName();
    }
}