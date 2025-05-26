
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLog;
import acme.entities.claim.TrackingLogStatus;
import acme.entities.claim.Type;
import acme.entities.flight.Leg;
import acme.realms.Agent;

@GuiService
public class AgentClaimDeleteService extends AbstractGuiService<Agent, Claim> {
	// Internal State --------------------------------------------------------------------

	@Autowired
	private AgentClaimRepository repository;

	// AbstractGuiService ----------------------------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);
		status = claim != null && claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAgent());

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
		super.bindObject(object, "description", "passengerEmail", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		if (!super.getBuffer().getErrors().hasErrors("draftMode"))
			super.state(claim.isDraftMode(), "draftMode", "agent.claim.form.error.draftMode");
	}

	@Override
	public void perform(final Claim object) {
		Collection<TrackingLog> trackingLogs;

		trackingLogs = this.repository.findAllTrackingLogsByClaimId(object.getId());

		this.repository.deleteAll(trackingLogs);
		this.repository.delete(object);
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
		dataset.put("legFlightNumber", object.getLeg().getFlightNumber());
		dataset.put("legs", choicesLegs);
		dataset.put("leg", choicesLegs.getSelected().getKey());
		super.getResponse().addData(dataset);
	}
}
