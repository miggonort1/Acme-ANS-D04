
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.Status;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

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
		if (super.getRequest().hasData("id")) {
			Integer AircraftId = super.getRequest().getData("aircraft", Integer.class);
			if (AircraftId == null || AircraftId != 0) {
				Aircraft aircraft = this.repository.findOneAircraftById(AircraftId);
				status = aircraft != null && aircraft.getStatus() == AircraftStatus.UNDER_MAINTENANCE;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord object;

		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneMaintenanceRecordById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final MaintenanceRecord object) {
		assert object != null;
		Aircraft aircraft;
		int aircraftId;
		Date moment;
		super.bindObject(object, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft");

		moment = MomentHelper.getCurrentMoment();
		object.setMoment(moment);
		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findOneAircraftById(aircraftId);
		object.setAircraft(aircraft);
		if (object.getNotes() == "")
			object.setNotes(null);
	}

	@Override
	public void validate(final MaintenanceRecord object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("inspectionDueDate"))
			super.state(MomentHelper.isAfter(object.getInspectionDueDate(), object.getMoment()), "inspectionDueDate", "technician.maintenance-record.form.error.bad-date");
		{
			Collection<Task> tasks;
			boolean allPublished;

			tasks = this.repository.findManyTaskByMaintenanceRecordId(object.getId());
			allPublished = tasks.stream().allMatch(task -> !task.isDraftMode());
			super.state(allPublished, "*", "technician.maintenance-record.form.error.task");
		}
		{
			Collection<Task> tasks;
			boolean existsTasks;

			tasks = this.repository.findManyTaskByMaintenanceRecordId(object.getId());
			existsTasks = tasks.size() > 0;
			super.state(existsTasks, "*", "technician.maintenance-record.form.error.existsTasks");
		}
		{
			super.state(object.getStatus() == Status.COMPLETED, "status", "technician.maintenance-record.form.error.statusCompleted");
		}

	}

	@Override
	public void perform(final MaintenanceRecord object) {
		assert object != null;

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final MaintenanceRecord object) {
		Dataset dataset;
		SelectChoices choicesStatus;
		SelectChoices choicesAircraft;

		Collection<Aircraft> aircrafts;
		AircraftStatus status = AircraftStatus.UNDER_MAINTENANCE;
		aircrafts = this.repository.findManyAircraftsUnderMaintenance(status);
		choicesAircraft = SelectChoices.from(aircrafts, "registrationNumber", object.getAircraft());

		choicesStatus = SelectChoices.from(Status.class, object.getStatus());
		dataset = super.unbindObject(object, "moment", "status", "inspectionDueDate", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choicesStatus);
		dataset.put("aircraft", choicesAircraft.getSelected().getKey());
		dataset.put("aircrafts", choicesAircraft);

		super.getResponse().addData(dataset);
	}

}
