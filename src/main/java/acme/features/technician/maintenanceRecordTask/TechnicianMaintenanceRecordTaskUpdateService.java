
package acme.features.technician.maintenanceRecordTask;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordTaskUpdateService extends AbstractGuiService<Technician, MaintenanceRecordTask> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository repository;


	// AbstractService<Manager, ProjectUserStoryLink> ---------------------------
	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordTaskId;
		MaintenanceRecordTask maintenanceRecordTask;

		maintenanceRecordTaskId = super.getRequest().getData("id", int.class);
		maintenanceRecordTask = this.repository.findOneMaintenanceRecordTaskById(maintenanceRecordTaskId);
		status = maintenanceRecordTask != null && maintenanceRecordTask.getMaintenanceRecord().isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecordTask.getMaintenanceRecord().getTechnician());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecordTask objects;
		int id;

		id = super.getRequest().getData("id", int.class);
		objects = this.repository.findOneMaintenanceRecordTaskById(id);

		super.getBuffer().addData(objects);
	}

	@Override
	public void bind(final MaintenanceRecordTask object) {

		super.bindObject(object, "maintanenceRecord", "task");
	}

	@Override
	public void validate(final MaintenanceRecordTask object) {
		assert object != null;
	}

	@Override
	public void perform(final MaintenanceRecordTask object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final MaintenanceRecordTask object) {
		Dataset dataset;
		SelectChoices choicesTask;
		Collection<Task> tasks;
		int masterId;
		Collection<Task> tasks_asociated;
		tasks = this.repository.findManyTasksByTechnicianId(super.getRequest().getPrincipal().getActiveRealm().getId());

		masterId = super.getRequest().getData("masterId", int.class);
		tasks_asociated = this.repository.findTasksFromMaintenanceRecordId(masterId);
		tasks.removeAll(tasks_asociated);
		choicesTask = SelectChoices.from(tasks, "description", object.getTask());
		dataset = super.unbindObject(object, "version");

		dataset.put("tasks", choicesTask);
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		super.getResponse().addData(dataset);
	}
}
