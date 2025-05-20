
package acme.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = BookingPassengerValidator.class)
@Target({
	TYPE
})
@Retention(RUNTIME)
public @interface ValidBookingPassenger {

	String message() default "acme.validation.booking-passenger.invalid-date-of-birth";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
