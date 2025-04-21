
package acme.entities.service;

import javax.persistence.Column;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;

public class Service extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				picture;

	@Mandatory
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private double				avDwellTime;

	@Optional
	@ValidString(pattern = "^[A-Z]{4}-[0-9]{2}$")
	@Column(unique = true)
	private String				employeeCode;

	@Optional
	@Valid
	@Automapped
	private Money				money;

	// Relationships ----------------------------------------------------------
}
