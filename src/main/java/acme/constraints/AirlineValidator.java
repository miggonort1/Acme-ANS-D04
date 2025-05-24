
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineRepository;

@Validator
public class AirlineValidator extends AbstractValidator<ValidAirline, Airline> {

	@Autowired
	private AirlineRepository repository;


	@Override
	protected void initialise(final ValidAirline annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (airline == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			result = false;
		} else {
			Airline existingAirline = this.repository.findAirlineByIataCode(airline.getIataCode());
			boolean uniqueIataCode = existingAirline == null || existingAirline.equals(airline);
			super.state(context, uniqueIataCode, "iataCode", "acme.validation.airline.iataCode.unique.message");

			result = !super.hasErrors(context);
		}

		return result;
	}
}
