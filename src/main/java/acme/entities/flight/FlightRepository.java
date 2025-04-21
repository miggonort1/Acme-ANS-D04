
package acme.entities.flight;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("SELECT MIN(l.scheduledDeparture) FROM Leg l WHERE l.flight.id = :flightId")
	Date findScheduledDeparture(int flightId);

	@Query("SELECT MAX(l.scheduledArrival) FROM Leg l WHERE l.flight.id = :flightId")
	Date findScheduledArrival(int flightId);

	@Query("SELECT a.city FROM Airport a WHERE a = (SELECT l.departureAirport FROM Leg l WHERE l.flight.id = :flightId AND l.scheduledDeparture = (SELECT MIN(l2.scheduledDeparture) FROM Leg l2 WHERE l2.flight.id = :flightId))")
	String findOriginCity(int flightId);

	@Query("SELECT a.city FROM Airport a WHERE a = (SELECT l.arrivalAirport FROM Leg l WHERE l.flight.id = :flightId AND l.scheduledArrival = (SELECT MAX(l2.scheduledArrival) FROM Leg l2 WHERE l2.flight.id = :flightId))")
	String findDestinationCity(int flightId);

	@Query("SELECT COUNT(l) FROM Leg l WHERE l.flight.id = :flightId")
	Integer countLegs(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	List<Leg> findLegsByFlight(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);
}
