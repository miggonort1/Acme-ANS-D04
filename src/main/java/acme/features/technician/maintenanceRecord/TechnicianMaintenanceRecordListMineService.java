
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.Status;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordListMineService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<MaintenanceRecord> objects;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		objects = this.repository.findManyMaintenanceRecordByTechnicianId(technicianId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final MaintenanceRecord object) {
		assert object != null;

		Dataset dataset;
		dataset = super.unbindObject(object, "notes", "inspectionDueDate");

		super.getResponse().addData(dataset);
	}

}
