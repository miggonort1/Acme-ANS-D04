
package acme.features.technician.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenancerecord.Status;

@Repository
public interface TechnicianDashboardRepository extends AbstractRepository {

	@Query("select count(mr) from MaintenanceRecord mr where mr.status = :status and mr.technician.id = :id")
	Integer totalMaintenancePending(Status status, int id);

	//	@Query("select avg(t.periodEnd - t.periodSttt) from Task t where t.codeAudit.auditor.id= :id and t.codeAudit.draftMode = false")
	//	Double averageTimeOfPeriod(int id);
	//
	//	@Query("select stddev(t.periodEnd - t.periodSttt ) from Task t where t.codeAudit.auditor.id= :id and t.codeAudit.draftMode = false")
	//	Double deviationTimeOfPeriod(int id);
	//
	//	@Query("select min(t.periodEnd - t.periodSttt ) from Task t where t.codeAudit.auditor.id= :id and t.codeAudit.draftMode = false")
	//	Double minimumTimeOfPeriod(int id);
	//
	//	@Query("select max(t.periodEnd - t.periodSttt) from Task t where t.codeAudit.auditor.id= :id and t.codeAudit.draftMode = false")
	//	Double maximumTimeOfPeriod(int id);
}
