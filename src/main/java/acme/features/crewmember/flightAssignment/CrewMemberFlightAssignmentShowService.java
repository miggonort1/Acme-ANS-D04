
package acme.features.crewmember.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberFlightAssignmentShowService extends AbstractGuiService<CrewMember, FlightAssignment> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightAssignmentId;
		CrewMember crewMember;
		FlightAssignment assignment;

		flightAssignmentId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		crewMember = assignment == null ? null : assignment.getCrewMember();
		status = crewMember != null && super.getRequest().getPrincipal().hasRealm(crewMember);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		SelectChoices duties = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices legs = SelectChoices.from(this.repository.findAllLegsByAirlineId(flightAssignment.getCrewMember().getAirline().getId()), "flightNumber", flightAssignment.getLeg());

		dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "crewMember", "leg", "draftMode");
		dataset.put("crewMember", flightAssignment.getCrewMember().getIdentity().getFullName());
		dataset.put("duties", duties);
		dataset.put("duty", duties.getSelected().getKey());
		dataset.put("statusChoices", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());
		dataset.put("legs", legs);
		dataset.put("leg", legs.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
