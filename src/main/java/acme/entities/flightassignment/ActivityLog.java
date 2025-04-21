
package acme.entities.flightassignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.constraints.ValidActivityLog;
import acme.constraints.ValidLongText;
import acme.constraints.ValidShortText;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidActivityLog
public class ActivityLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				incidentType;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10, message = "{validation.activityLog.severityLevelOutOfRange}")
	@Automapped
	private int					severityLevel;

	@Mandatory
	//@Valid
	@Automapped
	private Boolean				draftMode			= true;

	// Relationships ----------------------------------------------------------
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private FlightAssignment	flightAssignment;

}
