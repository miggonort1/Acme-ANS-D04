
package acme.features.crewmember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
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
public class CrewMemberFlightAssignmentPublishService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository	repository;

	@Autowired
	private CrewMemberRepository					crewMemberRepository;


	@Override
	public void authorise() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		boolean status = false;

		if (flightAssignment != null && flightAssignment.getDraftMode()) {
			int activeUserId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean userOwnsAssignment = flightAssignment.getCrewMember().getId() == activeUserId;

			Object legData = super.getRequest().getData().get("leg");
			if (legData instanceof String legKey) {
				legKey = legKey.trim();

				if (legKey.equals("0"))
					status = userOwnsAssignment;
				else if (legKey.matches("\\d+")) {
					int legId = Integer.parseInt(legKey);
					Leg leg = this.repository.findLegById(legId);
					boolean legIsValid = this.repository.findAllLegs().contains(leg);
					status = userOwnsAssignment && legIsValid;
				}
			}

		}

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
		super.bindObject(object, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment object) {
		int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.crewMemberRepository.findCrewMemberById(userId);

		if (object.getLeg() != null) {
			boolean isLinkedToPastLeg = object.getLeg().getScheduledDeparture().before(MomentHelper.getCurrentMoment());
			super.state(!isLinkedToPastLeg, "leg", "acme.validation.flightAssignment.leg.moment");

			Date start = object.getLeg().getScheduledDeparture();
			Date end = object.getLeg().getScheduledArrival();
			boolean overlaps = this.repository.isOverlappingAssignmentExcludingSelf(object.getCrewMember(), start, end, object.getId());
			super.state(!overlaps, "*", "acme.validation.flightAssignment.crewMember.multipleLegs");

			boolean isLegDraft = object.getLeg().isDraftMode();
			super.state(!isLegDraft, "leg", "acme.validation.flightAssignment.legNotPublished");
		}

		if (object.getCrewMember() != null) {
			boolean isAvailable = object.getCrewMember().getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
			super.state(isAvailable, "crewMember", "acme.validation.flightAssignment.crewMember.available");
		}

		Leg selectedLeg = object.getLeg();

		if (selectedLeg != null) {
			boolean pilotAssigned = this.repository.hasDutyAssignedExcludingSelf(selectedLeg, Duty.PILOT, object.getId());
			boolean coPilotAssigned = this.repository.hasDutyAssignedExcludingSelf(selectedLeg, Duty.CO_PILOT, object.getId());

			if (object.getDuty() == Duty.PILOT)
				super.state(!pilotAssigned, "duty", "acme.validation.flightAssignment.crewMember.onlyOnePilot");

			if (object.getDuty() == Duty.CO_PILOT)
				super.state(!coPilotAssigned, "duty", "acme.validation.flightAssignment.crewMember.onlyOneCoPilot");
		}

	}

	@Override
	public void perform(final FlightAssignment object) {
		object.setDraftMode(false);
		object.setCurrentStatus(CurrentStatus.CONFIRMED);
		this.repository.save(object);
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
			boolean isCurrentLeg = leg.equals(flightAssignment.getLeg());

			if (isInFuture && !alreadyAssigned && !overlaps && !leg.isDraftMode() || isCurrentLeg) {
				String key = Integer.toString(leg.getId());
				String label = leg.getFlightNumber() + " (" + leg.getFlight().getTag() + ")";
				boolean selected = isCurrentLeg;
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

}
