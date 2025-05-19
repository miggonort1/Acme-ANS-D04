
package acme.features.crewmember.flightAssignment;

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
					boolean legIsValid = leg != null && this.repository.findAllLegs().contains(leg);
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

		if (object.getDuty() != null && object.getLeg() != null) {
			boolean isDutyAlreadyAssigned = this.repository.hasDutyAssigned(object.getLeg().getId(), object.getDuty(), object.getId());
			super.state(!isDutyAlreadyAssigned, "duty", "acme.validation.flightAssignment.duty");
		}

		if (object.getLeg() != null) {
			boolean isLinkedToPastLeg = object.getLeg().getScheduledDeparture().before(MomentHelper.getCurrentMoment());
			super.state(!isLinkedToPastLeg, "leg", "acme.validation.flightAssignment.leg.moment");

			Date start = object.getLeg().getScheduledDeparture();
			Date end = object.getLeg().getScheduledArrival();
			boolean overlaps = this.repository.isOverlappingAssignmentExcludingSelf(object.getCrewMember(), start, end, object.getId());
			super.state(!overlaps, "*", "acme.validation.flightAssignment.crewMember.multipleLegs");

			boolean isLegDraft = object.getLeg().isDraftMode();
			super.state(!isLegDraft, "leg", "acme.validation.flightAssignment.leg.notPublished");
		}

		if (object.getCrewMember() != null) {
			boolean isAvailable = object.getCrewMember().getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
			super.state(isAvailable, "crewMember", "acme.validation.flightAssignment.crewMember.available");

			boolean isAlreadyAssigned = this.repository.hasFlightCrewMemberLegAssociated(object.getCrewMember().getId(), object.getMoment());
			super.state(!isAlreadyAssigned, "crewMember", "acme.validation.flightAssignment.crewMember.multipleLegs");
		}

	}

	@Override
	public void perform(final FlightAssignment object) {
		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		SelectChoices duties = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices legs = SelectChoices.from(this.repository.findPublishedLegsByAirlineId(flightAssignment.getCrewMember().getAirline().getId()), "flightNumber", flightAssignment.getLeg());

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
