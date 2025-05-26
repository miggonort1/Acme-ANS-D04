
package acme.entities.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Double						ratioOfClaimsResolved;
	Double						ratioOfClaimsRejected;

	Map<String, Integer>		topMonthsWithHighestNumberOfClaims;

	double						averageNumberOfLogsTheirClaimsHave;
	int							minimumNumberOfLogsTheirClaimsHave;
	int							maximumNumberOfLogsTheirClaimsHave;
	double						deviationNumberOfLogsTheirClaimsHave;

	double						averageNumberOfClaimsTheyAssisted;
	int							minimumNumberOfClaimsTheyAssisted;
	int							maximumNumberOfClaimsTheyAssisted;
	double						deviationNumberOfClaimsTheyAssisted;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
