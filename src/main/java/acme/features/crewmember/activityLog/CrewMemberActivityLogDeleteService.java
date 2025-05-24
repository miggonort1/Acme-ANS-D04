
package acme.features.crewmember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberActivityLogDeleteService extends AbstractGuiService<CrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		boolean status = false;

		if (activityLog != null && activityLog.getDraftMode()) {
			int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
			FlightAssignment assignment = activityLog.getFlightAssignment();

			boolean userOwnsLog = assignment.getCrewMember().getId() == userId;
			boolean assignmentIsPublished = !assignment.getDraftMode();

			status = userOwnsLog && assignmentIsPublished;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog object) {
		;
	}

	@Override
	public void validate(final ActivityLog object) {
		;
	}

	@Override
	public void perform(final ActivityLog object) {
		this.repository.delete(object);
	}

	@Override
	public void unbind(final ActivityLog object) {
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		if (object.getFlightAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		super.getResponse().addData(dataset);
	}
}
