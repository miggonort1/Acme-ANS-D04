
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.realms.manager.Manager;
import acme.realms.manager.ManagerRepository;

public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	// Internal state ---------------------------------------------------------------------

	@Autowired
	private ManagerRepository repository;

	// ConstraintValidator interface ------------------------------------------------------


	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {

		if (context == null)
			return false;

		boolean validIdentifier = false;

		if (manager.getUserAccount() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = this.getInitials(manager);
			String identifier = manager.getIdentifierNumber();

			if (identifier != null)
				validIdentifier = identifier.startsWith(initials);
		}
		return validIdentifier;
	}

	private String getInitials(final Manager manager) {

		String initials = "";
		String name = manager.getUserAccount().getIdentity().getName().trim();
		String surname = manager.getUserAccount().getIdentity().getSurname().trim();

		if (name != null && surname != null)
			initials = name.substring(0, 1) + surname.substring(0, 1);

		return initials;
	}
}
