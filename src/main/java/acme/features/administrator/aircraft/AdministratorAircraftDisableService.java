
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
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

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

		aircraft.setStatus(AircraftStatus.UNDER_MAINTENANCE);
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		Aircraft old = this.repository.findAircraftByRegistrationNumber(aircraft.getRegistrationNumber());
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		if (old != null) {
			boolean sameId = aircraft.getId() == old.getId();
			super.state(sameId, "registrationNumber", "acme.validation.registrationNumber");
		}

		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		SelectChoices airlinesChoices = SelectChoices.from(this.airlineRepository.findAllAirlines(), "name", aircraft.getAirline());

		Dataset dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
		dataset.put("statusChoices", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());
		dataset.put("airlinesChoices", airlinesChoices);
		dataset.put("airline", airlinesChoices.getSelected().getKey());
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
