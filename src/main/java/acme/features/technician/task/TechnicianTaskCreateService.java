
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.Task;
import acme.entities.maintenancerecord.Type;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskCreateService extends AbstractGuiService<Technician, Task> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Task object;
		Technician technician;
		technician = this.repository.findOneTechnicianById(super.getRequest().getPrincipal().getActiveRealm().getId());

		object = new Task();
		object.setDraftMode(true);
		object.setTechnician(technician);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Task object) {
		assert object != null;
		super.bindObject(object, "type", "description", "priority", "estimatedDuration");

	}

	@Override
	public void validate(final Task object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("priority"))
			super.state(object.getPriority() >= 0 && object.getPriority() < 11, "priority", "technician.task.form.error.priority-range");
		if (!super.getBuffer().getErrors().hasErrors("estimatedDuration"))
			super.state(object.getEstimatedDuration() >= 0 && object.getEstimatedDuration() < 1001, "estimatedDuration", "technician.task.form.error.estimatedDuration-range");
	}

	@Override
	public void perform(final Task object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Task object) {
		Dataset dataset;
		SelectChoices choicesType;

		choicesType = SelectChoices.from(Type.class, object.getType());
		dataset = super.unbindObject(object, "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("type", choicesType);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
