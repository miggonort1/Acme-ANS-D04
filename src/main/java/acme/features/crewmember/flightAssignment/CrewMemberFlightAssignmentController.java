
package acme.features.crewmember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.CrewMember;

@GuiController
public class CrewMemberFlightAssignmentController extends AbstractGuiController<CrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberFlightAssignmentShowService			showService;

	@Autowired
	private CrewMemberFlightAssignmentCompletedListService	listCompletedService;

	@Autowired
	private CrewMemberFlightAssignmentPlannedListService	listPlannedService;

	@Autowired
	private CrewMemberFlightAssignmentCreateService			createService;

	@Autowired
	private CrewMemberFlightAssignmentDeleteService			deleteService;

	@Autowired
	private CrewMemberFlightAssignmentUpdateService			updateService;

	@Autowired
	private CrewMemberFlightAssignmentPublishService		publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("list-planned", "list", this.listPlannedService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
