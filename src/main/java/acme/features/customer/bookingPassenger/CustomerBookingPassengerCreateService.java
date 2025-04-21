
package acme.features.customer.bookingPassenger;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.booking.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerCreateService extends AbstractGuiService<Customer, BookingPassenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Booking booking;
		Customer customer;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);
		customer = booking == null ? null : booking.getCustomer();
		status = booking != null && booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(customer);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		BookingPassenger BookingPassenger;
		Booking booking;
		int masterId;

		BookingPassenger = new BookingPassenger();

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		BookingPassenger.setBooking(booking);

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
		{
			boolean passengerPublished;

			passengerPublished = !BookingPassenger.getPassenger().isDraftMode();
			super.state(passengerPublished, "passenger", "acme.validation.BookingPassenger.passenger.draftMode.message");
		}
		{
			boolean alreadyAssigned;

			List<BookingPassenger> BookingPassengers = this.repository.findAssignationFromBookingIdAndPassengerId(BookingPassenger.getBooking().getId(), BookingPassenger.getPassenger().getId()).stream().toList();

			alreadyAssigned = BookingPassengers.isEmpty();

			super.state(alreadyAssigned, "passenger", "acme.validation.BookingPassenger.alreadyAssigned.message");
		}
	}

	@Override
	public void perform(final BookingPassenger BookingPassenger) {
		this.repository.save(BookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger BookingPassenger) {
		Collection<Passenger> passengers;

		SelectChoices passengerChoices;
		Customer customer;

		Dataset dataset;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		passengers = this.repository.findAllPublishedPassengersFromCustomerId(customer.getId());

		passengerChoices = SelectChoices.from(passengers, "passportNumber", BookingPassenger.getPassenger());

		dataset = super.unbindObject(BookingPassenger, "booking", "passenger");
		dataset.put("passenger", passengerChoices.getSelected().getKey());
		dataset.put("passengers", passengerChoices);

		dataset.put("masterId", super.getRequest().getData("masterId", int.class));

		super.getResponse().addData(dataset);
	}
}
