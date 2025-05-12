
package acme.entities.forms;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.realms.CrewMember;

@GuiController
public class CrewMemberDashboardController extends AbstractGuiController<CrewMember, CrewMemberDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CrewMemberDashboardShowService showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
	}

}
