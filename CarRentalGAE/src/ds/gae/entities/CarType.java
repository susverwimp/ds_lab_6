package ds.gae.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;

@Entity
public class CarType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;
    private String name;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    //trunk space in liters
    private float trunkSpace;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Car> cars = new HashSet<Car>();
    
    /***************
	 * CONSTRUCTOR *
	 ***************/
    
    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }
    
    public CarType(){}
    
    public Key getId()
    {
        return id;
    }
    
    public void setId(Key id)
    {
        this.id = id;
    }

    public String getName() {
    	return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public void setNbOfSeats(int nbOfSeats)
    {
        this.nbOfSeats = nbOfSeats;
    }
    
    public boolean getSmokingAllowed() {
        return smokingAllowed;
    }
    
    public void setSmokingAllowed(boolean smokingAllowed)
    {
        this.smokingAllowed = smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    public void setRentalPricePerDay(double rentalPricePerDay)
    {
        this.rentalPricePerDay = rentalPricePerDay;
    }
    
    public float getTrunkSpace() {
    	return trunkSpace;
    }
    
    public void setTrunkSpace(float trunkSpace)
    {
        this.trunkSpace = trunkSpace;
    }
    
    public Set<Car> getCars()
    {
        return this.cars;
    }
    
    public void setCars(Set<Car> cars)
    {
        this.cars = cars;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), getSmokingAllowed(), getTrunkSpace());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarType other = (CarType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}