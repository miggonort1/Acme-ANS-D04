
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;
		Manager manager;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(flightId);

		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		status = flight != null && flight.getManager().equals(manager) && flight.isDraftMode();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;
		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		int flightId = flight.getId();

		Collection<Leg> legs = this.repository.findAllLegsByFlightId(flightId);
		super.state(!legs.isEmpty(), "*", "acme.validation.manager.flight.no-legs");

		boolean allLegsPublished = legs.stream().allMatch(l -> !l.isDraftMode());
		super.state(allLegsPublished, "*", "acme.validation.manager.flight.legs-not-published");

		if (!super.getBuffer().getErrors().hasErrors("selfTransfer")) {
			Integer layovers = flight.getLayovers();
			if (flight.getSelfTransfer() == false && layovers > 0)
				super.state(false, "selfTransfer", "acme.validation.manager.flight.invalid-selfTransfer-layovers");
		}
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");

		dataset.put("scheduledDeparture", flight.getScheduledDeparture());
		dataset.put("scheduledArrival", flight.getScheduledArrival());
		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		dataset.put("layovers", flight.getLayovers());

		dataset.put("id", flight.getId());
		super.getResponse().addData(dataset);
	}
}
