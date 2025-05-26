
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
		Manager current = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		boolean status = current != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		Manager manager;

		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		flight = new Flight();
		flight.setDraftMode(true);
		flight.setManager(manager);
		flight.setSelfTransfer(true);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		assert flight != null;
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;

	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");

		super.getResponse().addData(dataset);
	}
}
