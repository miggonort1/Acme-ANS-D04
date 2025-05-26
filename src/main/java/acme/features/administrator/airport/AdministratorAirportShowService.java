
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.OperationalScope;

@GuiService
public class AdministratorAirportShowService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		Airport airport;
		Integer id;
		boolean isAuthorised = false;
		boolean status;
		boolean exists = false;

		id = super.getRequest().getData("id", Integer.class);
		if (id != null) {
			airport = this.repository.findAirportById(id);
			exists = airport != null;
			isAuthorised = airport != null && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		}
		status = exists && isAuthorised;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		SelectChoices statuses;
		Dataset dataset;

		statuses = SelectChoices.from(OperationalScope.class, airport.getScope());

		dataset = super.unbindObject(airport, "name", "iataCode", "scope", "city", "country", "website", "email", "phoneNumber");
		dataset.put("statuses", statuses);

		super.getResponse().addData(dataset);
	}

}
