
package acme.features.customer.bookingPassenger;

import java.util.Collection;

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
		boolean status = true;
		Integer masterId, passengerId;
		Booking booking;
		Customer customer;
		Passenger passenger;
		Collection<BookingPassenger> existingAssignments;

		try {
			masterId = super.getRequest().getData("masterId", int.class);
			booking = this.repository.findBookingById(masterId);
			customer = booking == null ? null : booking.getCustomer();

			status = booking != null && booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(customer);

			if (status && super.getRequest().hasData("passenger")) {
				passengerId = super.getRequest().getData("passenger", int.class);
				passenger = this.repository.findPassengerById(passengerId);

				status = status && passenger != null;

				status = status && passenger.getCustomer().equals(customer);

				status = status && !passenger.isDraftMode();

				status = status && passenger.getDateOfBirth().before(booking.getPurchaseMoment());

				existingAssignments = this.repository.findAssignationFromBookingIdAndPassengerId(masterId, passengerId);
				status = status && existingAssignments.isEmpty();
			}
		} catch (Exception e) {
			status = false;
		}

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
		;

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
		int bookingId;

		Dataset dataset;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		bookingId = BookingPassenger.getBooking().getId();

		passengers = this.repository.findAllValidPassengersForBooking(customer.getId(), bookingId);

		passengerChoices = SelectChoices.from(passengers, "fullNameAndPassportNumber", BookingPassenger.getPassenger());

		dataset = super.unbindObject(BookingPassenger, "booking", "passenger");
		dataset.put("passenger", passengerChoices.getSelected().getKey());
		dataset.put("passengers", passengerChoices);

		dataset.put("masterId", super.getRequest().getData("masterId", int.class));

		super.getResponse().addData(dataset);
	}
}
