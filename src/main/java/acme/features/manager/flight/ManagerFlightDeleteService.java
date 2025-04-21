
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
		boolean status;
		int flightId;
		Flight flight;
		Manager manager;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(flightId);
		manager = flight == null ? null : flight.getManager();

		// El vuelo solo se puede eliminar si está en modo borrador y el usuario es su creador
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

		Collection<Leg> legs;

		// Eliminar primero los legs asociados al vuelo
		legs = this.repository.findAllLegsByFlightId(flight.getId());
		this.repository.deleteAll(legs);

		// Luego eliminar el vuelo
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;

		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description");

		super.getResponse().addData(dataset);
	}

}
