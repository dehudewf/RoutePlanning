package src.model;

/**
 * Attraction class, representing places users might want to visit
 */
public class Attraction {
    private String name;     // Attraction name
    private City location;   // City where the attraction is located
    
    /**
     * Constructor
     * @param name Attraction name
     * @param location City where the attraction is located
     */
    public Attraction(String name, City location) {
        this.name = name;
        this.location = location;
    }
    
    /**
     * Get attraction name
     * @return Attraction name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set attraction name
     * @param name Attraction name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get attraction location
     * @return City location
     */
    public City getLocation() {
        return location;
    }
    
    /**
     * Set attraction location
     * @param location City location
     */
    public void setLocation(City location) {
        this.location = location;
    }
    
    /**
     * Override equals method
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Attraction that = (Attraction) obj;
        
        if (!name.equals(that.name)) return false;
        return location.equals(that.location);
    }
    
    /**
     * Override hashCode method
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
    
    /**
     * Override toString method
     */
    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}