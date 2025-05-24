
package acme.entities.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	private List<String>			lastFiveDestinations;
	private Map<String, Double>		spentMoneyLastYear;
	private Map<String, Integer>	bookingsGroupedByTravelClass;
	private Map<String, Double>		bookingCountCost;
	private Map<String, Double>		bookingAverageCost;
	private Map<String, Double>		bookingMinimumCost;
	private Map<String, Double>		bookingMaximumCost;
	private Map<String, Double>		bookingDeviationCost;
	private Integer					bookingCountPassengers;
	private Double					bookingAveragePassengers;
	private Integer					bookingMinimumPassengers;
	private Integer					bookingMaximumPassengers;
	private Double					bookingDeviationPassengers;
}
