
package acme.constraints;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.TrackingLog;
import acme.entities.claim.TrackingLogStatus;
import acme.features.agent.trackingLog.AgentTrackingLogRepository;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


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

		else if (!trackingLog.isDraftMode())
			super.state(context, !trackingLog.getClaim().isDraftMode(), "*", "El claim debe estar publicado para publicar el tracking log.");

		else if (trackingLog.getStatus() != null && trackingLog.getResolution() != null && trackingLog.getClaim() != null) {

			if (trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() == 100.0)
				super.state(context, !trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "Status", "El estado no puede ser PENDING");
			else
				super.state(context, trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "Status", "El estado debe ser PENDING");

			if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
				super.state(context, trackingLog.getResolution() == null || trackingLog.getResolution().isBlank(), "Resolution", "El campo resolution debe quedar vacío hasta la finalización del tracking log");
			else
				super.state(context, trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank(), "Resolution", "El campo resolucion no debe quedar vacío si se ha finalizado el tracking log");

			Collection<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(trackingLog.getClaim().getId());

			if (trackingLogs != null && !trackingLogs.isEmpty()) {

				trackingLogs.stream().filter(t -> t.getResolutionPercentage() != null).sorted(Comparator.comparing(TrackingLog::getCreationMoment)).reduce((prev, curr) -> {
					if (prev.getResolutionPercentage() >= curr.getResolutionPercentage())
						super.state(context, false, "resolutionPercentage", "Los porcentajes de resolución deben ser estrictamente crecientes respecto al momento de creación.");
					return curr;
				});

				List<TrackingLog> completedLogs = trackingLogs.stream().filter(t -> t.getResolutionPercentage() != null && t.getResolutionPercentage() == 100.0).sorted(Comparator.comparing(TrackingLog::getCreationMoment)).toList();

				if (completedLogs.size() > 2)
					super.state(context, false, "resolutionPercentage", "No puede haber más de dos registros de seguimiento completados (100%).");
				else if (completedLogs.size() == 2) {
					TrackingLog first = completedLogs.get(0);
					TrackingLog second = completedLogs.get(1);

					super.state(context, !first.isDraftMode(), "resolutionPercentage", "El primer tracking log con resolución al 100% debe estar publicado.");

					boolean sameStatus = first.getStatus().equals(second.getStatus());
					super.state(context, sameStatus, "status", "El segundo tracking log con 100% de resolución debe tener el mismo estado que el primero.");
				}
			}

		}
		result = !super.hasErrors(context);
		return result;
	}
}
