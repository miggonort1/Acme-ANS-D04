
package acme.features.crewmember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Leg;
import acme.entities.flight.Status;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.AvailabilityStatus;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberFlightAssignmentShowService extends AbstractGuiService<CrewMember, FlightAssignment> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		boolean isOwner = assignment != null && super.getRequest().getPrincipal().hasRealm(assignment.getCrewMember());

		boolean isPublished = assignment != null && !assignment.getDraftMode();

		boolean status = isOwner || isPublished;

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
		CrewMember crewMember = (CrewMember) super.getRequest().getPrincipal().getActiveRealm();

		Collection<Leg> legs = this.repository.findAllLegsByAirlineId(crewMember.getAirline().getId());
		SelectChoices legChoices = new SelectChoices();
		boolean hasAvailableLegs = false;

		for (Leg leg : legs) {
			boolean isInFuture = leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
			boolean alreadyAssigned = this.repository.isAlreadyAssignedToLeg(crewMember, leg);
			boolean overlaps = this.repository.isOverlappingAssignment(crewMember, leg.getScheduledDeparture(), leg.getScheduledArrival());
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

		SelectChoices duties = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices availabilityChoices = SelectChoices.from(AvailabilityStatus.class, flightAssignment.getCrewMember().getAvailabilityStatus());
		SelectChoices legStatuses = SelectChoices.from(Status.class, flightAssignment.getLeg() != null ? flightAssignment.getLeg().getStatus() : null);

		dataset = super.unbindObject(flightAssignment, "duty", "currentStatus", "moment", "remarks", "draftMode", "leg");

		if (flightAssignment.getCrewMember() != null) {
			crewMember = flightAssignment.getCrewMember();
			dataset.put("flightCrewMember.id", crewMember.getId());
			dataset.put("flightCrewMember.employeeCode", crewMember.getEmployeeCode());
			dataset.put("flightCrewMember.phoneNumber", crewMember.getPhoneNumber());
			dataset.put("flightCrewMember.languageSkills", crewMember.getLanguageSkills());
			dataset.put("flightCrewMember.availabilityStatus", crewMember.getAvailabilityStatus());
			dataset.put("flightCrewMember.salary", crewMember.getSalary());
			dataset.put("flightCrewMember.yearsOfExperience", crewMember.getYearsOfExperience());
			dataset.put("flightCrewMember.airline", crewMember.getAirline().getName());
		}

		if (flightAssignment.getLeg() != null) {
			Leg leg = flightAssignment.getLeg();
			dataset.put("leg.id", leg.getId());
			dataset.put("leg.flightNumber", leg.getFlightNumber());
			dataset.put("leg.status", leg.getStatus());
			dataset.put("leg.scheduledDeparture", leg.getScheduledDeparture());
			dataset.put("leg.scheduledArrival", leg.getScheduledArrival());
			dataset.put("leg.departureAirport", leg.getDepartureAirport().getName());
			dataset.put("leg.arrivalAirport", leg.getArrivalAirport().getName());
			dataset.put("leg.aircraft", leg.getAircraft().getRegistrationNumber());
			dataset.put("leg.flight", leg.getFlight().getTag());
		}

		dataset = super.unbindObject(flightAssignment, "duty", "currentStatus", "moment", "remarks", "draftMode", "leg");

		dataset.put("leg", flightAssignment.getLeg() != null ? Integer.toString(flightAssignment.getLeg().getId()) : "0");

		dataset.put("statusChoices", statusChoices);
		dataset.put("currentStatus", statusChoices.getSelected().getKey());
		dataset.put("duty", duties.getSelected().getKey());

		dataset.put("confirmation", false);
		dataset.put("duties", duties);
		dataset.put("availabilityChoices", availabilityChoices);
		dataset.put("legStatuses", legStatuses);
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

}
