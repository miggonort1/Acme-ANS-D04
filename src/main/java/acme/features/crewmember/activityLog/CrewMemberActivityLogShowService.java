
package acme.features.crewmember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.ActivityLog;
import acme.features.crewmember.flightAssignment.CrewMemberFlightAssignmentRepository;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberActivityLogShowService extends AbstractGuiService<CrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberActivityLogRepository			repository;

	@Autowired
	private CrewMemberFlightAssignmentRepository	flightAssignmentRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);
		boolean status = super.getRequest().getPrincipal().hasRealm(activityLog.getFlightAssignment().getCrewMember()) && activityLog != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog object) {
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		if (object.getFlightAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		boolean draftModeFlightAssignment = this.flightAssignmentRepository.findFlightAssignmentById(object.getFlightAssignment().getId()).getDraftMode();
		dataset.put("draftModeFlightAssignment", draftModeFlightAssignment);

		super.getResponse().addData(dataset);
	}
}
