
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select c from Technician c where c.userAccount.id = :id")
	Technician findTechnicianByUserAccountId(int id);

	@Query("select te from Technician te where te.id = :id")
	Technician findOneTechnicianById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findOneMaintenanceRecordById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findManyMaintenanceRecordByTechnicianId(int technicianId);

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
