
package acme.features.manager.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);

	@Query("SELECT l FROM Leg l WHERE l.id = :id")
	Leg findLegById(int id);

	@Query("SELECT a FROM Airport a WHERE a.id = :id")
	Airport findAirportById(int id);

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("SELECT a FROM Airport a")
	Collection<Airport> findAllAirports();

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("select l.flight from Leg l where l.id = :id")
	Flight findFlightByLegId(int id);

	@Query("select count(c) > 0 from Claim c where c.leg.id = :legId")
	boolean existsClaimsByLegId(int legId);

	@Query("select count(a) > 0 from Aircraft a, Manager m " + "where a.id = :aircraftId " + "and   m.id = :managerId " + "and   a.airline = m.airline")
	boolean existsAircraftOfManager(int managerId, int aircraftId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND l.scheduledDeparture < (SELECT ll.scheduledDeparture FROM Leg ll WHERE ll.id = :currentLegId) ORDER BY l.scheduledDeparture DESC")
	List<Leg> findPreviousLeg(int flightId, int currentLegId);

	@Query("select count(l) > 0 from Leg l " + "where l.flight.id = :flightId " + "and (l.departureAirport.id = :airportId " + "     or l.arrivalAirport.id   = :airportId)")
	boolean existsAirportInFlight(int flightId, int airportId);

}
