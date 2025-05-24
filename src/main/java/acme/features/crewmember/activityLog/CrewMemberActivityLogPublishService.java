
package acme.features.crewmember.activityLog;

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
public class CrewMemberActivityLogPublishService extends AbstractGuiService<CrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberActivityLogRepository			repository;

	@Autowired
	private CrewMemberFlightAssignmentRepository	flightAssignmentRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		boolean status = false;

		if (activityLog != null && activityLog.getDraftMode()) {
			boolean userOwnsActivityLog = super.getRequest().getPrincipal().hasRealm(activityLog.getFlightAssignment().getCrewMember());
			status = userOwnsActivityLog;
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
		super.bindObject(object, "incidentType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		FlightAssignment fa = activityLog.getFlightAssignment();

		boolean isAssignmentPublished = !fa.getDraftMode();
		super.state(isAssignmentPublished, "*", "acme.validation.activityLog.flightAssignment-not-published");

		boolean hasLegStarted = fa.getLeg().getScheduledDeparture().before(MomentHelper.getCurrentMoment());
		super.state(hasLegStarted, "*", "acme.validation.activityLog.leg.not-started");
	}

	@Override
	public void perform(final ActivityLog object) {
		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "incidentType", "description", "severityLevel", "flightAssignment", "draftMode");

		if (activityLog.getFlightAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		super.getResponse().addData(dataset);
	}

}
