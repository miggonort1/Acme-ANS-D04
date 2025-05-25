
package acme.features.technician.dashboard;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.forms.TechnicianDashboard;
import acme.entities.maintenancerecord.Status;
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

		object = new TechnicianDashboard();
		object.setTotalMaintenanceCompleted(completed);
		object.setTotalMaintenanceInProgress(inProgress);
		object.setTotalMaintenancePending(pending);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final TechnicianDashboard object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "totalMaintenancePending", "totalMaintenanceInProgress", "totalMaintenanceCompleted");

		super.getResponse().addData(dataset);
	}

}
