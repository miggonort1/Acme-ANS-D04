
package acme.entities.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Integer						managerRanking;

	Integer						yearsUntilRetirement;

	Double						ratioOfOnTimeLegs;
	Double						ratioOfDelayedLegs;

	List<String>				mostPopularAirports;
	List<String>				leastPopularAirports;

	Map<String, Integer>		legsGroupedByStatus;

	Money						averageFlightCost;
	Money						minFlightCost;
	Money						maxFlightCost;
	Money						standardDeviationFlightCost;

}
