
package acme.features.technician.maintenanceRecordTask;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordTaskDeleteService extends AbstractGuiService<Technician, MaintenanceRecordTask> {

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
	public void bind(final MaintenanceRecordTask object) {
		assert object != null;

		super.bindObject(object, "version");
	}

	@Override
	public void validate(final MaintenanceRecordTask object) {
		assert object != null;
	}

	@Override
	public void perform(final MaintenanceRecordTask object) {
		assert object != null;
		this.repository.delete(object);
	}

}
