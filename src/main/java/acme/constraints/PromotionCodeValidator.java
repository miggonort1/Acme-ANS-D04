
package acme.constraints;

import java.time.Year;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class PromotionCodeValidator extends AbstractValidator<ValidPromotionCode, String> {

	private static final String PATTERN = "^[A-Z]{4}-[0-9]{2}$";


	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || value.isBlank())
			return true; // No validar si es opcional y está vacío

		if (!value.matches(PromotionCodeValidator.PATTERN))
			return false; // Validar formato

		// Verificar si los dos últimos dígitos corresponden con el año actual
		String yearPart = value.substring(value.length() - 2);
		int currentYear = Year.now().getValue() % 100;

		return yearPart.equals(String.format("%02d", currentYear));
	}
}
