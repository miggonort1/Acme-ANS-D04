
package acme.features.crewmember.flightAssignment;

import java.util.Collection;

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
import acme.realms.CrewMember;
import acme.realms.CrewMemberRepository;

@GuiService
public class CrewMemberFlightAssignmentUpdateService extends AbstractGuiService<CrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberFlightAssignmentRepository	repository;

	@Autowired
	private CrewMemberRepository					crewMemberRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(id);

		boolean status = false;

		if (assignment != null && assignment.getDraftMode()) {
			int activeUserId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean userOwnsAssignment = assignment.getCrewMember().getId() == activeUserId;

			Object legData = super.getRequest().getData().get("leg");

			if (legData instanceof String legKey) {
				legKey = legKey.trim();

				if (legKey.equals("0"))
					status = userOwnsAssignment;
				else if (legKey.matches("\\d+")) {
					int legId = Integer.parseInt(legKey);
					Leg leg = this.repository.findLegById(legId);
					boolean legIsValid = leg != null && (!leg.isDraftMode() || leg.equals(assignment.getLeg()));
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
		CrewMember crewMember = (CrewMember) super.getRequest().getPrincipal().getActiveRealm();

		Collection<Leg> legs = this.repository.findAllLegsByAirlineId(crewMember.getAirline().getId());
		SelectChoices legChoices = new SelectChoices();
		boolean hasAvailableLegs = false;

		for (Leg leg : legs) {
			boolean isInFuture = leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
			boolean alreadyAssigned = this.repository.isAlreadyAssignedToLeg(crewMember, leg);
			boolean overlaps = this.repository.isOverlappingAssignmentExcludingSelf(flightAssignment.getCrewMember(), leg.getScheduledDeparture(), leg.getScheduledArrival(), flightAssignment.getId());
			boolean isValid = isInFuture && !alreadyAssigned && !overlaps && !leg.isDraftMode();
			boolean isCurrent = leg.equals(flightAssignment.getLeg());
			if (isValid || isCurrent) {
				String key = Integer.toString(leg.getId());
				String label = leg.getFlightNumber() + " (" + leg.getFlight().getTag() + ")";
				boolean selected = isCurrent;
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
