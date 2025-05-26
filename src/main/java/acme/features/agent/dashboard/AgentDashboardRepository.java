
package acme.features.agent.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLog;

@Repository
public interface AgentDashboardRepository extends AbstractRepository {

	@Query("SELECT c FROM Claim c WHERE c.agent.id = :agentId")
	Collection<Claim> findClaimsByAgentId(int agentId);

	@Query("SELECT COUNT(t) FROM TrackingLog t WHERE t.claim.id = :claimId")
	int countTrackingLogsByClaimId(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);
}
