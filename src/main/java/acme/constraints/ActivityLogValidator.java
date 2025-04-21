
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flightassignment.ActivityLog;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	// Internal state ---------------------------------------------------------------------

	// ConstraintValidator interface ------------------------------------------------------

	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		if (activityLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean fechaTrasLeg;

			fechaTrasLeg = MomentHelper.isAfterOrEqual(activityLog.getRegistrationMoment(), activityLog.getFlightAssignment().getLeg().getScheduledArrival());
			super.state(context, fechaTrasLeg, "fechaActivityLog", "acme.validation.activityLog.beforeLeg");
		}
		result = !super.hasErrors(context);

		return result;
	}
}
