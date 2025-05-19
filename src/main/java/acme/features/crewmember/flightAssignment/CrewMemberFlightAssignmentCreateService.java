
package acme.features.crewmember.flightAssignment;

import java.util.Collection;
import java.util.Date;

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
import acme.realms.AvailabilityStatus;
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
		int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.crewMemberRepository.findCrewMemberById(userId);

		if (crewMember == null || crewMember.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
			super.getResponse().setAuthorised(false);
			return;
		}

		Object legData = super.getRequest().getData().get("leg");

		if (legData == null || "0".equals(legData.toString().trim())) {
			super.getResponse().setAuthorised(true);
			return;
		}

		String legKey = legData.toString().trim();
		if (legKey.matches("\\d+")) {
			int legId = Integer.parseInt(legKey);

			Leg leg = this.repository.findLegById(legId);
			boolean legIsValid = leg != null && !leg.isDraftMode();
			super.getResponse().setAuthorised(legIsValid);
			return;
		}

		// En cualquier otro caso, no se autoriza
		super.getResponse().setAuthorised(false);
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

		super.bindObject(assignment, "duty", "currentStatus", "remarks");
		assignment.setLeg(leg);
		CrewMember crewMember = (CrewMember) super.getRequest().getPrincipal().getActiveRealm();
		assignment.setCrewMember(crewMember);
		assignment.setMoment(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.crewMemberRepository.findCrewMemberById(userId);

		if (assignment.getLeg() != null) {

			boolean isLinkedToPastLeg = assignment.getLeg().getScheduledDeparture().before(MomentHelper.getCurrentMoment());
			super.state(!isLinkedToPastLeg, "leg", "acme.validation.flightAssignment.leg.moment");

			Date start = assignment.getLeg().getScheduledDeparture();
			Date end = assignment.getLeg().getScheduledArrival();
			boolean overlaps = this.repository.isOverlappingAssignment(crewMember, start, end);
			super.state(!overlaps, "*", "acme.validation.flightAssignment.crewMember.multipleLegs");
		}

		Leg selectedLeg = assignment.getLeg();

		if (selectedLeg != null) {
			long pilotCount = this.repository.countByLegAndDuty(selectedLeg, Duty.PILOT);
			long coPilotCount = this.repository.countByLegAndDuty(selectedLeg, Duty.CO_PILOT);

			boolean isPilotAssigned = pilotCount > 0;
			boolean isCoPilotAssigned = coPilotCount > 0;

			super.state(!(isPilotAssigned && assignment.getDuty() == Duty.PILOT), "duty", "acme.validation.flightAssignment.crewMember.onlyOnePilot");
			super.state(!(isCoPilotAssigned && assignment.getDuty() == Duty.CO_PILOT), "duty", "acme.validation.flightAssignment.crewMember.onlyOneCoPilot");
		}
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		CrewMember crewMember = (CrewMember) super.getRequest().getPrincipal().getActiveRealm();

		Collection<Leg> legs = this.repository.findAllLegsByAirlineId(crewMember.getAirline().getId());
		SelectChoices legChoices = new SelectChoices();
		boolean hasAvailableLegs = false;

		for (Leg leg : legs) {
			boolean isInFuture = leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
			boolean alreadyAssigned = this.repository.isAlreadyAssignedToLeg(crewMember, leg);
			boolean overlaps = this.repository.isOverlappingAssignment(crewMember, leg.getScheduledDeparture(), leg.getScheduledArrival());

			if (isInFuture && !alreadyAssigned && !overlaps && !leg.isDraftMode()) {
				String key = Integer.toString(leg.getId());
				String label = leg.getFlightNumber() + " (" + leg.getFlight().getTag() + ")";
				boolean selected = leg.equals(flightAssignment.getLeg());
				legChoices.add(key, label, selected);
				hasAvailableLegs = true;
			}

		}

		if (!hasAvailableLegs)
			legChoices.add("0", "acme.validation.flightAssignment.crewMember.noAvailableLegs", true);
		else
			legChoices.add("0", "----", flightAssignment.getLeg() == null);

		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices duties = SelectChoices.from(Duty.class, flightAssignment.getDuty());

		dataset = super.unbindObject(flightAssignment, "duty", "currentStatus", "moment", "remarks", "draftMode", "leg");

		dataset.put("crewMember", crewMember.getIdentity().getFullName());
		dataset.put("statusChoices", statusChoices);
		dataset.put("currentStatus", statusChoices.getSelected().getKey());
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
