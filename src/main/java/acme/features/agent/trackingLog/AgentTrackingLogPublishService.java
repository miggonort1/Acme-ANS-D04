
package acme.features.agent.trackingLog;

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
		int agentId;
		TrackingLog trackingLog;
		Agent agent;

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		agent = this.repository.findOneAgentById(agentId);
		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);
		status = trackingLog != null && (!trackingLog.isDraftMode() || super.getRequest().getPrincipal().hasRealm(trackingLog.getClaim().getAgent())) && trackingLog.getClaim().getAgent().equals(agent);

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

		if (object.getResolutionPercentage() != null && object.getResolutionPercentage() != null && object.getStatus() != null && object.getResolutionPercentage() < 100.0)
			super.state(object.getStatus().equals(TrackingLogStatus.PENDING), "status", "agent.trackingLog.form.error.badStatus");
		else if (object.getStatus() != null)
			super.state(!object.getStatus().equals(TrackingLogStatus.PENDING), "status", "agent.trackingLog.form.error.badStatus2");
		if (object.getStatus() != null && object.getStatus().equals(TrackingLogStatus.PENDING))
			super.state(object.getResolution() == null || object.getResolution().isBlank(), "resolution", "agent.trackingLog.form.error.badResolution");
		else
			super.state(object.getResolution() != null && !object.getResolution().isBlank(), "resolution", "agent.trackingLog.form.error.badResolution2");
		if (object.getClaim() != null) {
			TrackingLog recentTrackingLog;
			Optional<List<TrackingLog>> trackingLogs = this.repository.findOrderTrackingLog(object.getClaim().getId());
			if (object.getResolutionPercentage() != null && trackingLogs.isPresent() && trackingLogs.get().size() > 0) {
				recentTrackingLog = trackingLogs.get().get(0);
				long completedTrackingLogs = trackingLogs.get().stream().filter(t -> t.getResolutionPercentage() == 100).count();
				if (recentTrackingLog.getId() != object.getId())
					if (recentTrackingLog.getResolutionPercentage() == 100 && object.getResolutionPercentage() == 100)
						super.state(!recentTrackingLog.isDraftMode() && completedTrackingLogs < 2, "resolutionPercentage", "agent.trackingLog.form.error.maxcompleted");
					else
						super.state(recentTrackingLog.getResolutionPercentage() < object.getResolutionPercentage(), "resolutionPercentage", "agent.trackingLog.form.error.badPercentage");
			}
		}
	}

	@Override
	public void perform(final TrackingLog object) {
		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;
		SelectChoices choicesStatus;

		choicesStatus = SelectChoices.from(TrackingLogStatus.class, object.getStatus());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
		dataset.put("status", choicesStatus);

		super.getResponse().addData(dataset);
	}
}
