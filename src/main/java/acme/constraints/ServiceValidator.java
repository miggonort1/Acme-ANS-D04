
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.service.Service;
import acme.entities.service.ServiceRepository;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ServiceRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (service == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean uniquePromotionCode;
			Service existingService;

			existingService = this.repository.findServiceByPromotionCode(service.getPromotionCode());
			uniquePromotionCode = existingService == null || existingService.equals(service);

			super.state(context, uniquePromotionCode, "promotionCode", "acme.validation.service.duplicated-promotionCode.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
