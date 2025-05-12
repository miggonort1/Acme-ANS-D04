
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Leg;
import acme.entities.flight.Status;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		Manager current = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		boolean status = leg != null && leg.getFlight().getManager().equals(current);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");

		SelectChoices statuses = SelectChoices.from(Status.class, leg.getStatus());
		dataset.put("status", statuses.getSelected().getKey());
		dataset.put("statuses", statuses);

		Collection<Airport> airports = this.repository.findAllAirports();
		SelectChoices departureChoices = SelectChoices.from(airports, "name", leg.getDepartureAirport());
		SelectChoices arrivalChoices = SelectChoices.from(airports, "name", leg.getArrivalAirport());

		dataset.put("departureAirport", departureChoices.getSelected().getKey());
		dataset.put("departureAirportChoices", departureChoices);
		dataset.put("arrivalAirport", arrivalChoices.getSelected().getKey());
		dataset.put("arrivalAirportChoices", arrivalChoices);

		Collection<Aircraft> aircrafts = this.repository.findAllAircrafts();
		SelectChoices aircraftChoices = SelectChoices.from(aircrafts, "model", leg.getAircraft());

		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircraftChoices", aircraftChoices);
		dataset.put("durationHours", leg.getDurationInHours());
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("draftMode", leg.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
