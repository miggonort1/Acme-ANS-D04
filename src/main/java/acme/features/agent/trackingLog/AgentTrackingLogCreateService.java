
package acme.features.agent.trackingLog;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLog;
import acme.entities.claim.TrackingLogStatus;
import acme.realms.Agent;

@GuiService
public class AgentTrackingLogCreateService extends AbstractGuiService<Agent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(claimId);
		status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAgent());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId;
		masterId = super.getRequest().getData("masterId", int.class);
		Claim claim = this.repository.findClaimById(masterId);
		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		TrackingLog trackingLog = new TrackingLog();
		trackingLog.setClaim(claim);
		trackingLog.setLastUpdateMoment(currentMoment);
		trackingLog.setCreationMoment(currentMoment);
		trackingLog.setResolutionPercentage(0.0);
		trackingLog.setStatus(TrackingLogStatus.PENDING);
		trackingLog.setDraftMode(true);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog object) {
		super.bindObject(object, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog object) {
		assert object != null;

		if (object.getClaim() != null) {
			Collection<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(object.getClaim().getId());

			if (!trackingLogs.isEmpty()) {
				List<TrackingLog> completedLogs = trackingLogs.stream().filter(t -> t.getResolutionPercentage() != null && t.getResolutionPercentage() == 100.0).sorted(Comparator.comparing(TrackingLog::getCreationMoment)).toList();

				super.state(completedLogs.size() <= 2, "*", "agent.trackingLog.form.error.maxcompletedGlobal");

				if (completedLogs.size() == 1 && object.getResolutionPercentage() == 100.00) {
					TrackingLog first = completedLogs.get(0);
					TrackingLog second = object;

					super.state(!first.isDraftMode(), "resolutionPercentage", "agent.trackingLog.form.error.firstMustBePublished");

					super.state(first.getStatus().equals(second.getStatus()), "status", "agent.trackingLog.form.error.mismatchedStatus");
				}

				Optional<TrackingLog> recentTrackingLogOpt = trackingLogs.stream().filter(t -> t.getId() != object.getId()).sorted(Comparator.comparing(TrackingLog::getCreationMoment).reversed()).findFirst();

				if (object.getResolutionPercentage() != null && recentTrackingLogOpt.isPresent()) {
					TrackingLog recentTrackingLog = recentTrackingLogOpt.get();

					if (recentTrackingLog.getResolutionPercentage() == 100.00 && object.getResolutionPercentage() == 100.00) {
						super.state(!recentTrackingLog.isDraftMode(), "resolutionPercentage", "agent.trackingLog.form.error.previousDraft");

						super.state(completedLogs.size() <= 2, "resolutionPercentage", "agent.trackingLog.form.error.maxcompleted");
					} else
						super.state(recentTrackingLog.getResolutionPercentage() < object.getResolutionPercentage(), "resolutionPercentage", "agent.trackingLog.form.error.badPercentage");
				}
			}
		}
	}

	@Override
	public void perform(final TrackingLog object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;
		SelectChoices choicesStatus;

		choicesStatus = SelectChoices.from(TrackingLogStatus.class, object.getStatus());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode");
		dataset.put("status", choicesStatus);

		dataset.put("masterId", super.getRequest().getData("masterId", int.class));

		super.getResponse().addData(dataset);
	}
}
