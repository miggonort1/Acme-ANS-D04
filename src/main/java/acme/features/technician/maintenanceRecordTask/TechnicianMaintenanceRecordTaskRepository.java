
package acme.features.technician.maintenanceRecordTask;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordTask;
import acme.entities.maintenancerecord.Task;

@Repository
public interface TechnicianMaintenanceRecordTaskRepository extends AbstractRepository {

	@Query("select mrt from MaintenanceRecordTask mrt where mrt.id = :maintenanceRecordTaskId")
	MaintenanceRecordTask findOneMaintenanceRecordTaskById(int maintenanceRecordTaskId);

	@Query("select u from Task u where u.technician.id = :technicianId")
	Collection<Task> findManyTasksByTechnicianId(int technicianId);

	@Query("select t from Task t where t.id = :id")
	Task findOneTaskById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findOneMaintenanceRecordById(int id);

	@Query("select mrt from MaintenanceRecordTask mrt where mrt.maintenanceRecord.id = :maintenanceRecordId")
	Collection<MaintenanceRecordTask> findManyMaintenanceRecordTaskByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select mrt from MaintenanceRecordTask mrt where mrt.maintenanceRecord.id = :maintenanceRecordId and mrt.task.id= :taskId")
	MaintenanceRecordTask findOneMaintenanceRecordTaskByMaintenanceRecordAndTaskId(int maintenanceRecordId, int taskId);

	@Query("select mrt.task from MaintenanceRecordTask mrt where mrt.maintenanceRecord.id = :id")
	Collection<Task> findTasksFromMaintenanceRecordId(int id);
}
