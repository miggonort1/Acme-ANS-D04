
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
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		boolean consistentRegistrationMoment = this.validateRegistrationMoment(claim, context);
		boolean validLegStatus = this.validateLegStatus(claim, context);
		result = consistentRegistrationMoment && validLegStatus && !super.hasErrors(context);

		return result;
	}

	private boolean validateRegistrationMoment(final Claim claim, final ConstraintValidatorContext context) {
		Date registrationMoment = claim.getRegistrationMoment();
		Date workStartMoment = claim.getAgent().getMoment();

		boolean consistentMoment = registrationMoment.after(workStartMoment);

		super.state(context, consistentMoment, "registrationMoment", "acme.validation.claim.registrationMoment.message");

		return consistentMoment;
	}

	private boolean validateLegStatus(final Claim claim, final ConstraintValidatorContext context) {
		if (claim.getLeg() == null) {
			super.state(context, false, "leg", "javax.validation.constraints.NotNull.message");
			return false;
		}

		boolean validLegStatus = claim.getRegistrationMoment().after(claim.getLeg().getScheduledArrival());
		super.state(context, validLegStatus, "leg", "assistanceAgent.claim.form.error.badLeg");

		return validLegStatus;
	}

}
