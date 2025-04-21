
package acme.features.crewmember.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Leg;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;
import acme.realms.CrewMemberRepository;

@GuiService
public class CrewMemberFlightAssignmentCreateService extends AbstractGuiService<CrewMember, FlightAssignment> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberFlightAssignmentRepository	repository;

	@Autowired
	private CrewMemberRepository					crewMemberRepository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(CrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment = new FlightAssignment();
		assignment.setMoment(MomentHelper.getCurrentMoment());
		assignment.setDraftMode(true);

		CrewMember crewMember = (CrewMember) super.getRequest().getPrincipal().getActiveRealm();
		assignment.setCrewMember(crewMember);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);
		int crewMemberId = super.getRequest().getData("member", int.class);
		CrewMember crewMember = this.crewMemberRepository.findCrewMemberById(crewMemberId);

		super.bindObject(assignment, "duty", "status", "remarks");
		assignment.setLeg(leg);
		assignment.setCrewMember(crewMember);
		assignment.setMoment(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		CrewMember crewMember = (CrewMember) super.getRequest().getPrincipal().getActiveRealm();

		SelectChoices currentStatus = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices duties = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		SelectChoices legChoices = SelectChoices.from(this.repository.findAllLegsByAirlineId(crewMember.getAirline().getId()), "flightNumber", flightAssignment.getLeg());

		dataset = super.unbindObject(flightAssignment, "duty", "currentStatus", "moment", "remarks", "draftMode", "crewMember", "leg");
		dataset.put("crewMember", crewMember.getIdentity().getFullName());
		dataset.put("status", currentStatus.getSelected().getKey());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duties", duties);
		dataset.put("duty", duties.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
