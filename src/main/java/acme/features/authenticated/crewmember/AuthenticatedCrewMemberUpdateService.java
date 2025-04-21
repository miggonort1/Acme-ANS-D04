
package acme.features.authenticated.crewmember;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineRepository;
import acme.realms.AvailabilityStatus;
import acme.realms.CrewMember;

@GuiService
public class AuthenticatedCrewMemberUpdateService extends AbstractGuiService<Authenticated, CrewMember> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCrewMemberRepository	repository;

	@Autowired
	private AirlineRepository					airlineRepository;

	// AbstractService interface ----------------------------------------------ç


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(CrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		CrewMember object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findCrewMemberByUserAccountId(userAccountId);

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
		assert object != null;

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
