
package acme.features.agent.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.claim.Type;
import acme.entities.flight.Leg;
import acme.realms.Agent;

@GuiService
public class AgentClaimCreateService extends AbstractGuiService<Agent, Claim> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Agent.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim object;
		Agent agent;
		Date moment;

		agent = this.repository.findOneAgentById(super.getRequest().getPrincipal().getActiveRealm().getId());
		moment = MomentHelper.getCurrentMoment();

		object = new Claim();
		object.setLeg(null);
		object.setDraftMode(true);
		object.setRegistrationMoment(moment);
		object.setAgent(agent);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Claim object) {
		assert object != null;
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		object.setLeg(leg);
		super.bindObject(object, "description", "passengerEmail", "status", "type", "leg");
	}

	@Override
	public void validate(final Claim object) {
		assert object != null;

	}

	@Override
	public void perform(final Claim object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Claim object) {
		Dataset dataset;
		SelectChoices choicesType;
		SelectChoices choicesStatus;
		SelectChoices choicesLegs;

		Collection<Leg> legs;
		legs = this.repository.findManyLegsLanded();

		choicesType = SelectChoices.from(Type.class, object.getType());
		choicesStatus = SelectChoices.from(ClaimStatus.class, object.getStatus());
		choicesLegs = SelectChoices.from(legs, "flightNumber", object.getLeg());

		dataset = super.unbindObject(object, "registrationMoment", "description", "passengerEmail", "status", "type", "draftMode");
		dataset.put("type", choicesType);
		dataset.put("status", choicesStatus);
		dataset.put("legs", choicesLegs);
		dataset.put("leg", choicesLegs.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
