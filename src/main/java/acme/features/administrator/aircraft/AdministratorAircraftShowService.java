
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airline.AirlineRepository;

@GuiService
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository	repository;

	@Autowired
	private AirlineRepository				airlineRepository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Aircraft aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		SelectChoices airlinesChoices = SelectChoices.from(this.airlineRepository.findAllAirlines(), "name", aircraft.getAirline());

		Dataset dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
		dataset.put("statusChoices", statusChoices);
		dataset.put("airlinesChoices", airlinesChoices);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}
}
