
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
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
	public boolean isValid(final Manager airlineManager, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (airlineManager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {

			//Validar identidad del manager
			DefaultUserIdentity identity = airlineManager.getIdentity();

			if (identity != null && identity.getName() != null && identity.getSurname() != null) {
				String name = identity.getName().trim();
				String surname = identity.getSurname().trim();

				char nameInitial = name.charAt(0);
				char surnameInitial = surname.split(" ")[0].charAt(0);

				String expectedPrefix = "" + nameInitial + surnameInitial;
				String identifier = airlineManager.getIdentifierNumber();

				//Validar patron y prefijo del identifierNumber del manager
				boolean matchesPattern = identifier != null && identifier.matches("^[A-Z]{2,3}\\d{6}$");
				boolean startsWithPrefix = identifier != null && identifier.startsWith(expectedPrefix);

				boolean alreadyExists = this.repository.existsByIdentifierNumber(identifier);
				Manager existing = this.repository.findByIdentifierNumber(identifier);

				//Validar unicidad del identifierNumber del manager
				boolean isUnique = !alreadyExists || existing != null && existing.getId() == airlineManager.getId();

				//Union de validaciones
				boolean isValidIdentifier = matchesPattern && startsWithPrefix && isUnique;

				super.state(context, isValidIdentifier, "identifierNumber", "acme.validation.manager.flight.identifier-number.invalid.message");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}
