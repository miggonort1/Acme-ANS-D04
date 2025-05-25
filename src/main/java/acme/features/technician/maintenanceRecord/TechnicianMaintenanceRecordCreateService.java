
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.Status;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = true;
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
		Technician technician;
		Date moment;
		technician = this.repository.findOneTechnicianById(super.getRequest().getPrincipal().getActiveRealm().getId());

		moment = MomentHelper.getCurrentMoment();
		object = new MaintenanceRecord();
		object.setAircraft(null);
		object.setDraftMode(true);
		object.setMoment(moment);
		object.setTechnician(technician);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final MaintenanceRecord object) {
		assert object != null;
		Aircraft aircraft;
		int aircraftId;
		super.bindObject(object, "status", "inspectionDueDate", "estimatedCost", "notes", "aircraft");

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

	}

	@Override
	public void perform(final MaintenanceRecord object) {
		assert object != null;

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

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
