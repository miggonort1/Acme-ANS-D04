
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordDeleteService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;

		maintenanceRecordId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findOneMaintenanceRecordById(maintenanceRecordId);
		status = maintenanceRecord != null && (!maintenanceRecord.isDraftMode() || super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord objects;
		int id;

		id = super.getRequest().getData("id", int.class);
		objects = this.repository.findOneMaintenanceRecordById(id);

		super.getBuffer().addData(objects);
	}

	@Override
	public void bind(final MaintenanceRecord object) {
		assert object != null;

		super.bindObject(object, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft");

	}

	@Override
	public void validate(final MaintenanceRecord object) {
		assert object != null;
	}

	@Override
	public void perform(final MaintenanceRecord object) {
		assert object != null;

		Collection<MaintenanceRecordTask> maintenanceRecordTask;

		maintenanceRecordTask = this.repository.findManyMaintenanceRecordTaskByMaintenanceRecordId(object.getId());
		this.repository.deleteAll(maintenanceRecordTask);
		this.repository.delete(object);
	}

	@Override
	public void unbind(final MaintenanceRecord object) {
	}

}
