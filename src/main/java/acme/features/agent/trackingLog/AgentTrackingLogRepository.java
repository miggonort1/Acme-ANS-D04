
package acme.features.agent.trackingLog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLog;
import acme.realms.Agent;

@Repository
public interface AgentTrackingLogRepository extends AbstractRepository {

	@Query("select a from Agent a where a.id = :id")
	Agent findOneAgentById(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.draftMode = false")
	Collection<TrackingLog> findTrackingLogsPublishedByClaimId(int claimId);

	@Query("SELECT c FROM Claim c WHERE c.id = :id")
	Claim findClaimById(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.resolutionPercentage = 100.0")
	List<TrackingLog> findCompletedTrackingLogsByClaimId(int claimId);

}
