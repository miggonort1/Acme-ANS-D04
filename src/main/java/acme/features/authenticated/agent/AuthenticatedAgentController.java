
package acme.features.authenticated.agent;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.realms.Agent;

@GuiController
public class AuthenticatedAgentController extends AbstractGuiController<Authenticated, Agent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedAgentCreateService	createService;

	@Autowired
	private AuthenticatedAgentUpdateService	updateService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}

}
