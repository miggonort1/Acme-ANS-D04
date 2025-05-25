
package acme.features.manager.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Leg;
import acme.entities.flight.Status;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);

		status = leg != null && leg.isDraftMode() && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getManager());

		if (status && !super.getRequest().getMethod().equals("GET")) {

			int depId = super.getRequest().getData("departureAirport", int.class);
			int arrId = super.getRequest().getData("arrivalAirport", int.class);
			int planeId = super.getRequest().getData("aircraft", int.class);

			int flightId = leg.getFlight().getId();
			int managerId = leg.getFlight().getManager().getId();

			boolean validDep = depId == 0 || this.repository.existsAirportInFlight(flightId, depId);
			boolean validArr = arrId == 0 || this.repository.existsAirportInFlight(flightId, arrId);
			boolean validPlane = planeId == 0 || this.repository.existsAircraftOfManager(managerId, planeId);

			status = validDep && validArr && validPlane;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		int departureAirportId = super.getRequest().getData("departureAirport", int.class);
		int arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		int aircraftId = super.getRequest().getData("aircraft", int.class);

		Airport departure = this.repository.findAirportById(departureAirportId);
		Airport arrival = this.repository.findAirportById(arrivalAirportId);
		Aircraft aircraft = this.repository.findAircraftById(aircraftId);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		leg.setDepartureAirport(departure);
		leg.setArrivalAirport(arrival);
		leg.setAircraft(aircraft);
	}

	@Override
	public void validate(final Leg leg) {

		if (!super.getBuffer().getErrors().hasErrors("scheduledDeparture")) {
			boolean isPastOrPresent = MomentHelper.isPresentOrPast(leg.getScheduledDeparture());
			super.state(!isPastOrPresent, "scheduledDeparture", "acme.validation.airline-manager.leg.departure-in-the-past");
		}

		if (!super.getBuffer().getErrors().hasErrors("scheduledArrival")) {
			boolean isPastOrPresent = MomentHelper.isPresentOrPast(leg.getScheduledArrival());
			super.state(!isPastOrPresent, "scheduledArrival", "acme.validation.airline-manager.leg.arrival-in-the-past");
		}

		if (!super.getBuffer().getErrors().hasErrors("arrivalAirport") && !super.getBuffer().getErrors().hasErrors("departureAirport")) {
			boolean sameAirport = leg.getDepartureAirport().equals(leg.getArrivalAirport());
			super.state(!sameAirport, "arrivalAirport", "acme.validation.airline-manager.leg.departure-equals-arrival");
		}

		if (!super.getBuffer().getErrors().hasErrors("departureAirport")) {
			int flightId = leg.getFlight().getId();
			int legId = leg.getId();
			List<Leg> previousLegs = this.repository.findPreviousLeg(flightId, legId);
			Leg previousLeg = previousLegs.isEmpty() ? null : previousLegs.get(0);

			if (previousLeg != null) {
				boolean isConnected = previousLeg.getArrivalAirport().equals(leg.getDepartureAirport());
				super.state(isConnected, "departureAirport", "acme.validation.airline-manager.leg.not-connected-to-previous");
			}
		}
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		SelectChoices statuses;
		SelectChoices departureAirportChoices;
		SelectChoices arrivalAirportChoices;
		SelectChoices aircraftChoices;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");

		statuses = SelectChoices.from(Status.class, leg.getStatus());
		dataset.put("status", statuses.getSelected().getKey());
		dataset.put("statuses", statuses);

		Collection<Airport> airports = this.repository.findAllAirports();
		Collection<Aircraft> aircrafts = this.repository.findAllAircrafts();

		departureAirportChoices = SelectChoices.from(airports, "name", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "name", leg.getArrivalAirport());
		aircraftChoices = SelectChoices.from(aircrafts, "model", leg.getAircraft());

		if (leg.getDepartureAirport() != null)
			dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());

		if (leg.getArrivalAirport() != null)
			dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());

		if (leg.getAircraft() != null)
			dataset.put("aircraft", aircraftChoices.getSelected().getKey());

		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("draftMode", leg.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
