
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidEmployeeCode;
import acme.constraints.ValidLongText;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidEmployeeCode
public class Agent extends AbstractRole {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", message = "{validation.agent.employeeCode}")
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				spokenLanguages;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Optional
	@ValidLongText
	@Automapped
	private String				briefBio;

	@Optional
	@Valid
	@Automapped
	private Money				salary;

	@Optional
	@ValidUrl
	@Automapped
	private String				photo;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;
}
