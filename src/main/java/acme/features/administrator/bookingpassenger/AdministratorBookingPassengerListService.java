
package acme.features.administrator.bookingpassenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;

@GuiService
public class AdministratorBookingPassengerListService extends AbstractGuiService<Administrator, BookingPassenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Booking booking;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);
		status = booking != null && !booking.isDraftMode();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<BookingPassenger> bookingPassengers;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		bookingPassengers = this.repository.findBookingPassengersByBookingId(masterId);

		super.getBuffer().addData(bookingPassengers);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {
		Dataset dataset;

		dataset = super.unbindObject(bookingPassenger, "passenger.fullName", "passenger.email", "passenger.passportNumber");
		super.addPayload(dataset, bookingPassenger, "passenger.dateOfBirth", "passenger.specialNeeds");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<BookingPassenger> bookingPassenger) {
		int masterId;
		Booking booking;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		super.getResponse().addGlobal("masterId", masterId);
	}

}
