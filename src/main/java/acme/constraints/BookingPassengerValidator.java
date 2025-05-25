
package acme.constraints;

import java.util.Collection;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.validation.AbstractValidator;
import acme.entities.booking.BookingPassenger;
import acme.features.customer.bookingPassenger.CustomerBookingPassengerRepository;

public class BookingPassengerValidator extends AbstractValidator<ValidBookingPassenger, BookingPassenger> {

	@Autowired
	private CustomerBookingPassengerRepository repository;


	@Override
	public boolean isValid(final BookingPassenger bookingPassenger, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (bookingPassenger == null || bookingPassenger.getPassenger() == null || bookingPassenger.getBooking() == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}
		// Validación 0: Passenger debe estar publicado
		final boolean isPublished = !bookingPassenger.getPassenger().isDraftMode();
		super.state(context, isPublished, "passenger", "acme.validation.booking-passenger.unpublished-passenger");
		result = result && isPublished;

		// Validación 1: Fecha de nacimiento < fecha de compra
		final Date dateOfBirth = bookingPassenger.getPassenger().getDateOfBirth();
		final Date purchaseMoment = bookingPassenger.getBooking().getPurchaseMoment();

		final boolean validBirth = dateOfBirth != null && purchaseMoment != null && dateOfBirth.before(purchaseMoment);
		super.state(context, validBirth, "passenger.dateOfBirth", "acme.validation.booking-passenger.invalid-date-of-birth");

		result = validBirth;

		// Validación 2: No duplicar pasajeros en un mismo booking
		final int bookingId = bookingPassenger.getBooking().getId();
		final int passengerId = bookingPassenger.getPassenger().getId();
		final Integer currentId = bookingPassenger.getId();

		final Collection<BookingPassenger> existing = this.repository.findAssignationFromBookingIdAndPassengerId(bookingId, passengerId);
		final boolean isDuplicate = existing.stream().anyMatch(bp -> currentId == null || bp.getId() != currentId);

		super.state(context, !isDuplicate, "passenger", "acme.validation.booking-passenger.duplicate-passenger");

		result = result && !isDuplicate;

		// Validación 3: El passenger debe pertenecer al mismo customer que el booking
		final int bookingCustomerId = bookingPassenger.getBooking().getCustomer().getId();
		final int passengerCustomerId = bookingPassenger.getPassenger().getCustomer().getId();
		final boolean sameCustomer = bookingCustomerId == passengerCustomerId;
		super.state(context, sameCustomer, "passenger", "acme.validation.booking-passenger.passenger-from-different-customer");
		result = result && sameCustomer;

		// Validación 4: El precio total del booking no debe superar 1.000.000,00
		Money price = bookingPassenger.getBooking().getPrice();
		boolean validPrice = price != null && price.getAmount() <= 1000000.00;
		super.state(context, validPrice, "passenger", "acme.validation.booking-passenger.excessive-price");
		result = result && validPrice;

		return result;
	}
}
