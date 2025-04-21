
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLog;
import acme.entities.flight.Leg;
import acme.realms.Agent;

@Repository
public interface AgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.agent.id = :id")
	Collection<Claim> findManyClaimsByAgentId(int id);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select a from Agent a where a.id = :id")
	Agent findOneAgentById(int id);

	@Query("select l from Leg l where l.status = 3")
	Collection<Leg> findManyLegsLanded();

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findAllTrackingLogsByClaimId(int claimId);
}
