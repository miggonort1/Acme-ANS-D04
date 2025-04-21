
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightListMineService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Flight> flights;
		int managerId = super.getRequest().getPrincipal().getAccountId();
		flights = this.repository.findAllFlightsByManagerId(managerId);
		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost");

		super.getResponse().addData(dataset);
	}
}
