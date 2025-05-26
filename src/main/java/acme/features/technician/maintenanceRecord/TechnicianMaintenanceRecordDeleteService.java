
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.entities.maintenancerecord.Status;
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
		status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());

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
		Dataset dataset;
		SelectChoices choicesStatus;
		SelectChoices choicesAircraft;

		Collection<Aircraft> aircrafts;
		if (object.isDraftMode()) {

			AircraftStatus status = AircraftStatus.UNDER_MAINTENANCE;
			aircrafts = this.repository.findManyAircraftsUnderMaintenance(status);

		} else
			aircrafts = this.repository.findManyAircrafts();

		choicesAircraft = SelectChoices.from(aircrafts, "registrationNumber", object.getAircraft());

		choicesStatus = SelectChoices.from(Status.class, object.getStatus());
		dataset = super.unbindObject(object, "moment", "status", "inspectionDueDate", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choicesStatus);
		dataset.put("aircraft", choicesAircraft.getSelected().getKey());
		dataset.put("aircrafts", choicesAircraft);

		super.getResponse().addData(dataset);
	}
}
