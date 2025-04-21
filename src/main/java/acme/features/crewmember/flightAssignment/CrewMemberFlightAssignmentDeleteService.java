
package acme.features.crewmember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.ActivityLog;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.features.crewmember.activityLog.CrewMemberActivityLogRepository;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberFlightAssignmentDeleteService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository	repository;

	@Autowired
	private CrewMemberActivityLogRepository			activityLogRepository;


	@Override
	public void authorise() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		boolean status = flightAssignment != null && flightAssignment.getDraftMode() && super.getRequest().getPrincipal().hasRealm(flightAssignment.getCrewMember());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment object) {
		;
	}

	@Override
	public void validate(final FlightAssignment object) {
		;
	}

	@Override
	public void perform(final FlightAssignment object) {
		Collection<ActivityLog> activityLogs = this.activityLogRepository.findAllActivityLogs(object.getId());
		this.repository.deleteAll(activityLogs);
		this.repository.delete(object);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode", "crewMember", "leg");

		SelectChoices duties = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices legChoices = SelectChoices.from(this.repository.findAllLegsByAirlineId(flightAssignment.getCrewMember().getAirline().getId()), "flightNumber", flightAssignment.getLeg());

		dataset.put("crewMember", flightAssignment.getCrewMember().getIdentity().getFullName());
		dataset.put("duties", duties);
		dataset.put("duty", duties.getSelected().getKey());
		dataset.put("statusChoices", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());
		dataset.put("legChoices", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
