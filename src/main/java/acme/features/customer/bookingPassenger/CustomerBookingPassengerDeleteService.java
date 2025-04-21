
package acme.features.customer.bookingPassenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.entities.booking.Passenger;
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
	public void bind(final BookingPassenger BookingPassenger) {
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("passenger", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.bindObject(BookingPassenger, "passenger");

		BookingPassenger.setPassenger(passenger);
	}

	@Override
	public void validate(final BookingPassenger BookingPassenger) {
		;
	}

	@Override
	public void perform(final BookingPassenger BookingPassenger) {
		this.repository.delete(BookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger BookingPassenger) {
		Dataset dataset;

		dataset = super.unbindObject(BookingPassenger, "passenger", "passenger.fullName", "passenger.email", "passenger.passportNumber", "passenger.birthDate", "passenger.specialNeeds");
		dataset.put("passenger", BookingPassenger.getPassenger().getId());
		dataset.put("draftMode", BookingPassenger.getBooking().isDraftMode());

		super.getResponse().addData(dataset);
	}

}
