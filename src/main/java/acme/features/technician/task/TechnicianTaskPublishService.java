
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.Task;
import acme.entities.maintenancerecord.Type;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskPublishService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int taskId;
		Task task;

		taskId = super.getRequest().getData("id", int.class);
		task = this.repository.findOneTaskById(taskId);
		status = task != null && task.isDraftMode() && super.getRequest().getPrincipal().hasRealm(task.getTechnician());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task object;

		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTaskById(id);
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

		object.setDraftMode(false);
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

}
