
package acme.features.crewmember.activityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightassignment.ActivityLog;

@Repository
public interface CrewMemberActivityLogRepository extends AbstractRepository {

	@Query("SELECT a FROM ActivityLog a WHERE a.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findAllActivityLogs(int flightAssignmentId);

	@Query("select al from ActivityLog al where al.flightAssignment.crewMember.id = :memberId")
	Collection<ActivityLog> findAllLogsByFlightCrewMemberId(int memberId);

	@Query("SELECT a FROM ActivityLog a WHERE a.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(int flightAssignmentId);

	@Query("SELECT a FROM ActivityLog a WHERE a.id = :activityLogId")
	ActivityLog findActivityLogById(int activityLogId);

}
