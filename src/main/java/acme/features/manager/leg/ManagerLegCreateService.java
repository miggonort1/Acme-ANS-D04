
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.entities.flight.Status;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		String method = super.getRequest().getMethod();

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		status = flight != null && flight.isDraftMode() && flight.getManager().getId() == managerId;

		if (status && "POST".equals(method)) {
			int depId = super.getRequest().getData("departureAirport", int.class);
			int arrId = super.getRequest().getData("arrivalAirport", int.class);
			int planeId = super.getRequest().getData("aircraft", int.class);

			boolean validDep = depId == 0 || this.repository.findAirportById(depId) != null;
			boolean validArr = arrId == 0 || this.repository.findAirportById(arrId) != null;
			boolean validPlane = planeId == 0 || this.repository.existsAircraftOfManager(managerId, planeId);

			status = validDep && validArr && validPlane;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.findFlightById(masterId);

		Leg leg = new Leg();
		leg.setFlight(flight);
		leg.setStatus(Status.ON_TIME);
		leg.setDraftMode(true);
		super.getBuffer().addData(leg);
		super.getResponse().addGlobal("allowCreate", true);
	}

	@Override
	public void bind(final Leg leg) {
		int masterId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.findFlightById(masterId);
		leg.setFlight(flight);

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
			super.state(!isPastOrPresent, "scheduledDeparture", "acme.validation.manager.leg.departure-in-the-past");
		}

		if (!super.getBuffer().getErrors().hasErrors("scheduledArrival")) {
			boolean isPastOrPresent = MomentHelper.isPresentOrPast(leg.getScheduledArrival());
			super.state(!isPastOrPresent, "scheduledArrival", "acme.validation.manager.leg.arrival-in-the-past");
		}

		if (!super.getBuffer().getErrors().hasErrors("arrivalAirport") && !super.getBuffer().getErrors().hasErrors("departureAirport")) {
			boolean sameAirport = leg.getDepartureAirport().equals(leg.getArrivalAirport());
			super.state(!sameAirport, "arrivalAirport", "acme.validation.manager.leg.departure-equals-arrival");
		}

		if (!super.getBuffer().getErrors().hasErrors("departureAirport")) {
			int flightId = leg.getFlight().getId();
			Collection<Leg> existingLegs = this.repository.findLegsByFlightId(flightId);

			if (!existingLegs.isEmpty()) {
				Leg lastLeg = existingLegs.stream().max((l1, l2) -> l1.getScheduledDeparture().compareTo(l2.getScheduledDeparture())).orElse(null);

				boolean isConnected = lastLeg != null && lastLeg.getArrivalAirport().equals(leg.getDepartureAirport());

				super.state(isConnected, "departureAirport", "acme.validation.manager.leg.not-connected-to-previous");
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

		dataset.put("departureAirportChoices", departureAirportChoices);
		dataset.put("arrivalAirportChoices", arrivalAirportChoices);
		dataset.put("aircraftChoices", aircraftChoices);
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("draftMode", leg.getFlight().isDraftMode());

		super.getResponse().addData(dataset);
	}
}
