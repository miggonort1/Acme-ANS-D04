
package acme.features.agent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLog;
import acme.realms.Agent;

@GuiService
public class AgentTrackingLogListMineService extends AbstractGuiService<Agent, TrackingLog> {

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
		Collection<TrackingLog> trackingLogs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		trackingLogs = this.repository.findTrackingLogsByClaimId(masterId);

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog object) {
		assert object != null;
		Dataset dataset;

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> objects) {
		assert objects != null;

		int masterId;
		Claim claim;
		final boolean showCreate;
		long completedCount;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);

		completedCount = objects.stream().filter(t -> t.getResolutionPercentage() != null && t.getResolutionPercentage() == 100.0).count();

		showCreate = super.getRequest().getPrincipal().hasRealm(claim.getAgent()) && completedCount < 2;
		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}
}
