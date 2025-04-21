
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordListAllService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<MaintenanceRecord> objects;

		objects = this.repository.findManyMaintenanceRecordByAvailability();

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final MaintenanceRecord object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "inspectionDueDate", "notes");

		super.getResponse().addData(dataset);
	}

}
