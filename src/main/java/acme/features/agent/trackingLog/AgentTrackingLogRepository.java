
package acme.features.agent.trackingLog;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.lastUpdateMoment DESC")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("SELECT c FROM Claim c WHERE c.id = :id")
	Claim findClaimById(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId ORDER BY tl.creationMoment DESC")
	Optional<List<TrackingLog>> findLastTrackingLog(int claimId);
}
