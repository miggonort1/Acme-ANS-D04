
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLogStatus;
import acme.entities.claim.Type;
import acme.entities.flight.Leg;
import acme.realms.Agent;

@GuiService
public class AgentClaimPublishService extends AbstractGuiService<Agent, Claim> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);
		status = claim != null && claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAgent());

		if (super.getRequest().hasData("id")) {
			Integer legId = super.getRequest().getData("leg", Integer.class);
			if (legId == null || legId != 0) {
				Leg leg = this.repository.findLegById(legId);
				status = status && leg != null && !leg.isDraftMode() && leg.getScheduledDeparture().before(claim.getRegistrationMoment());
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
		super.bindObject(object, "description", "passengerEmail", "type", "leg");
	}

	@Override
	public void validate(final Claim object) {
		assert object != null;
	}

	@Override
	public void perform(final Claim object) {
		assert object != null;

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Claim object) {
		Dataset dataset;
		SelectChoices choicesType;
		TrackingLogStatus choicesStatus;
		SelectChoices choicesLegs;

		Collection<Leg> legs;
		legs = this.repository.findManyLegsPublished();
		Collection<Leg> validLegs = legs.stream().filter(leg -> leg.getScheduledDeparture().before(object.getRegistrationMoment())).toList();

		choicesType = SelectChoices.from(Type.class, object.getType());
		choicesStatus = object.getStatus();
		choicesLegs = SelectChoices.from(validLegs, "flightNumber", object.getLeg());

		dataset = super.unbindObject(object, "registrationMoment", "description", "passengerEmail", "type", "draftMode");
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
