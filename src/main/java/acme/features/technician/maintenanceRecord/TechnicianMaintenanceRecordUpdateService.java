
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
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = true;

		// Obtener ID del registro de mantenimiento
		int maintenanceRecordId = super.getRequest().getData("id", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findOneMaintenanceRecordById(maintenanceRecordId);

		// Validar existencia, modo borrador y que el técnico coincida con el usuario actual
		if (maintenanceRecord == null || !maintenanceRecord.isDraftMode() || !super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()))
			status = false;

		// Validar estado del avión si se proporciona su ID
		if (status && super.getRequest().hasData("aircraft")) {
			Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
			if (aircraftId != null && aircraftId != 0) {
				Aircraft aircraft = this.repository.findOneAircraftById(aircraftId);
				if (aircraft == null || aircraft.getStatus() != AircraftStatus.UNDER_MAINTENANCE)
					status = false;
			}
		}

		// Validar que la fecha 'moment' no haya cambiado
		if (status && super.getRequest().hasData("moment")) {
			Date requestMoment = super.getRequest().getData("moment", Date.class);
			if (requestMoment != null && maintenanceRecord.getMoment() != null) {
				boolean unchanged = maintenanceRecord.getMoment().getTime() == requestMoment.getTime();
				status = status && unchanged;
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

}
