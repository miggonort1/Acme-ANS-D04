
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.features.customer.booking.CustomerBookingRepository;

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
			{
				if (StringHelper.matches(booking.getLocatorCode(), "^[A-Z0-9]{6,8}$")) {
					boolean uniqueBooking;
					Booking existingBooking;

					existingBooking = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
					uniqueBooking = existingBooking == null || existingBooking.equals(booking);

					super.state(context, uniqueBooking, "locatorCode", "acme.validation.booking.duplicated-booking.message");
				}
			}
			{
				//				boolean flightInFuture;
				//				Flight flight;
				//
				//				flight = booking.getFlight();
				//				flightInFuture = flight != null ? MomentHelper.isFuture(flight.getScheduledDeparture()) : true;
				//
				//				super.state(context, flightInFuture, "locatorCode", "acme.validation.booking.duplicated-booking.message");
			}
			{
				boolean flightInDraftMode;
				Flight flight;

				flight = booking.getFlight();
				flightInDraftMode = flight != null ? flight.isDraftMode() : true;

				super.state(context, flightInDraftMode, "flight", "acme.validation.booking.flight-in-draft-mode.message");
			}

		}

		result = !super.hasErrors(context);

		return result;
	}

}
