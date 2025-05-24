
package acme.entities.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidPromotionCode;
import acme.constraints.ValidService;
import acme.entities.airport.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidService
@Table(name = "Service", uniqueConstraints = @UniqueConstraint(columnNames = "promotionCode"))

public class Service extends AbstractEntity {

	// Serialisation version ------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -----------------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				picture;

	@Optional
	@ValidPromotionCode
	@Column(unique = true)
	private String				promotionCode;

	@Optional
	@ValidMoney
	@Automapped
	private Money				discountedMoney;

	@Mandatory
	@ValidNumber(min = 0, max = 48)
	@Automapped
	private Double				averageDwellTime;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				airport;
}
