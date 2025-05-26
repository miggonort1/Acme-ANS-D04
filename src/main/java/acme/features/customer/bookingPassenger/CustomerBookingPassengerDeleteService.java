
package acme.features.customer.bookingPassenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerDeleteService extends AbstractGuiService<Customer, BookingPassenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		BookingPassenger BookingPassenger;

		id = super.getRequest().getData("id", int.class);
		BookingPassenger = this.repository.findBookingPassengerById(id);
		status = BookingPassenger != null && BookingPassenger.getBooking().isDraftMode() && super.getRequest().getPrincipal().hasRealm(BookingPassenger.getBooking().getCustomer());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		BookingPassenger BookingPassenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		BookingPassenger = this.repository.findBookingPassengerById(id);

		super.getBuffer().addData(BookingPassenger);
	}

	@Override
	public void bind(final BookingPassenger bookingPassenger) {
		;
	}
	@Override
	public void validate(final BookingPassenger BookingPassenger) {
		assert BookingPassenger != null;

	}

	@Override
	public void perform(final BookingPassenger BookingPassenger) {
		this.repository.delete(BookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger BookingPassenger) {

	}

}
