
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.Customer;
import acme.realms.CustomerRepository;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Autowired
	private CustomerRepository repository;


	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (customer == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Customer existingCustomer = this.repository.findCustomerByIdentifier(customer.getIdentifier());
			boolean uniqueCustomerIdentifier = existingCustomer == null || existingCustomer.equals(customer);
			super.state(context, uniqueCustomerIdentifier, "identifier", "acme.validation.customer.identifier.unique.message");

			String name = customer.getIdentity().getName().trim();
			String surname = customer.getIdentity().getSurname().trim();

			if (!StringHelper.isBlank(name) && !StringHelper.isBlank(surname)) {
				String initials = "" + name.charAt(0) + surname.charAt(0);
				String identifier = customer.getIdentifier().trim();

				boolean identifierCorrectSyntax = identifier.startsWith(initials);
				super.state(context, identifierCorrectSyntax, "identifier", "acme.validation.customer.identifier.syntax.message");
			} else
				super.state(context, false, "identifier", "acme.validation.customer.identifier.syntax.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
