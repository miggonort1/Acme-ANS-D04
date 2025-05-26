
package acme.features.agent.trackingLog;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.TrackingLog;
import acme.entities.claim.TrackingLogStatus;
import acme.realms.Agent;

@GuiService
public class AgentTrackingLogPublishService extends AbstractGuiService<Agent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);
		status = trackingLog != null && trackingLog.isDraftMode() && super.getRequest().getPrincipal().hasRealm(trackingLog.getClaim().getAgent());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog object) {
		assert object != null;

		super.bindObject(object, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog object) {
		assert object != null;

		if (object.getClaim() != null) {
			Collection<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(object.getClaim().getId());
			List<TrackingLog> completedLogs = this.repository.findCompletedTrackingLogsByClaimId(object.getClaim().getId()).stream().sorted(Comparator.comparing(TrackingLog::getCreationMoment)).toList();

			super.state(completedLogs.size() <= 2, "*", "agent.trackingLog.form.error.maxcompletedGlobal");

			Optional<TrackingLog> recentTrackingLogOpt = trackingLogs.stream().filter(t -> t.getId() != object.getId()).sorted(Comparator.comparing(TrackingLog::getCreationMoment).reversed()).findFirst();

			if (object.getResolutionPercentage() != null && recentTrackingLogOpt.isPresent()) {
				TrackingLog recentTrackingLog = recentTrackingLogOpt.get();
				Double previous = recentTrackingLog.getResolutionPercentage();
				Double current = object.getResolutionPercentage();

				boolean bothCompleted = previous != null && previous == 100.0 && current == 100.0;
				boolean validIncrement = previous == null || previous < current;

				if (bothCompleted)
					super.state(completedLogs.size() <= 2, "resolutionPercentage", "agent.trackingLog.form.error.maxcompleted");
				else if (recentTrackingLog.getCreationMoment().before(object.getCreationMoment()))
					super.state(validIncrement, "resolutionPercentage", "agent.trackingLog.form.error.badPercentage");
			}

			if (completedLogs.size() == 2 && object.getResolutionPercentage() == 100.0) {
				TrackingLog first = completedLogs.get(0);
				TrackingLog second = completedLogs.get(1);

				super.state(!first.isDraftMode(), "resolutionPercentage", "agent.trackingLog.form.error.previousDraft");

				boolean sameStatus = first.getStatus().equals(second.getStatus());
				super.state(sameStatus, "status", "agent.trackingLog.form.error.mismatchedStatus");
			}
		}
	}

	@Override
	public void perform(final TrackingLog object) {
		assert object != null;
		object.setDraftMode(false);
		this.repository.save(object);
		object.getClaim().getStatus();
	}

	@Override
	public void unbind(final TrackingLog object) {
		assert object != null;
		Dataset dataset;
		SelectChoices choicesStatus;

		choicesStatus = SelectChoices.from(TrackingLogStatus.class, object.getStatus());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode");
		dataset.put("status", choicesStatus);

		super.getResponse().addData(dataset);
	}
}
