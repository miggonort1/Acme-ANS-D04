
package acme.features.crewmember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.ActivityLog;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberActivityLogPublishService extends AbstractGuiService<CrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);
		boolean status = activityLog.getDraftMode() && !activityLog.getFlightAssignment().getDraftMode() && super.getRequest().getPrincipal().hasRealm(activityLog.getFlightAssignment().getCrewMember()) && activityLog.getDraftMode() && activityLog != null;

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
		if (object.getFlightAssignment() != null && object.getFlightAssignment().getDraftMode())
			super.state(false, "flightAssignment", "acme.validation.activityLog.flightAssignment-not-published");
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
