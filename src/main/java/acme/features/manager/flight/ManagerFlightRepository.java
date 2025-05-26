
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;

public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(Integer id);

	@Query("select f from Flight f where f.manager.id = :managerId")
	Collection<Flight> findAllFlightsByManagerId(Integer managerId);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findAllLegsByFlightId(Integer flightId);

	@Query("select count(fa) from FlightAssignment fa where fa.leg.flight.id = :flightId")
	int countAssignmentsByFlightId(int flightId);
}
