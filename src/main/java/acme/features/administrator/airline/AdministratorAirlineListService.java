
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;

@GuiService
public class AdministratorAirlineListService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratorAirlineRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Airline> airlines;
		int airlineId;

		airlineId = super.getRequest().getPrincipal().getActiveRealm().getId();

		airlines = this.repository.findAllAirlinesByAdministrator(airlineId);

		super.getBuffer().addData(airlines);

	}

	@Override
	public void unbind(final Airline airline) {
		Dataset dataset;

		dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "emailAdress", "phoneNumber");
		super.getResponse().addData(dataset);
	}

}
