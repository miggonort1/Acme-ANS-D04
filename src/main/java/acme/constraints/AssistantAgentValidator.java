
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.features.authenticated.agent.AuthenticatedAgentRepository;
import acme.realms.Agent;

@Validator
public class AssistantAgentValidator extends AbstractValidator<ValidAssistantAgent, Agent> {

	@Autowired
	private AuthenticatedAgentRepository repository;


	@Override
	protected void initialise(final ValidAssistantAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Agent assistantAgent, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (assistantAgent == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String fullName = assistantAgent.getUserAccount().getIdentity().getFullName();
			String[] nameParts = fullName.split(", ");
			String initials = "";

			String[] surnameParts = nameParts[0].split(" ");
			initials = nameParts[1].substring(0, 1).toUpperCase();
			initials += surnameParts[0].substring(0, 1).toUpperCase();

			if (surnameParts.length > 1)
				initials += surnameParts[1].substring(0, 1).toUpperCase();

			boolean validEmployeeCode;

			String identifier = assistantAgent.getEmployeeCode();

			boolean validLength = identifier.length() >= 8 && identifier.length() <= 9;
			boolean validPattern = identifier.matches("^" + initials + "\\d{6}$");

			validEmployeeCode = validLength && validPattern;

			super.state(context, validEmployeeCode, "employeeCode", "validation.agent.identifier.message");

			if (validEmployeeCode) {
				Agent existing = this.repository.findAssistanceAgentByUserAccountId(assistantAgent.getUserAccount().getId());
				boolean unique = existing == null || existing.equals(assistantAgent);
				super.state(context, unique, "employeeCode", "validation.agent.duplicated-identifier.message");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}

}
