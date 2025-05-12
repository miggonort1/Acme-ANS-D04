
package acme.entities.forms;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.CrewMemberStatistics;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberDashboardShowService extends AbstractGuiService<CrewMember, CrewMemberDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(CrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		CrewMemberDashboard dashboard = new CrewMemberDashboard();
		CrewMember cm = (CrewMember) super.getRequest().getPrincipal().getActiveRealm();
		int crewMemberId = cm.getId();
		List<String> lastFiveDestinations = this.repository.findLastFiveDestinations(crewMemberId, PageRequest.of(0, 5));

		Integer legsWithIncidentSeverity3 = this.repository.countLegsWithSeverity(0, 3);
		Integer legsWithIncidentSeverity7 = this.repository.countLegsWithSeverity(4, 7);
		Integer legsWithIncidentSeverity10 = this.repository.countLegsWithSeverity(8, 10);

		List<FlightAssignment> assigmentsRelated = this.repository.findFlightAssignment(crewMemberId);
		List<String> lastLegMembers = new ArrayList<>();
		if (!assigmentsRelated.isEmpty()) {
			int legId = assigmentsRelated.get(0).getLeg().getId();
			List<CrewMember> crewMembers = this.repository.findCrewMembersInLastLeg(legId);
			lastLegMembers = crewMembers.stream().map(x -> x.getIdentity().getFullName()).toList();
		}

		List<Object[]> flightAssigmentsResult = this.repository.countFlightAssignmentsGroupedByStatus(crewMemberId);
		Map<CurrentStatus, Integer> flightAssignmentsGroupedByStatus = new HashMap<>();

		for (Object[] result : flightAssigmentsResult) {
			CurrentStatus statusType = (CurrentStatus) result[0];
			Integer count = ((Long) result[1]).intValue();
			flightAssignmentsGroupedByStatus.put(statusType, count);
		}

		CrewMemberStatistics flightAssignmentsStatsLastMonth = new CrewMemberStatistics();
		Date dateMinus1Year = MomentHelper.deltaFromCurrentMoment(-1, ChronoUnit.YEARS);

		Integer count = this.repository.countFlightAssignmentsLastYear(MomentHelper.deltaFromCurrentMoment(-1, ChronoUnit.YEARS), crewMemberId);
		Double average = (double) count / 12;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateMinus1Year);
		int year = calendar.get(Calendar.YEAR);

		Integer countPerMonth = 0;
		List<Integer> assignmentsPerMonth = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			countPerMonth = this.repository.countFlightAssignmentsPerMonthAndYear(crewMemberId, year, month);
			assignmentsPerMonth.add(countPerMonth != null ? countPerMonth : 0);
		}

		Optional<Integer> min = assignmentsPerMonth.stream().min(Integer::compareTo);
		Optional<Integer> max = assignmentsPerMonth.stream().max(Integer::compareTo);
		double standardDeviation = Math.sqrt(assignmentsPerMonth.stream().mapToDouble(n -> Math.pow(n - average, 2)).average().orElse(0.0));

		flightAssignmentsStatsLastMonth.setCount(count);
		flightAssignmentsStatsLastMonth.setAverage(average);
		flightAssignmentsStatsLastMonth.setMin(min.orElse(0).doubleValue());
		flightAssignmentsStatsLastMonth.setMax(max.orElse(0).doubleValue());
		flightAssignmentsStatsLastMonth.setDeviation(standardDeviation);

		dashboard.setLastFiveDestinationsAssigned(lastFiveDestinations);
		dashboard.setLegsWithIncidentSeverity3(legsWithIncidentSeverity3);
		dashboard.setLegsWithIncidentSeverity7(legsWithIncidentSeverity7);
		dashboard.setLegsWithIncidentSeverity10(legsWithIncidentSeverity10);
		dashboard.setCrewMembersAssignedLastLeg(lastLegMembers);
		dashboard.setFlightAssignmentGroupedByStatus(flightAssignmentsGroupedByStatus);
		dashboard.setFlightAssignmentsStatsLastMonth(flightAssignmentsStatsLastMonth);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final CrewMemberDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, //
			"lastFiveDestinationsAssigned", "legsWithIncidentSeverity3", // 
			"legsWithIncidentSeverity7", "legsWithIncidentSeverity10", //
			"crewMembersAssignedLastLeg", "flightAssignmentGroupedByStatus", "flightAssignmentsStatsLastMonth");

		super.getResponse().addData(dataset);
	}
}
