
package acme.entities.forms;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@Repository
public interface CrewMemberDashboardRepository extends AbstractRepository {

	@Query("SELECT DISTINCT l.arrivalAirport.name FROM FlightAssignment f JOIN f.leg l WHERE f.crewMember.id = :crewMemberId ORDER BY f.moment DESC")
	List<String> findLastFiveDestinations(int crewMemberId, PageRequest pageable);

	@Query("SELECT COUNT(DISTINCT a.flightAssignment.leg) FROM ActivityLog a WHERE a.severityLevel BETWEEN :innerRange AND :outerRange")
	Integer countLegsWithSeverity(int innerRange, int outerRange);

	@Query("SELECT f FROM FlightAssignment f JOIN f.leg l WHERE f.crewMember.id = :crewMemberId ORDER BY l.scheduledArrival ASC")
	List<FlightAssignment> findFlightAssignment(int crewMemberId);

	@Query("SELECT DISTINCT fa.crewMember FROM FlightAssignment fa WHERE fa.leg.id = :legId")
	List<CrewMember> findCrewMembersInLastLeg(int legId);

	@Query("SELECT f.currentStatus, COUNT(f) FROM FlightAssignment f WHERE f.crewMember.id = :crewMemberId GROUP BY f.currentStatus")
	List<Object[]> countFlightAssignmentsGroupedByStatus(int crewMemberId);

	@Query("SELECT COUNT(f) FROM FlightAssignment f WHERE f.moment >= :moment AND f.crewMember.id = :crewMemberId")
	Integer countFlightAssignmentsLastYear(Date moment, int crewMemberId);

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.crewMember.id = :crewMemberId AND EXTRACT(YEAR FROM fa.leg.scheduledArrival) = :year AND EXTRACT(MONTH FROM fa.leg.scheduledArrival) = :month")
	Integer countFlightAssignmentsPerMonthAndYear(int crewMemberId, int year, int month);

}
