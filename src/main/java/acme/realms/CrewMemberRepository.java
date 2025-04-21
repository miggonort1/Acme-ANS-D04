
package acme.realms;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface CrewMemberRepository extends AbstractRepository {

	@Query("SELECT cm FROM CrewMember cm WHERE cm.employeeCode= :employeeCode")
	CrewMember findMemberSameCode(String employeeCode);

	@Query("select cm from CrewMember cm")
	Collection<CrewMember> findAllCrewMembers();

	@Query("select cm from CrewMember cm where cm.id = :memberId")
	CrewMember findCrewMemberById(int memberId);

}
