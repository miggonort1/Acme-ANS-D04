
package acme.entities.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.datatypes.CrewMemberStatistics;
import acme.entities.flightassignment.CurrentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrewMemberDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	List<String>					lastFiveDestinationsAssigned;

	private Integer					legsWithIncidentSeverity3;
	private Integer					legsWithIncidentSeverity7;
	private Integer					legsWithIncidentSeverity10;

	List<String>					crewMembersAssignedLastLeg;

	Map<CurrentStatus, Integer>		flightAssignmentGroupedByStatus;

	private CrewMemberStatistics	flightAssignmentsStatsLastMonth;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
