
package acme.entities.maintenancerecord;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.constraints.ValidLongText;
import acme.realms.technician.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Task extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@Automapped
	private Type				type;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Double				priority;

	@Mandatory
	@ValidNumber(min = 0, max = 1000)
	@Automapped
	private Double				estimatedDuration;

	@Mandatory
	// HINT: @Valid by default.
	@Automapped
	private boolean				draftMode;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Technician			technician;

}
