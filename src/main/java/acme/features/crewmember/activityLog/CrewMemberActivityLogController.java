
package acme.features.crewmember.activityLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightassignment.ActivityLog;
import acme.realms.CrewMember;

@GuiController
public class CrewMemberActivityLogController extends AbstractGuiController<CrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberActivityLogListService	listService;

	@Autowired
	private CrewMemberActivityLogShowService	showService;

	@Autowired
	private CrewMemberActivityLogCreateService	createService;

	@Autowired
	private CrewMemberActivityLogUpdateService	updateService;

	@Autowired
	private CrewMemberActivityLogDeleteService	deleteService;

	@Autowired
	private CrewMemberActivityLogPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
