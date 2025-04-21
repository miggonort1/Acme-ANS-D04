
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Agent;

@Validator
public class EmployeeCodeValidator extends AbstractValidator<ValidEmployeeCode, Agent> {

	@Override
	protected void initialise(final ValidEmployeeCode annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Agent agent, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (agent.getUserAccount() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = this.getInitials(agent);
			String code = agent.getEmployeeCode();

			if (code == null)
				super.state(context, false, "code", "javax.validation.constraints.NotNull.message");
			else if (!code.startsWith(initials))
				super.state(context, false, "code", "acme.validation.Agent.code");

		}
		result = !super.hasErrors(context);
		return result;
	}

	private String getInitials(final Agent agent) {

		String initials = "";
		String name = agent.getUserAccount().getIdentity().getName().trim();
		String surname = agent.getUserAccount().getIdentity().getSurname().trim();

		if (name != null && surname != null)
			initials = name.substring(0, 1) + surname.substring(0, 1);

		return initials;
	}
}
