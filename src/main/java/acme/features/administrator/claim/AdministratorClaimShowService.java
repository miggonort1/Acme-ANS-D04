
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLogStatus;
import acme.entities.claim.Type;
import acme.entities.flight.Leg;

@GuiService
public class AdministratorClaimShowService extends AbstractGuiService<Administrator, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;

		masterId = super.getRequest().getData("id", int.class);

		claim = this.repository.findClaimById(masterId);

		status = claim != null && !claim.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
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
