
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;

@Repository
public interface AdministratorAirlineRepository extends AbstractRepository {

	@Query("select a from Airline a Where a.administrator.id = :id")
	Collection<Airline> findAllAirlinesByAdministrator(int id);

	@Query("select a from Airline a Where a.id = :airlineId")
	Airline findAirlineById(int airlineId);

}
