
package acme.entities.airline;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirlineRepository extends AbstractRepository {

	@Query("SELECT a from Airline a")
	Collection<Airline> findAllAirlines();
}
