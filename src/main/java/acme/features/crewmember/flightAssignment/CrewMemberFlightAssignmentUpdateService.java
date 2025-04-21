
package acme.features.crewmember.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberFlightAssignmentUpdateService extends AbstractGuiService<CrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


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

		flightAssignment.setMoment(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment object) {
		super.bindObject(object, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		SelectChoices duties = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices legs = SelectChoices.from(this.repository.findAllLegsByAirlineId(flightAssignment.getCrewMember().getAirline().getId()), "flightNumber", flightAssignment.getLeg());

		dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode", "crewMember", "leg");

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
