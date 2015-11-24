package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;
 
public class CarRentalModel {
		
	private static CarRentalModel instance;
    private EntityManager em;

	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}
	
	private EntityManager getEntityManager()
    {
        if(em == null || !em.isOpen())
        {
            em = EMF.get().createEntityManager();
        }
        return em;
    }
		
	/**
	 * Get the car types available in the given car rental company.
	 *
	 * @param 	crcName
	 * 			the car rental company
	 * @return	The list of car types (i.e. name of car type), available
	 * 			in the given car rental company.
	 */
	@SuppressWarnings("unchecked")
    public Set<String> getCarTypesNames(String company) 
	{
	    try
	    {
            Set<String> out = new HashSet<>();
            Query query = getEntityManager().createQuery("SELECT company.carTypes FROM CarRentalCompany company WHERE company.name = :name");
            query.setParameter("name", company);
            List<Set<CarType>> resultList = query.getResultList();
            if(resultList.size() > 0)
            {
                Set<CarType> types = resultList.get(0);
                for (CarType type: types)
                    out.add(type.getName());
            }
            return out;
        } 
	    finally 
	    {
            getEntityManager().close();
        }
	}

    /**
     * Get all registered car rental companies
     *
     * @return	the list of car rental companies
     */
    public Collection<String> getAllRentalCompanyNames() 
    {
        try
        {
            TypedQuery<String> query = getEntityManager().createQuery("SELECT company.name FROM CarRentalCompany company", String.class);
            List<String> resultList = query.getResultList();
            return resultList;
        } 
        finally 
        {
            getEntityManager().close();
        }
    }
	
	/**
	 * Create a quote according to the given reservation constraints (tentative reservation).
	 * 
	 * @param	company
	 * 			name of the car renter company
	 * @param	renterName 
	 * 			name of the car renter 
	 * @param 	constraints
	 * 			reservation constraints for the quote
	 * @return	The newly created quote.
	 *  
	 * @throws ReservationException
	 * 			No car available that fits the given constraints.
	 */
    public Quote createQuote(String company, String renterName, ReservationConstraints constraints) throws ReservationException 
    {    	
        try
        {
            TypedQuery<CarRentalCompany> query = getEntityManager().createQuery("SELECT company FROM CarRentalCompany company WHERE company.name = :name", CarRentalCompany.class);
            query.setParameter("name", company);
            List<CarRentalCompany> resultList = query.getResultList();
        	Quote out = null;
    
            if (resultList.size() > 0) 
            {
                out = resultList.get(0).createQuote(constraints, renterName);
            } 
            else 
            {
            	throw new ReservationException("CarRentalCompany '" + company + "' not found.");    	
            }
            
            return out;
        }
        finally
        {
            getEntityManager().close();
        }
    }
    
	/**
	 * Confirm the given quote.
	 *
	 * @param 	q
	 * 			Quote to confirm
	 * 
	 * @throws ReservationException
	 * 			Confirmation of given quote failed.	
	 */
	public Reservation confirmQuote(Quote quote) throws ReservationException 
	{
	    EntityTransaction transaction = getEntityManager().getTransaction();
        try 
        {
            transaction.begin();
            CarRentalCompany company = getEntityManager().find(CarRentalCompany.class, quote.getRentalCompany());
            Reservation reservation = company.confirmQuote(quote);
            transaction.commit();
            return reservation;
        } 
        catch(ReservationException e) 
        {
            transaction.rollback();
            throw e;
        } 
        finally 
        {
            getEntityManager().close();
        }   
	}
	
    /**
	 * Confirm the given list of quotes
	 * 
	 * @param 	quotes 
	 * 			the quotes to confirm
	 * @return	The list of reservations, resulting from confirming all given quotes.
	 * 
	 * @throws 	ReservationException
	 * 			One of the quotes cannot be confirmed. 
	 * 			Therefore none of the given quotes is confirmed.
	 */
    public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException 
    {    	
        // TODO: implement a more efficient way to batch process these quotes per company
		List<Reservation> reservations = new ArrayList<>();
		try
		{
		    for(Quote quote : quotes)
		    {
		        reservations.add(confirmQuote(quote));
		    }
		    return reservations;
		}
		catch(ReservationException ex)
		{
		    EntityTransaction transaction = getEntityManager().getTransaction();
            transaction.begin();
		    for(Reservation reservation : reservations)
		    {
	            CarRentalCompany company = getEntityManager().find(CarRentalCompany.class, reservation.getRentalCompany());
	            company.cancelReservation(reservation);
		    }
            transaction.commit();
		    throw ex;
		}
		finally
		{
		    getEntityManager().close();
		}
    }
	
	/**
	 * Get all reservations made by the given car renter.
	 *
	 * @param 	renter
	 * 			name of the car renter
	 * @return	the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) 
	{
		
		List<Reservation> out = new ArrayList<Reservation>();
        try
        {
            TypedQuery<Reservation> query = getEntityManager().createQuery("SELECT reservation FROM Reservation reservation WHERE reservation.carRenter = :renter", Reservation.class);
            query.setParameter("renter", renter);
            out.addAll(query.getResultList());
            return out;
        } 
        finally 
        {
            getEntityManager().close();
        }

    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param 	crcName
     * 			the given car rental company
     * @return	The list of car types in the given car rental company.
     */
    @SuppressWarnings("unchecked")
    public Collection<CarType> getCarTypesOfCarRentalCompany(String company) 
    {
        try
        {
            Collection<CarType> carTypes = new HashSet<>();
            Query query = getEntityManager().createQuery("SELECT company.carTypes FROM CarRentalCompany company WHERE company.name = :name");
            query.setParameter("name", company);
            List<Collection<CarType>> resultList = query.getResultList();
            if(resultList.size() > 0)
            {
                carTypes.addAll(resultList.get(0));
            }
            return carTypes;
        } 
        finally 
        {
            getEntityManager().close();
        }
    }
	
    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A list of car IDs of cars with the given car type.
     */
    public Collection<Integer> getCarIdsByCarType(String crcName, CarType carType) 
    {
        // TODO: find a way to safely cast Long to int without trespassing the bounds
    	Collection<Integer> out = new ArrayList<>();
    	for (Car c : getCarsByCarType(crcName, carType)) 
    	{
    		out.add(c.getId().hashCode());
    	}
    	return out;
    }
    
    /**
     * Get the amount of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A number, representing the amount of cars of the given car type.
     */
    public int getAmountOfCarsByCarType(String crcName, CarType carType) {
    	return this.getCarsByCarType(crcName, carType).size();
    }

	/**
	 * Get the list of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	List of cars of the given car type
	 */
	@SuppressWarnings("unchecked")
    private List<Car> getCarsByCarType(String company, CarType carType) 
	{				
	    // TODO: find a better way to access car types -> cars
		List<Car> out = new ArrayList<Car>(); 
        Collection<CarType> types = getCarTypesOfCarRentalCompany(company);
        for(CarType type : types)
        {
            if(type.getName().equals(carType.getName()))
            {
                Query query = getEntityManager().createQuery("SELECT carType.cars FROM CarType carType WHERE carType.id = :id");
                query.setParameter("id", carType.getId());
                List<Set<Car>> resultList = query.getResultList();
                if(resultList.size() > 0)
                    out.addAll(resultList.get(0));
                return out;
            }
                
        }
        throw new IllegalArgumentException("Car type '" + carType.getName() + "' is not a car type of company '" + company + "'");
	}

	/**
	 * Check whether the given car renter has reservations.
	 *
	 * @param 	renter
	 * 			the car renter
	 * @return	True if the number of reservations of the given car renter is higher than 0.
	 * 			False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;		
	}	
}