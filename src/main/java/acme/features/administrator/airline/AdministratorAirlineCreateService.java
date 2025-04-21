
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineRepository;
import acme.entities.airline.Type;

@GuiService
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airline airline;

		airline = new Airline();

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "emailAdress", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices types;
		Dataset dataset;

		types = SelectChoices.from(Type.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "emailAdress", "phoneNumber");
		dataset.put("confirmation", false);
		dataset.put("types", types);

		super.getResponse().addData(dataset);
	}

}
