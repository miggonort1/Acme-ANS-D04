
package acme.features.authenticated.crewmember;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineRepository;
import acme.realms.AvailabilityStatus;
import acme.realms.CrewMember;

@GuiService
public class AuthenticatedCrewMemberCreateService extends AbstractGuiService<Authenticated, CrewMember> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCrewMemberRepository	repository;

	@Autowired
	private AirlineRepository					airlineRepository;

	// AbstractService<Authenticated, CrewMember> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(CrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		CrewMember object;
		int userAccountId;
		UserAccount userAccount;
		Money defaultSalary = new Money();
		defaultSalary.setAmount(1500.0);
		defaultSalary.setCurrency("EUR");

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new CrewMember();
		object.setUserAccount(userAccount);
		object.setSalary(defaultSalary);
		object.setEmployeeCode(CodeGenerator.generateValidCode(object));

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final CrewMember object) {
		assert object != null;

		super.bindObject(object, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "yearsOfExperience", "salary", "airline");
	}

	@Override
	public void validate(final CrewMember object) {
		assert object != null;
	}

	@Override
	public void perform(final CrewMember object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final CrewMember object) {
		Dataset dataset;
		Collection<Airline> airlines;
		airlines = this.airlineRepository.findAllAirlines();

		SelectChoices choicesAvailabilityStatus = SelectChoices.from(AvailabilityStatus.class, object.getAvailabilityStatus());
		SelectChoices airlineChoices = SelectChoices.from(airlines, "name", object.getAirline());

		dataset = super.unbindObject(object, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "yearsOfExperience", "salary", "airline");
		dataset.put("airlineChoices", airlineChoices);
		dataset.put("availabilityStatusChoices", choicesAvailabilityStatus);
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
