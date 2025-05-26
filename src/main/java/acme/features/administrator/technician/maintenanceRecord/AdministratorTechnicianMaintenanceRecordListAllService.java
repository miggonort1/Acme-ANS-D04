
package acme.features.administrator.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;

@GuiService
public class AdministratorTechnicianMaintenanceRecordListAllService extends AbstractGuiService<Administrator, MaintenanceRecord> {

	@Autowired
	private AdministratorTechnicianMaintenanceRecordRepository repository;


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
