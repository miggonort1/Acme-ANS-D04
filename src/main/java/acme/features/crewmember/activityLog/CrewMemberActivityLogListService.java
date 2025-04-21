
package acme.features.crewmember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.features.crewmember.flightAssignment.CrewMemberFlightAssignmentRepository;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberActivityLogListService extends AbstractGuiService<CrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberActivityLogRepository			repository;

	@Autowired
	private CrewMemberFlightAssignmentRepository	flightAssignmentRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int assignmentId = super.getRequest().getData("assignmentId", int.class);
		FlightAssignment flightAssignment = this.flightAssignmentRepository.findFlightAssignmentById(assignmentId);
		boolean status = super.getRequest().getPrincipal().hasRealm(flightAssignment.getCrewMember()) && flightAssignment != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("assignmentId", int.class);
		Collection<ActivityLog> activityLogs = this.repository.findActivityLogsByFlightAssignmentId(assignmentId);
		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void unbind(final ActivityLog object) {
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		if (object.getFlightAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<ActivityLog> activityLogs) {
		super.getResponse().addGlobal("assignmentId", super.getRequest().getData("assignmentId", int.class));
	}

}
