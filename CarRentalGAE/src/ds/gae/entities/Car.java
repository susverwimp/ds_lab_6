package ds.gae.entities;

import java.util.Date;
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
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Reservation> reservations = new HashSet<Reservation>();

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(){}

    /******
     * ID *
     ******/
    
    public Key getId() {
    	return id;
    }
    
    public void setId(Key id)
    {
        this.id = id;
    }

    /****************
     * RESERVATIONS *
     ****************/
    
    public Set<Reservation> getReservations() {
    	return reservations;
    }
    
    public void setReservations(Set<Reservation> reservations)
    {
        this.reservations = reservations;
    }

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
}