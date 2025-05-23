
package acme.features.customer.bookingPassenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerShowService extends AbstractGuiService<Customer, BookingPassenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int BookingPassengerId;
		BookingPassenger BookingPassenger;

		BookingPassengerId = super.getRequest().getData("id", int.class);
		BookingPassenger = this.repository.findBookingPassengerById(BookingPassengerId);
		status = BookingPassenger != null && super.getRequest().getPrincipal().hasRealm(BookingPassenger.getBooking().getCustomer());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int BookingPassengerId;
		BookingPassenger BookingPassenger;

		BookingPassengerId = super.getRequest().getData("id", int.class);
		BookingPassenger = this.repository.findBookingPassengerById(BookingPassengerId);

		super.getBuffer().addData(BookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger BookingPassenger) {

		Dataset dataset;

		dataset = super.unbindObject(BookingPassenger, "booking", "passenger", "passenger.fullName", "passenger.email", "passenger.passportNumber", "passenger.dateOfBirth", "passenger.specialNeeds");
		dataset.put("draftMode", BookingPassenger.getBooking().isDraftMode());

		super.getResponse().addData(dataset);

	}
}
