
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		int managerId;
		Flight flight;
		Manager manager;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(flightId);

		managerId = super.getRequest().getPrincipal().getAccountId();
		manager = this.repository.findManagerById(managerId);

		status = flight != null && flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);
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
		Integer managerId;
		Manager manager;

		managerId = super.getRequest().getPrincipal().getAccountId();
		manager = this.repository.findManagerById(managerId);

		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
		flight.setManager(manager);
	}

	@Override
	public void validate(final Flight flight) {
		// Add any necessary validation logic
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Integer managerId;
		Manager manager;
		Dataset dataset;

		managerId = super.getRequest().getPrincipal().getAccountId();
		manager = this.repository.findManagerById(managerId);

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description");
		dataset.put("manager", manager);

		super.getResponse().addData(dataset);
	}
}
