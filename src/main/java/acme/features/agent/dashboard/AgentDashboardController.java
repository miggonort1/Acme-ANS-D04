
package acme.features.agent.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.forms.AgentDashboard;
import acme.realms.Agent;

@GuiController
public class AgentDashboardController extends AbstractGuiController<Agent, AgentDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentDashboardShowService agentDashboardService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.agentDashboardService);
	}

}
