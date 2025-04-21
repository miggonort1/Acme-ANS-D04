
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightCreateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Flight flight = new Flight();
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Manager manager = this.repository.findManagerById(managerId);
		flight.setManager(manager);
		flight.setDraftMode(true);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Manager manager = this.repository.findManagerById(managerId);

		super.bindObject(flight, "tag", "selfTransfer", "cost", "description", "manager");
		flight.setManager(manager);
	}

	@Override
	public void validate(final Flight flight) {
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Integer managerId;
		Manager manager;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		manager = this.repository.findManagerById(managerId);

		Dataset dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description");
		dataset.put("manager", manager);

		super.getResponse().addData(dataset);
	}
}
