
package acme.features.agent.trackingLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claim.TrackingLog;
import acme.realms.Agent;

@GuiController
public class AgentTrackingLogController extends AbstractGuiController<Agent, TrackingLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentTrackingLogListMineService	listService;

	@Autowired
	private AgentTrackingLogShowService		showService;

	@Autowired
	private AgentTrackingLogCreateService	createService;

	@Autowired
	private AgentTrackingLogUpdateService	updateService;

	@Autowired
	private AgentTrackingLogDeleteService	deleteService;

	@Autowired
	private AgentTrackingLogPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("list-mine", "list", this.listService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
