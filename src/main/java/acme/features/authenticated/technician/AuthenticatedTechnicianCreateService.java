/*
 * AuthenticatedTechnicianCreateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.authenticated.technician;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.technician.Technician;

@GuiService
public class AuthenticatedTechnicianCreateService extends AbstractGuiService<Authenticated, Technician> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedTechnicianRepository repository;

	// AbstractService<Authenticated, Consumer> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Technician object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new Technician();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Technician object) {
		assert object != null;

		super.bindObject(object, "licenseNumber", "phoneNumber", "specialisation", "annualHealthTest", "yearsOfExperience", "certifications");

		if (object.getCertifications() == "")
			object.setCertifications(null);
	}

	@Override
	public void validate(final Technician object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("licenseNumber")) {
			Technician existing;

			existing = this.repository.findOneTechnicianByLicenseNumber(object.getLicenseNumber());
			super.state(existing == null, "licenseNumber", "technician.form.error.duplicated");
		}
	}

	@Override
	public void perform(final Technician object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Technician object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "licenseNumber", "phoneNumber", "specialisation", "annualHealthTest", "yearsOfExperience", "certifications");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
