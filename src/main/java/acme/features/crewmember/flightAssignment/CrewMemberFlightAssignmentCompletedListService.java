
package acme.features.crewmember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.AvailabilityStatus;
import acme.realms.CrewMember;
import acme.realms.CrewMemberRepository;

@GuiService
public class CrewMemberFlightAssignmentCompletedListService extends AbstractGuiService<CrewMember, FlightAssignment> {

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
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Date currentMoment = MomentHelper.getCurrentMoment();

		Collection<FlightAssignment> completed = this.repository.findCompletedFlightAssignments(crewMemberId, currentMoment);

		super.getBuffer().addData(completed);
	}
	@Override
	public void unbind(final FlightAssignment object) {
		Dataset dataset = super.unbindObject(object, "duty", "moment", "currentStatus", "remarks", "draftMode", "leg");

		dataset.put("leg", object.getLeg().getFlightNumber());

		super.addPayload(dataset, object, "duty", "moment", "currentStatus", "remarks", "draftMode", "leg");
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<FlightAssignment> objects) {
		int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.crewMemberRepository.findCrewMemberById(userId);
		boolean canCreate = crewMember != null && crewMember.getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);

		super.getResponse().addGlobal("showCreate", canCreate);
	}

}
