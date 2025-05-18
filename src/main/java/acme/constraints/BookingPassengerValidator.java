
package acme.constraints;

import java.util.Collection;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

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

		return result;
	}
}
