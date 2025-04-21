
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

	Double						averageNumberOfLogsTheirClaimsHave;
	Double						minimumNumberOfLogsTheirClaimsHave;
	Double						maximumNumberOfLogsTheirClaimsHave;
	Double						deviationNumberOfLogsTheirClaimsHave;

	Double						averageNumberOfClaimsTheyAssisted;
	Double						minimumNumberOfClaimsTheyAssisted;
	Double						maximumNumberOfClaimsTheyAssisted;
	Double						deviationNumberOfClaimsTheyAssisted;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
