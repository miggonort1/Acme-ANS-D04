
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLogStatus;

@GuiService
public class AdministratorClaimListService extends AbstractGuiService<Administrator, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Claim> claims;

		claims = this.repository.findClaims();

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim object) {
		Dataset dataset;
		TrackingLogStatus choicesStatus;

		choicesStatus = object.getStatus();

		dataset = super.unbindObject(object, "registrationMoment", "description", "type", "draftMode");
		dataset.put("status", choicesStatus);
		super.getResponse().addData(dataset);
	}

}
