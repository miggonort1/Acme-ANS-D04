
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskListInMaintenanceRecordService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;

		maintenanceRecordId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findOneMaintenanceRecordById(maintenanceRecordId);
		status = maintenanceRecord != null && (!maintenanceRecord.isDraftMode() || super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Task> objects;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		objects = this.repository.findManyTaskTaskByMaintenanceRecordId(masterId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Task object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "description");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Task> objects) {
		assert objects != null;

		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		super.getResponse().addGlobal("masterId", masterId);
	}
}
