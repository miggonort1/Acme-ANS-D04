
package acme.features.crewmember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Leg;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@Repository
public interface CrewMemberFlightAssignmentRepository extends AbstractRepository {

	// Legs completados
	@Query("select fa from FlightAssignment fa where fa.crewMember.id = :crewMemberId and fa.leg.scheduledArrival < :currentMoment")
	Collection<FlightAssignment> findCompletedFlightAssignments(int crewMemberId, Date currentMoment);

	// Legs pendientes
	@Query("select fa from FlightAssignment fa where fa.crewMember.id = :crewMemberId and fa.leg.scheduledDeparture > :currentMoment")
	Collection<FlightAssignment> findPendingFlightAssignments(int crewMemberId, Date currentMoment);

	@Query("select fa from FlightAssignment fa")
	Collection<FlightAssignment> findAllFlightAssignments();

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("SELECT l FROM Leg l WHERE l.aircraft.airline.id = :airlineId")
	Collection<Leg> findAllLegsByAirlineId(int airlineId);

	@Query("select distinct fa.leg from FlightAssignment fa where fa.crewMember.id = :memberId")
	Collection<Leg> findLegsByCrewMemberId(int memberId);

	@Query("select fa from FlightAssignment fa where fa.leg.id = :legId")
	Collection<FlightAssignment> findFlightAssignmentByLegId(int legId);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);

	@Query("""
		    SELECT COUNT(fa) > 0
		    FROM FlightAssignment fa
		    WHERE fa.crewMember = :crewMember
		    AND fa.leg.scheduledDeparture < :end
		    AND fa.leg.scheduledArrival > :start
		    AND fa.draftMode = false
		""")
	Boolean isOverlappingAssignment(CrewMember crewMember, java.util.Date start, java.util.Date end);

	@Query("""
		    SELECT COUNT(fa) > 0
		    FROM FlightAssignment fa
		    WHERE fa.crewMember = :crewMember
		    AND fa.id <> :currentId
		    AND fa.leg.scheduledDeparture < :end
		    AND fa.leg.scheduledArrival > :start
		    AND fa.draftMode = false
		""")
	boolean isOverlappingAssignmentExcludingSelf(CrewMember crewMember, Date start, Date end, int currentId);

	@Query("""
			SELECT COUNT(fa) > 0
			FROM FlightAssignment fa
			WHERE fa.leg = :leg
			AND fa.duty = :duty
			AND fa.id <> :currentId
			AND fa.draftMode = false
		""")
	boolean hasDutyAssignedExcludingSelf(Leg leg, Duty duty, int currentId);

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.leg = :leg AND fa.duty = :duty")
	long countByLegAndDuty(Leg leg, Duty duty);

	@Query("SELECT COUNT(fa) > 0 FROM FlightAssignment fa WHERE fa.crewMember.id = :crewMemberId AND fa.moment = :moment AND fa.draftMode = false")
	Boolean hasFlightCrewMemberLegAssociated(int crewMemberId, Date moment);

	@Query("""
		    SELECT COUNT(fa) > 0
		    FROM FlightAssignment fa
		    WHERE fa.crewMember = :crewMember
		    AND fa.leg = :leg
		""")
	boolean isAlreadyAssignedToLeg(CrewMember crewMember, Leg leg);

	@Query("""
		    SELECT l
		    FROM Leg l
		    WHERE l.aircraft.airline.id = :airlineId
		    AND l.draftMode = false
		""")
	Collection<Leg> findPublishedLegsByAirlineId(int airlineId);

	@Query("""
		    SELECT l
		    FROM Leg l
		    WHERE l.draftMode = false
		""")
	Collection<Leg> findPublishedLegs();

	@Query("SELECT COUNT(f) FROM FlightAssignment f WHERE f.crewMember = :crewMember")
	int countByCrewMember(CrewMember crewMember);

}
