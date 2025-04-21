
package acme.features.agent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.claim.TrackinLogStatus;
import acme.entities.claim.TrackingLog;
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
		super.bindObject(object, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog object) {
		Claim claim = object.getClaim();
		boolean claimPublished = claim.getStatus() != ClaimStatus.PENDING;

		super.state(claimPublished, "*", "acme.validation.trackinglog.publish-claim-not-published");
	}

	@Override
	public void perform(final TrackingLog object) {
		object.setStatus(TrackinLogStatus.ACCEPTED);
		this.repository.save(object);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;
		SelectChoices choicesStatus;

		choicesStatus = SelectChoices.from(TrackinLogStatus.class, object.getStatus());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
		dataset.put("status", choicesStatus);

		super.getResponse().addData(dataset);
	}
}
