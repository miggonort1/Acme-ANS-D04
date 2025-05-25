
package acme.entities.claim;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidClaim;
import acme.constraints.ValidLongText;
import acme.entities.flight.Leg;
import acme.features.agent.trackingLog.AgentTrackingLogRepository;
import acme.realms.Agent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidClaim
public class Claim extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private Type				type;

	@Mandatory
	// HINT: @Valid by default.
	@Automapped
	private boolean				draftMode;

	// Derived attributes -----------------------------------------------------


	@Transient
	public TrackingLogStatus getStatus() {
		AgentTrackingLogRepository repository = SpringHelper.getBean(AgentTrackingLogRepository.class);
		Collection<TrackingLog> trackingLogs = repository.findTrackingLogsPublishedByClaimId(this.getId());

		if (trackingLogs == null || trackingLogs.isEmpty())
			return TrackingLogStatus.PENDING;

		return trackingLogs.stream().sorted((a, b) -> b.getCreationMoment().compareTo(a.getCreationMoment())).map(TrackingLog::getStatus).findFirst().orElse(TrackingLogStatus.PENDING);
	}


	// Relationships ----------------------------------------------------------
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Agent	agent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg		leg;

}
