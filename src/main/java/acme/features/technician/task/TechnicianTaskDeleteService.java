
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskDeleteService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int taskId;
		Task task;

		taskId = super.getRequest().getData("id", int.class);
		task = this.repository.findOneTaskById(taskId);
		status = task != null && (!task.isDraftMode() || super.getRequest().getPrincipal().hasRealm(task.getTechnician()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task objects;
		int id;

		id = super.getRequest().getData("id", int.class);
		objects = this.repository.findOneTaskById(id);

		super.getBuffer().addData(objects);
	}

	@Override
	public void bind(final Task object) {
		assert object != null;

		super.bindObject(object, "type", "description", "priority", "estimatedDuration");

	}

	@Override
	public void validate(final Task object) {
		assert object != null;
	}

	@Override
	public void perform(final Task object) {
		assert object != null;

		Collection<MaintenanceRecordTask> maintenanceRecordTask;

		maintenanceRecordTask = this.repository.findManyMaintenanceRecordTaskByTaskId(object.getId());
		this.repository.deleteAll(maintenanceRecordTask);
		this.repository.delete(object);
	}

}
