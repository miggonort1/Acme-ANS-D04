
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
			super.state(context, false, "TrackingLog", "agent.trackingLog.validator.error.null");

		else if (!trackingLog.isDraftMode())
			super.state(context, !trackingLog.getClaim().isDraftMode(), "TrackingLog", "agent.trackingLog.validator.error.claim-not-published");

		else if (trackingLog.getStatus() != null && trackingLog.getResolution() != null && trackingLog.getClaim() != null) {

			if (trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() == 100.0)
				super.state(context, !trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "status", "agent.trackingLog.form.error.badStatus2");
			else
				super.state(context, trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "status", "agent.trackingLog.form.error.badStatus");

			if (trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
				super.state(context, trackingLog.getResolution() == null || trackingLog.getResolution().isBlank(), "resolution", "agent.trackingLog.form.error.badResolution");
			else
				super.state(context, trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank(), "resolution", "agent.trackingLog.form.error.badResolution2");

			Collection<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(trackingLog.getClaim().getId());

			if (trackingLogs != null && !trackingLogs.isEmpty()) {
				trackingLogs.stream().filter(t -> t.getResolutionPercentage() != null).sorted(Comparator.comparing(TrackingLog::getCreationMoment)).reduce((prev, curr) -> {
					if (prev.getResolutionPercentage() >= curr.getResolutionPercentage() && curr.getResolutionPercentage() != 100.0)
						super.state(context, false, "resolutionPercentage", "agent.trackingLog.validator.error.nonIncreasingPercentage");
					return curr;
				});

				List<TrackingLog> completedLogs = trackingLogs.stream().filter(t -> t.getResolutionPercentage() != null && t.getResolutionPercentage() == 100.0).sorted(Comparator.comparing(TrackingLog::getCreationMoment)).toList();

				if (completedLogs.size() > 2)
					super.state(context, false, "resolutionPercentage", "agent.trackingLog.form.error.maxcompletedGlobal");
				else if (completedLogs.size() == 2) {
					TrackingLog first = completedLogs.get(0);
					TrackingLog second = completedLogs.get(1);

					super.state(context, !first.isDraftMode(), "resolutionPercentage", "agent.trackingLog.form.error.firstMustBePublished");

					boolean sameStatus = first.getStatus().equals(second.getStatus());
					super.state(context, sameStatus, "status", "agent.trackingLog.form.error.mismatchedStatus");
				}
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}
