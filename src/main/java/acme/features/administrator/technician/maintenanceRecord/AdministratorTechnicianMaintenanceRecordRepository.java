
package acme.features.administrator.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@Repository
public interface AdministratorTechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select te from Technician te where te.id = :id")
	Technician findOneTechnicianById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findOneMaintenanceRecordById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findManyMaintenanceRecordByTechnicianId(int technicianId);

	@Query("select a from Aircraft a where a.status = :status")
	Collection<Aircraft> findManyAircraftsUnderMaintenance(AircraftStatus status);

	@Query("select a from Aircraft a")
	Collection<Aircraft> findManyAircrafts();

	@Query("select a from Aircraft a where a.id = :aircraftId")
	Aircraft findOneAircraftById(int aircraftId);

	@Query("select mrt from MaintenanceRecordTask mrt where mrt.maintenanceRecord.id = :maintenanceRecordId")
	Collection<MaintenanceRecordTask> findManyMaintenanceRecordTaskByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select m from MaintenanceRecord m where m.draftMode = false")
	Collection<MaintenanceRecord> findManyMaintenanceRecordByAvailability();

	@Query("select mrt.task from MaintenanceRecordTask mrt where mrt.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Task> findManyTaskByMaintenanceRecordId(int maintenanceRecordId);

}
