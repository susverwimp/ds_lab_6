package ds.gae.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Reservation extends Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;
    private Key carId;
    
    /***************
	 * CONSTRUCTOR *
	 ***************/

    Reservation(Quote quote, Key carId) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    			quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }
    
    public Reservation()
    {
        super();
    }
    
    /******
     * ID *
     ******/
    
    public Key getCarId() {
    	return carId;
    }
    
    public Key getId()
    {
        return id;
    }
    
    public void setId(Key id)
    {
        this.id = id;
    }
    
    public void setCarId(Key carId)
    {
        this.carId = carId;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + carId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		Reservation other = (Reservation) obj;
		if (carId != other.carId)
			return false;
		return true;
	}
}