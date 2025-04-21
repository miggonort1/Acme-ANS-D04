
package acme.constraints;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.technician.Technician;
import acme.realms.technician.TechnicianRepository;

@Validator
public class TechnicianValidator extends AbstractValidator<ValidTechnician, Technician> {

	@Autowired
	TechnicianRepository repository;


	@Override
	public void initialise(final ValidTechnician constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Technician technician, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		boolean isNull;

		isNull = technician == null || technician.getLicenseNumber() == null || technician.getUserAccount() == null;

		if (!isNull) {
			{
				boolean uniqueTechnician;
				Technician existingTechnician;

				existingTechnician = this.repository.findTechnicianByLicenseNumber(technician.getLicenseNumber());
				uniqueTechnician = existingTechnician == null || existingTechnician.equals(technician);

				super.state(context, uniqueTechnician, "licenseNumber", "acme.validation.technician.license-number-duplicated.message");
			}
			{
				boolean licenseNumberValid;

				String licenseNumber = technician.getLicenseNumber();

				licenseNumberValid = licenseNumber != null && Pattern.matches("^[A-Z]{2,3}\\d{6}$", licenseNumber);

				super.state(context, licenseNumberValid, "licenseNumber", "{acme.validation.technician.license-number.message}");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}

}
