
package acme.features.authenticated.agent;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.Agent;

@GuiService
public class AuthenticatedAgentCreateService extends AbstractGuiService<Authenticated, Agent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedAgentRepository repository;

	// AbstractService<Authenticated, Consumer> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(Agent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Agent object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new Agent();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Agent object) {
		assert object != null;

		super.bindObject(object, "employeeCode", "spokenLanguages", "moment", "briefBio", "salary", "photo", "airline");
	}

	@Override
	public void validate(final Agent object) {
		assert object != null;
	}

	@Override
	public void perform(final Agent object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Agent object) {
		Dataset dataset;
		Collection<Airline> airlines = this.repository.findAllAirlines();
		SelectChoices airlineChoices = SelectChoices.from(airlines, "iataCode", object.getAirline());

		dataset = super.unbindObject(object, "employeeCode", "spokenLanguages", "moment", "briefBio", "salary", "photo", "airline");
		dataset.put("airlineChoices", airlineChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
