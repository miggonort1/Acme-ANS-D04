
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.flight.Leg;

@Repository
public interface AdministratorClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.draftMode = false")
	Collection<Claim> findClaims();

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select l from Leg l where l.draftMode = false")
	Collection<Leg> findManyLegsPublished();

}
