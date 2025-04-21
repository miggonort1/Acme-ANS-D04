
package acme.realms.technician;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidTechnician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTechnician
public class Technician extends AbstractRole {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Column(unique = true)
	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2,3}\\d{6}$")
	private String				licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				specialisation;

	@Mandatory
	// HINT: @Valid by default
	@Automapped
	private boolean				annualHealthTest;

	@Mandatory
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private Double				yearsOfExperience;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				certifications;

	// Relationships ----------------------------------------------------------

}
