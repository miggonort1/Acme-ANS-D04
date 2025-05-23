
package acme.features.authenticated.agent;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.Agent;

@Repository
public interface AuthenticatedAgentRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select a from Agent a where a.userAccount.id = :id")
	Agent findAssistanceAgentByUserAccountId(int id);

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

}
