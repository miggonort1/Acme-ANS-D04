
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
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		int masterId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(masterId);

		Manager current = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		boolean status = flight != null && flight.getManager().equals(current) && flight.isDraftMode();

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
		assert flight != null;

		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		int assignmentCount = this.repository.countAssignmentsByFlightId(flight.getId());

		if (assignmentCount > 0)
			super.state(false, "*", "acme.validation.manager.flight.delete-has-assignments");
	}

	@Override
	public void perform(final Flight flight) {
		Collection<Leg> legs;

		legs = this.repository.findAllLegsByFlightId(flight.getId());
		this.repository.deleteAll(legs);
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description");

		super.getResponse().addData(dataset);
	}

}
