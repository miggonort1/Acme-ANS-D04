
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
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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
	public void unbind(final Flight flight) {
		Dataset dataset;
		Collection<Leg> legs;
		boolean canBePublished;

		legs = this.repository.findAllLegsByFlightId(flight.getId());
		canBePublished = !legs.isEmpty() && this.repository.areAllLegsPublished(flight.getId());

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "manager");
		dataset.put("legs", legs);
		dataset.put("canBePublished", canBePublished);

		super.getResponse().addData(dataset);
	}
}
