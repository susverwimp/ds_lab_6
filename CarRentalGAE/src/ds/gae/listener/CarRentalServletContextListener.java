package ds.gae.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ds.gae.EMF;
import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;

public class CarRentalServletContextListener implements ServletContextListener {

    private EntityManager em;
    
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// This will be invoked as part of a warming request, 
		// or the first user request if no warming request was invoked.
	    // check if dummy data is available, and add if necessary
		if(!isDummyDataAvailable()) {
			addDummyData();
		}
	    //addCarRentalCompanyTest();
	}
	
	private Car addCarTest()
	{
	    Car car = new Car();
	    return getEntityManager().merge(car);
	}
	
	private CarType addCarTypeTest()
	{
	    CarType carType = new CarType();
	    carType.setName("Test type");
	    carType.setNbOfSeats(4);
	    carType.setRentalPricePerDay(140);
	    carType.setSmokingAllowed(false);
	    carType.setTrunkSpace(14);
	    return getEntityManager().merge(carType);
	}
	
	private CarRentalCompany addCarRentalCompanyTest()
	{
	    CarRentalCompany company = new CarRentalCompany();
	    //company.getCars().add(addCarTest());
	    return getEntityManager().merge(company);
	}
	
	private boolean isDummyDataAvailable() {
		// If the Hertz car rental company is in the datastore, we assume the dummy data is available

		// FIXME: use persistence instead
		return (getEntityManager().find(CarRentalCompany.class, "Hertz") != null);

	}
	
	private void addDummyData() {
		loadRental("Hertz","hertz.csv");
        loadRental("Dockx","dockx.csv");
	}
	
	private EntityManager getEntityManager()
	{
        if(em == null || !em.isOpen())
        {
            em = EMF.get().createEntityManager();
        }
        return em;
	}
	
	private void loadRental(String name, String datafile) {
		Logger.getLogger(CarRentalServletContextListener.class.getName()).log(Level.INFO, "loading {0} from file {1}", new Object[]{name, datafile});
        try 
        {
    		// FIXME: use persistence instead
            EntityTransaction transaction = getEntityManager().getTransaction();
            transaction.begin();
            getEntityManager().persist(loadData(name, datafile));
            transaction.commit();

        } catch (NumberFormatException ex) 
        {
            Logger.getLogger(CarRentalServletContextListener.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) 
        {
            Logger.getLogger(CarRentalServletContextListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            getEntityManager().close();
        }
	}
	
	public CarRentalCompany loadData(String name, String datafile) throws NumberFormatException, IOException {
		// FIXME: adapt the implementation of this method to your entity structure
		
		Set<CarType> carTypes = new HashSet<>();
		//open file from jar
		BufferedReader in = new BufferedReader(new InputStreamReader(CarRentalServletContextListener.class.getClassLoader().getResourceAsStream(datafile)));
		//while next line exists
		while (in.ready()) {
			//read line
			String line = in.readLine();
			//if comment: skip
			if (line.startsWith("#")) {
				continue;
			}
			//tokenize on ,
			StringTokenizer csvReader = new StringTokenizer(line, ",");
			//create new car type from first 5 fields
			CarType type = new CarType(csvReader.nextToken(),
					Integer.parseInt(csvReader.nextToken()),
					Float.parseFloat(csvReader.nextToken()),
					Double.parseDouble(csvReader.nextToken()),
					Boolean.parseBoolean(csvReader.nextToken()));
			//create N new cars with given type, where N is the 5th field
			//getEntityManager().merge(type);
            Set<Car> cars = new HashSet<>();
			for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
				cars.add(new Car());
			}
			type.setCars(cars);
			carTypes.add(type);
		}
		CarRentalCompany crc = new CarRentalCompany(name, carTypes);
        return crc;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// App Engine does not currently invoke this method.
	}
}