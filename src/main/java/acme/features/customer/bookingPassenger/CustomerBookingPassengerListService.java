
package acme.features.customer.bookingPassenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.booking.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerListService extends AbstractGuiService<Customer, BookingPassenger> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<BookingPassenger> BookingPassengers;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		BookingPassengers = this.repository.findBookingPassengersByBookingId(masterId);

		super.getBuffer().addData(BookingPassengers);
	}

	@Override
	public void unbind(final Collection<BookingPassenger> BookingPassengers) {
		int masterId;
		Booking booking;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("draftMode", booking.isDraftMode());
	}

	@Override
	public void unbind(final BookingPassenger BookingPassenger) {
		Dataset dataset;

		Passenger passenger = BookingPassenger.getPassenger();

		String fullname = passenger.getFullName().length() > 50 ? passenger.getFullName().substring(0, 50) + "..." : passenger.getFullName();
		String email = passenger.getEmail().length() > 50 ? passenger.getEmail().substring(0, 50) + "..." : passenger.getEmail();

		dataset = super.unbindObject(BookingPassenger, "passenger.passportNumber");

		dataset.put("passenger.fullName", fullname);
		dataset.put("passenger.email", email);

		super.getResponse().addData(dataset);
	}
}
