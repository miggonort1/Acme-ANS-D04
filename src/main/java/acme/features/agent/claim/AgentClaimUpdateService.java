
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.claim.Type;
import acme.entities.flight.Leg;
import acme.realms.Agent;

@GuiService
public class AgentClaimUpdateService extends AbstractGuiService<Agent, Claim> {

	@Autowired
	private AgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		int agentId;
		Claim claim;
		Agent agent;

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		agent = this.repository.findOneAgentById(agentId);
		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);
		status = claim != null && (!claim.isDraftMode() || super.getRequest().getPrincipal().hasRealm(claim.getAgent())) && claim.getAgent().equals(agent);

		if (super.getRequest().hasData("id")) {
			Integer legId = super.getRequest().getData("leg", Integer.class);
			if (legId == null || legId != 0) {
				Leg leg = this.repository.findLegById(legId);
				status = status && leg != null && !leg.isDraftMode();
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim object;
		int id;

		id = super.getRequest().getData("id", int.class);

		object = this.repository.findClaimById(id);
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
		legs = this.repository.findManyLegsPublished();
		for (Leg leg : legs)
			if (leg.getScheduledArrival().before(object.getRegistrationMoment()))
				legs.add(leg);

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

}
