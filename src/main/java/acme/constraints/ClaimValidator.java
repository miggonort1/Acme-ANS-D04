
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.entities.claim.Claim;

public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	@Override
	protected void initialise(final ValidClaim annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {
		boolean result;

		assert context != null;

		if (claim == null) {
			super.state(context, false, "*", "agent.claim.validator.error.null");
			return false;
		}

		Date registrationMoment = claim.getRegistrationMoment();
		Date workStartMoment = claim.getAgent().getMoment();
		boolean consistentMoment = registrationMoment != null && workStartMoment != null && registrationMoment.after(workStartMoment);
		super.state(context, consistentMoment, "registrationMoment", "agent.claim.validator.error.registrationMoment");

		if (claim.getLeg() == null)
			super.state(context, false, "leg", "agent.claim.validator.error.nullLeg");
		else if (!claim.getLeg().isDraftMode()) {
			boolean validLegStatus = registrationMoment.after(claim.getLeg().getScheduledDeparture());
			super.state(context, validLegStatus, "leg", "agent.claim.validator.error.departureAfter");
		} else
			super.state(context, false, "leg", "agent.claim.validator.error.legNotPublished");

		result = !super.hasErrors(context);
		return result;
	}

}
