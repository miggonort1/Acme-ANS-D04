
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Agent;

@Validator
public class AssistantAgentValidator extends AbstractValidator<ValidAssistantAgent, Agent> {

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

			boolean validIdentifier;

			String identifier = assistantAgent.getEmployeeCode();

			boolean validLength = identifier.length() >= 8 && identifier.length() <= 9;
			boolean validPattern = identifier.matches("^" + initials + "\\d{6}$");

			validIdentifier = validLength && validPattern;

			super.state(context, validIdentifier, "identifier", "java.validation.assistanceAgent.identifier.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
