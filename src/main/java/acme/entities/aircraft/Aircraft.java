
package acme.entities.aircraft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidShortText;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidShortText
	@Automapped
	private String				model;

	@Mandatory
	@ValidShortText
	@Column(unique = true)
	private String				registrationNumber;

	@Mandatory
	@ValidNumber(min = 0, max = 255, message = "255 pasajeros como máximo")
	@Automapped
	private int					capacity;

	@Mandatory
	@ValidNumber(min = 2000.0, max = 50000.0, message = "El peso debe estar entre los 2K Kg y los 50K Kg")
	@Automapped
	private Double				cargoWeight;

	@Mandatory
	@Valid
	@Automapped
	private AircraftStatus		status;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				details;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline; //muchos aviones pertenecen a una compañia

}
