
package acme.features.technician.dashboard;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.Status;
import acme.entities.maintenancerecord.Task;

@Repository
public interface TechnicianDashboardRepository extends AbstractRepository {

	@Query("""
		select count(mr)
		from MaintenanceRecord mr
		where mr.status = :status
		  and mr.technician.id = :id
		  and mr.draftMode = false
		""")
	Integer totalMaintenancePending(Status status, int id);

	@Query("""
		select mr
		from MaintenanceRecord mr
		where mr.technician.id = :technicianId
		  and mr.draftMode = false
		order by mr.inspectionDueDate asc
		""")
	List<MaintenanceRecord> findTopByInspectionDueDateByTechnician(int technicianId);

	@Query("""
		select mrt.maintenanceRecord.aircraft
		from MaintenanceRecordTask mrt
		where mrt.maintenanceRecord.technician.id = :technicianId
		  and mrt.maintenanceRecord.draftMode = false
		group by mrt.maintenanceRecord.aircraft
		order by count(mrt.task) desc
		""")
	List<Aircraft> findTop5AircraftsByTaskCount(int technicianId);

	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.technician.id =:technicianId and mr.draftMode = false")
	Collection<MaintenanceRecord> findAllMaintenanceRecordOf(int technicianId);

	@Query("""
		select mrt.task
		from MaintenanceRecordTask mrt
		where mrt.maintenanceRecord.technician.id = :technicianId
		""")
	List<Task> findTasksByTechnicianId(int technicianId);
}
