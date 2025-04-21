
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.CrewMember;
import acme.realms.CrewMemberRepository;

@Validator
public class CrewMemberCodeValidator extends AbstractValidator<ValidCrewMemberCode, CrewMember> {

	@Autowired
	private CrewMemberRepository repository;


	@Override
	protected void initialise(final ValidCrewMemberCode annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final CrewMember crewMember, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (crewMember.getUserAccount() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = this.getInitials(crewMember);
			String code = crewMember.getEmployeeCode();
			CrewMember sameCode = this.repository.findMemberSameCode(code);

			if (code == null)
				super.state(context, false, "employeeCode", "javax.validation.constraints.NotNull.message");
			else if (!code.startsWith(initials))
				super.state(context, false, "employeeCode", "validation.CrewMember.codePattern");
			else if (sameCode != null && !sameCode.equals(crewMember))
				super.state(context, false, "employeeCode", "validation.CrewMember.codeNotUnique");

		}
		result = !super.hasErrors(context);
		return result;
	}

	private String getInitials(final CrewMember crewMember) {

		String initials = "";
		String name = crewMember.getUserAccount().getIdentity().getName().trim();
		String surname = crewMember.getUserAccount().getIdentity().getSurname().trim();

		if (name != null && surname != null)
			initials = name.substring(0, 1) + surname.substring(0, 1);

		return initials;
	}

}
