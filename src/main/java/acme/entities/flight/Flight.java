
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidLongText;
import acme.constraints.ValidShortText;
import acme.realms.manager.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	// Serialisation identifier
	private static final long	serialVersionUID	= 1L;

	// Attributes
	@Mandatory
	@ValidShortText
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney
	@Automapped
	private Money				cost;

	@Optional
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	// HINT: @Valid by default.
	@Automapped
	private boolean				draftMode;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Date getScheduledDeparture() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		Date departure = repository.findScheduledDeparture(this.getId());

		return departure;
	}

	@Transient
	public Date getScheduledArrival() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		Date arrival = repository.findScheduledArrival(this.getId());

		return arrival;
	}

	@Transient
	public String getOriginCity() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		return repository.findOriginCity(this.getId());
	}

	@Transient
	public String getDestinationCity() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		return repository.findDestinationCity(this.getId());
	}

	@Transient
	public Integer getLayovers() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		Integer legs = repository.countLegs(this.getId());
		return legs != null && legs > 0 ? legs - 1 : 0;
	}


	// Relationships
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager manager;

}
