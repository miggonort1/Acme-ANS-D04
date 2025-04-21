
package acme.features.technician.maintenanceRecordTask;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordTaskCreateService extends AbstractGuiService<Technician, MaintenanceRecordTask> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findOneMaintenanceRecordById(masterId);
		status = maintenanceRecord != null && (!maintenanceRecord.isDraftMode() || super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecordTask object;
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findOneMaintenanceRecordById(masterId);

		object = new MaintenanceRecordTask();
		object.setMaintenanceRecord(maintenanceRecord);
		object.setTask(null);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final MaintenanceRecordTask object) {
		assert object != null;
		int taskId;
		Task task;

		super.bindObject(object, "version");
		taskId = super.getRequest().getData("task", int.class);
		task = this.repository.findOneTaskById(taskId);
		object.setTask(task);
	}

	@Override
	public void validate(final MaintenanceRecordTask object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors()) {
			super.state(object.getMaintenanceRecord().getTechnician().equals(object.getMaintenanceRecord().getTechnician()), "maintenanceRecord", "technician.maintenance-record-task.form.error.not-same-maintenance-record");

			MaintenanceRecordTask mrt;
			mrt = this.repository.findOneMaintenanceRecordTaskByMaintenanceRecordAndTaskId(object.getMaintenanceRecord().getId(), object.getTask().getId());
			super.state(mrt == null, "*", "technician.maintenance-record-task.form.error.existing-m-r-t");
		}

	}

	@Override
	public void perform(final MaintenanceRecordTask object) {
		assert object != null;

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

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
