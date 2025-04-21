
package acme.features.agent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.TrackinLogStatus;
import acme.entities.claim.TrackingLog;
import acme.realms.Agent;

@GuiService
public class AgentTrackingLogShowService extends AbstractGuiService<Agent, TrackingLog> {

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
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;
		SelectChoices choicesStatus;

		choicesStatus = SelectChoices.from(TrackinLogStatus.class, object.getStatus());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");

		dataset.put("status", choicesStatus);
		dataset.put("masterId", object.getClaim().getId());
		dataset.put("draftMode", object.getClaim().isDraftMode());

		super.getResponse().addData(dataset);
	}
}
