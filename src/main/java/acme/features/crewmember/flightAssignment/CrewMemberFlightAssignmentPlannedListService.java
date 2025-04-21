
package acme.features.crewmember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@GuiService
public class CrewMemberFlightAssignmentPlannedListService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(CrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Date currentMoment = MomentHelper.getCurrentMoment();

		Collection<FlightAssignment> planned = this.repository.findPendingFlightAssignments(crewMemberId, currentMoment);

		super.getBuffer().addData(planned);
	}

	@Override
	public void unbind(final FlightAssignment object) {
		assert object != null;

		Date currentMoment = MomentHelper.getCurrentMoment();
		// String legStatus = object.getLeg().getScheduledDeparture().after(currentMoment) ? "PLANNED FLIGHT LEG" : "COMPLETED FLIGHT LEG";

		Dataset dataset;

		dataset = super.unbindObject(object, "duty", "moment", "currentStatus", "remarks", "crewMember", "draftMode", "leg");
		dataset.put("legFlightNumber", object.getLeg().getFlightNumber());
		dataset.put("legScheduledDeparture", object.getLeg().getScheduledDeparture());
		dataset.put("legScheduledArrival", object.getLeg().getScheduledArrival());
		dataset.put("crewMemberEmployeeCode", object.getCrewMember().getEmployeeCode());
		// dataset.put("legStatus", legStatus);

		super.addPayload(dataset, object, "duty", "moment", "currentStatus", "remarks", "crewMember", "draftMode", "leg");
		super.getResponse().addData(dataset);
	}

}
