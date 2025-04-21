
package acme.entities.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import acme.entities.booking.TravelClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	List<String>				lastFiveDestination;
	Money						moneySpentBookingsLastYear;
	Map<TravelClass, Integer>	topMonthsWithHighestNumberOfClaims;

	Double						countNumberOfCostBookingsLastFiveYears;
	Double						averageNumberOfCostBookingsLastFiveYears;
	Double						minimumNumberOfCostBookingsLastFiveYears;
	Double						maximumNumberOfCostBookingsLastFiveYears;
	Double						standardDeviationNumberOfCostBookingsLastFiveYears;

	Integer						countNumberOfPassengerBookings;
	Double						averageNumberOfPassengerBookings;
	Double						minimumNumberOfPassengerBookings;
	Double						maximumNumberOfPassengerBookings;
	Double						standardDeviationNumberOfPassengerBookings;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
