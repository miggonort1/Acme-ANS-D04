
package acme.realms.manager;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface ManagerRepository extends AbstractRepository {

	@Query("SELECT COUNT(m) > 0 FROM Manager m WHERE m.identifierNumber = :identifierNumber")
	boolean existsByIdentifierNumber(String identifierNumber);

	@Query("SELECT m FROM Manager m WHERE m.identifierNumber = :identifierNumber")
	Manager findByIdentifierNumber(String identifierNumber);
}
