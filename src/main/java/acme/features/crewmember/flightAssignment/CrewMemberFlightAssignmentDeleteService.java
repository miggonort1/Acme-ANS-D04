
package acme.features.crewmember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Leg;
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

}
