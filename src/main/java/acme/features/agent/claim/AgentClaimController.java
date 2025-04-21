
package acme.features.agent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claim.Claim;
import acme.realms.Agent;

@GuiController
public class AgentClaimController extends AbstractGuiController<Agent, Claim> {

	@Autowired
	private AgentClaimListMineService	listMineService;

	@Autowired
	private AgentClaimShowService		showService;

	@Autowired
	private AgentClaimCreateService		createService;

	@Autowired
	private AgentClaimUpdateService		updateService;

	@Autowired
	private AgentClaimDeleteService		deleteService;

	@Autowired
	private AgentClaimPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("list-mine", "list", this.listMineService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
