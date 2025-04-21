
package acme.features.authenticated.crewmember;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.realms.CrewMember;

@GuiController
public class AuthenticatedCrewMemberController extends AbstractGuiController<Authenticated, CrewMember> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCrewMemberCreateService	createService;

	@Autowired
	private AuthenticatedCrewMemberUpdateService	updateService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}

}
