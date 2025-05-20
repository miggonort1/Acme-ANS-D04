
package acme.features.agent.trackingLog;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackinLogStatus;
import acme.entities.claim.TrackingLog;
import acme.realms.Agent;

@GuiService
public class AgentTrackingLogCreateService extends AbstractGuiService<Agent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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
		trackingLog.setResolutionPercentage(0.0);
		trackingLog.setStatus(TrackinLogStatus.PENDING);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog object) {
		super.bindObject(object, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog object) {
		assert object != null;

		if (object.getResolutionPercentage() != null && object.getResolutionPercentage() != null && object.getStatus() != null && object.getResolutionPercentage() < 100.0)
			super.state(object.getStatus().equals(TrackinLogStatus.PENDING), "status", "agent.trackingLog.form.error.badStatus");
		else if (object.getStatus() != null)
			super.state(!object.getStatus().equals(TrackinLogStatus.PENDING), "status", "agent.trackingLog.form.error.badStatus2");
		if (object.getStatus() != null && object.getStatus().equals(TrackinLogStatus.PENDING))
			super.state(object.getResolution() == null || object.getResolution().isBlank(), "resolution", "agent.trackingLog.form.error.badResolution");
		else
			super.state(object.getResolution() != null && !object.getResolution().isBlank(), "resolution", "agent.trackingLog.form.error.badResolution2");
	}

	@Override
	public void perform(final TrackingLog object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;
		SelectChoices choicesStatus;

		choicesStatus = SelectChoices.from(TrackinLogStatus.class, object.getStatus());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
		dataset.put("status", choicesStatus);

		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("draftMode", object.getClaim().isDraftMode());

		super.getResponse().addData(dataset);
	}
}
