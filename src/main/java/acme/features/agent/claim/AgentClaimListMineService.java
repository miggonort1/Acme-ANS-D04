
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.realms.Agent;

@GuiService
public class AgentClaimListMineService extends AbstractGuiService<Agent, Claim> {

	@Autowired
	private AgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Agent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Claim> objects;
		int agentId;

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		objects = this.repository.findManyClaimsByAgentId(agentId);

		super.getBuffer().addData(objects);
	}
	@Override
	public void unbind(final Claim object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "registrationMoment", "description", "type", "status");
		super.getResponse().addData(dataset);
	}

}
