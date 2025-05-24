
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
import acme.realms.CrewMemberRepository;

@GuiService
public class CrewMemberActivityLogCreateService extends AbstractGuiService<CrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberActivityLogRepository			repository;

	@Autowired
	private CrewMemberFlightAssignmentRepository	flightAssignmentRepository;

	@Autowired
	private CrewMemberRepository					crewMemberRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.crewMemberRepository.findCrewMemberById(userId);

		boolean status = false;

		if (crewMember != null) {
			Object assignmentData = super.getRequest().getData().get("assignmentId");

			if (assignmentData != null) {
				int assignmentId = Integer.parseInt(assignmentData.toString());
				FlightAssignment assignment = this.flightAssignmentRepository.findFlightAssignmentById(assignmentId);

				boolean userOwnsAssignment = assignment != null && assignment.getCrewMember().getId() == userId;

				status = userOwnsAssignment;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLog = new ActivityLog();
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());

		int assignmentId = super.getRequest().getData("assignmentId", int.class);
		FlightAssignment flightAssignment = this.flightAssignmentRepository.findFlightAssignmentById(assignmentId);

		activityLog.setFlightAssignment(flightAssignment);
		activityLog.setDraftMode(true);
		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog object) {
		super.bindObject(object, "incidentType", "description", "severityLevel");
	}

	@Override
	public void unbind(final ActivityLog object) {
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		if (object.getFlightAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		dataset.put("assignmentId", super.getRequest().getData("assignmentId", int.class));
		super.getResponse().addData(dataset);
	}

	@Override
	public void validate(final ActivityLog object) {
		;
	}

	@Override
	public void perform(final ActivityLog object) {
		this.repository.save(object);
	}

}
