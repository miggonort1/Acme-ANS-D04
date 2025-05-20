
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.flight.FlightRepository;
import acme.entities.flight.Leg;

@Validator
public class IataCodeValidatorLeg extends AbstractValidator<ValidIATACodeLeg, Leg> {

	@Autowired
	private FlightRepository repository;


	@Override
	protected void initialise(final ValidIATACodeLeg constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;
		Leg notUnique = this.repository.findLegByFlightNumber(leg.getFlightNumber());
		if (notUnique != null && !notUnique.equals(leg))
			super.state(context, false, "locatorCode", "validation.customer.uniqueIdentifier.message");
		result = !super.hasErrors(context);
		return result;
	}

}
