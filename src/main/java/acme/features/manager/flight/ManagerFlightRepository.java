
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.realms.manager.Manager;

public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(Integer id);

	@Query("select f from Flight f where f.manager.userAccount.id = :managerId")
	Collection<Flight> findAllFlightsByManagerId(Integer managerId);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findAllLegsByFlightId(Integer flightId);

	@Query("select count(l) > 0 from Leg l where l.flight.id = :flightId")
	boolean existsLegsForFlight(Integer flightId);

	@Query("select count(l) = (select count(l2) from Leg l2 where l2.flight.id = :flightId and l2.status = 'PUBLISHED') from Leg l where l.flight.id = :flightId")
	boolean areAllLegsPublished(Integer flightId);

	@Query("select m from Manager m where m.id = :managerId")
	Manager findManagerById(Integer managerId);

}
