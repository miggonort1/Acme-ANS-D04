
package acme.features.authenticated.crewmember;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.realms.CrewMember;

@Repository
public interface AuthenticatedCrewMemberRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select cm from CrewMember cm where cm.userAccount.id = :id")
	CrewMember findCrewMemberByUserAccountId(int id);

	@Query("SELECT cm FROM CrewMember cm WHERE cm.employeeCode= :employeeCode")
	CrewMember findMemberSameCode(String employeeCode);

}
