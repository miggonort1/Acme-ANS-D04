
package acme.entities.claim;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.constraints.ValidLongText;
import acme.entities.flight.Leg;
import acme.realms.Agent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
	@Valid
	@Automapped
	private ClaimStatus			status;

	@Mandatory
	// HINT: @Valid by default.
	@Automapped
	private boolean				draftMode;

	@Optional
	// HINT: @Valid by default.
	@Automapped
	private boolean				dissatisfied;

	// Relationships ----------------------------------------------------------
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Agent				agent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg					leg;

}
