
package acme.features.technician.dashboard;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.forms.TechnicianDashboard;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.Status;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianDashboardShowService extends AbstractGuiService<Technician, TechnicianDashboard> {

	@Autowired
	private TechnicianDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TechnicianDashboard object;
		int id;

		id = super.getRequest().getPrincipal().getActiveRealm().getId();
		Integer completed = this.repository.totalMaintenancePending(Status.COMPLETED, id);
		Integer inProgress = this.repository.totalMaintenancePending(Status.IN_PROGRESS, id);
		Integer pending = this.repository.totalMaintenancePending(Status.PENDING, id);
		String nearest = this.repository.findTopByInspectionDueDateByTechnician(id).getFirst().getNotes();
		List<Aircraft> l = this.repository.findTop5AircraftsByTaskCount(id);
		String models = l.stream().map(Aircraft::getModel).collect(Collectors.joining(", "));

		Collection<MaintenanceRecord> maintenanceRecords = this.repository.findAllMaintenanceRecordOf(id);

		List<MaintenanceRecord> recentmrs = maintenanceRecords.stream().filter(mr -> mr.getInspectionDueDate() != null && MomentHelper.isAfterOrEqual(mr.getInspectionDueDate(), MomentHelper.deltaFromCurrentMoment(-1, ChronoUnit.YEARS)))
			.collect(Collectors.toList());

		Map<String, List<Double>> amountsByCurrency = recentmrs.stream().collect(Collectors.groupingBy(mr -> mr.getEstimatedCost().getCurrency(), Collectors.mapping(mr -> mr.getEstimatedCost().getAmount(), Collectors.toList())));
		Map<String, Double> maintenanceRecordCountCost = new HashMap<>();
		Map<String, Double> maintenanceRecordAverageCost = new HashMap<>();
		Map<String, Double> maintenanceRecordMinimumCost = new HashMap<>();
		Map<String, Double> maintenanceRecordMaximumCost = new HashMap<>();
		Map<String, Double> maintenanceRecordDeviationCost = new HashMap<>();
		for (Map.Entry<String, List<Double>> entry : amountsByCurrency.entrySet()) {
			String currency = entry.getKey();
			List<Double> amounts = entry.getValue();

			double average = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
			double min = amounts.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
			double max = amounts.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
			double stddev = Math.sqrt(amounts.stream().mapToDouble(a -> Math.pow(a - average, 2)).average().orElse(0.0));

			maintenanceRecordAverageCost.put(currency, average);
			maintenanceRecordMinimumCost.put(currency, min);
			maintenanceRecordMaximumCost.put(currency, max);
			maintenanceRecordDeviationCost.put(currency, stddev);
		}
		maintenanceRecordAverageCost.putIfAbsent("EUR", 0.0);
		maintenanceRecordMinimumCost.putIfAbsent("EUR", 0.0);
		maintenanceRecordMaximumCost.putIfAbsent("EUR", 0.0);
		maintenanceRecordDeviationCost.putIfAbsent("EUR", 0.0);

		List<Task> tasks = this.repository.findTasksByTechnicianId(id);
		List<Double> durations = tasks.stream().map(Task::getEstimatedDuration).filter(d -> d != null).toList();

		double average = durations.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
		double min = durations.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
		double max = durations.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
		double stddev = Math.sqrt(durations.stream().mapToDouble(a -> Math.pow(a - average, 2)).average().orElse(0.0));

		object = new TechnicianDashboard();
		object.setTotalMaintenanceCompleted(completed);
		object.setTotalMaintenanceInProgress(inProgress);
		object.setTotalMaintenancePending(pending);
		object.setMaintentanceRecordWithNearestDueDate(nearest);
		object.setAircraftHigherNumberOfTask(models);
		object.setAverageNumberOfEstimatedCost(maintenanceRecordAverageCost);
		object.setMinimumNumberOfEstimatedCost(maintenanceRecordMinimumCost);
		object.setMaximumNumberOfEstimatedCost(maintenanceRecordMaximumCost);
		object.setDeviationNumberOfEstimatedCost(maintenanceRecordDeviationCost);
		object.setAverageNumberOfEstimatedDuration(average);
		object.setDeviationNumberOfEstimatedDuration(stddev);
		object.setMaximumNumberOfEstimatedDuration(max);
		object.setMinimumNumberOfEstimatedDuration(min);
		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final TechnicianDashboard object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "totalMaintenancePending", "totalMaintenanceInProgress", "totalMaintenanceCompleted", "maintentanceRecordWithNearestDueDate", "aircraftHigherNumberOfTask", "averageNumberOfEstimatedCost",
			"minimumNumberOfEstimatedCost", "maximumNumberOfEstimatedCost", "deviationNumberOfEstimatedCost", "averageNumberOfEstimatedDuration", "minimumNumberOfEstimatedDuration", "maximumNumberOfEstimatedDuration", "deviationNumberOfEstimatedDuration");

		super.getResponse().addData(dataset);
	}

}
