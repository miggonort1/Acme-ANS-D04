
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int customerId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		passengers = this.repository.findPassengersByCustomerId(customerId);
		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		String fullname = passenger.getFullName().length() > 50 ? passenger.getFullName().substring(0, 50) + "..." : passenger.getFullName();
		String email = passenger.getEmail().length() > 50 ? passenger.getEmail().substring(0, 50) + "..." : passenger.getEmail();

		dataset = super.unbindObject(passenger, "passportNumber", "draftMode");
		dataset.put("fullName", fullname);
		dataset.put("email", email);

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Passenger> passengers) {
		int masterId;
		Booking booking;
		final boolean showCreate;

		if (super.getRequest().hasData("masterId")) {
			masterId = super.getRequest().getData("masterId", int.class);
			booking = this.repository.findBookingById(masterId);
			showCreate = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

			super.getResponse().addGlobal("masterId", masterId);
			super.getResponse().addGlobal("showCreate", showCreate);
		}

	}
}
