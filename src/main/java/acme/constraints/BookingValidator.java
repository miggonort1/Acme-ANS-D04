
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.features.customer.booking.CustomerBookingRepository;
import acme.features.customer.passenger.CustomerPassengerRepository;

public class BookingValidator extends AbstractValidator<ValidBooking, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (booking == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			if (StringHelper.matches(booking.getLocatorCode(), "^[A-Z0-9]{6,8}$")) {
				Booking existingBooking = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
				boolean uniqueBooking = existingBooking == null || existingBooking.equals(booking);
				super.state(context, uniqueBooking, "locatorCode", "acme.validation.booking.duplicated-booking.message");
			}

			Flight flight = booking.getFlight();
			if (flight != null) {
				Date referenceMoment = booking.getPurchaseMoment();

				boolean validLegs = this.repository.findInvalidLegsForFlight(flight.getId(), referenceMoment).isEmpty();
				super.state(context, validLegs, "flight", "acme.validation.booking.invalid-legs.message");

				boolean flightNotInDraft = !flight.isDraftMode();
				super.state(context, flightNotInDraft, "flight", "acme.validation.booking.flight-in-draft-mode.message");
			}
			if (!booking.isDraftMode()) {
				int numPassengers = booking.getNumberOfPassengers();
				super.state(context, numPassengers > 0, "*", "acme.validation.booking.no-passengers.message");
				CustomerPassengerRepository passengerRepo = SpringHelper.getBean(CustomerPassengerRepository.class);
				var passengers = passengerRepo.findPassengersByBookingId(booking.getId());
				boolean allPassengersPublished = passengers.stream().allMatch(p -> !p.isDraftMode());
				super.state(context, allPassengersPublished, "*", "acme.validation.booking.passengers-in-draft.message");

			}
		}

		result = !super.hasErrors(context);
		return result;
	}

}
