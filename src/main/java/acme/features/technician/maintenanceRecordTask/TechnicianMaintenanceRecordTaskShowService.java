
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
public class TechnicianMaintenanceRecordTaskShowService extends AbstractGuiService<Technician, MaintenanceRecordTask> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordTaskId;
		MaintenanceRecordTask maintenanceRecordTask;

		maintenanceRecordTaskId = super.getRequest().getData("id", int.class);
		maintenanceRecordTask = this.repository.findOneMaintenanceRecordTaskById(maintenanceRecordTaskId);
		status = maintenanceRecordTask != null && (!maintenanceRecordTask.getMaintenanceRecord().isDraftMode() || super.getRequest().getPrincipal().hasRealm(maintenanceRecordTask.getMaintenanceRecord().getTechnician()));

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
	public void unbind(final MaintenanceRecordTask object) {
		Dataset dataset;
		SelectChoices choicesTask;
		Collection<Task> tasks;
		tasks = this.repository.findManyTasksByTechnicianId(super.getRequest().getPrincipal().getActiveRealm().getId());
		choicesTask = SelectChoices.from(tasks, "description", object.getTask());
		dataset = super.unbindObject(object, "version");

		dataset.put("tasks", choicesTask);
		dataset.put("draftMode", object.getMaintenanceRecord().isDraftMode());

		super.getResponse().addData(dataset);
	}

}
