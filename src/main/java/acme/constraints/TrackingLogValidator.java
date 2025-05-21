
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.TrackingLogStatus;
import acme.entities.claim.TrackingLog;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		boolean result;

		assert context != null;

		if (trackingLog == null)
			super.state(context, false, "TrackingLog", "No hay trackingLogs");
		else if (trackingLog.getStatus() != null && trackingLog.getResolution() != null && trackingLog.getClaim() != null) {

			if (trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() == 100.0)
				super.state(context, !trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "Status", "El estado no puede ser PENDING");
			else
				super.state(context, trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "Status", "El estado debe ser PENDING");

			if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
				super.state(context, trackingLog.getResolution() == null || trackingLog.getResolution().isBlank(), "Resolution", "El campo resolution debe quedar vacío hasta la finalización del tracking log");
			else
				super.state(context, trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank(), "Resolution", "El campo resolucion no debe quedar vacío si se ha finalizado el tracking log");

		}
		result = !super.hasErrors(context);
		return result;
	}
}
