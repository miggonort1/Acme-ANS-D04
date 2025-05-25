
package acme.features.administrator.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenancerecord.MaintenanceRecord;

@GuiController
public class AdministratorTechnicianMaintenanceRecordController extends AbstractGuiController<Administrator, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorTechnicianMaintenanceRecordShowService		showService;

	@Autowired
	private AdministratorTechnicianMaintenanceRecordListAllService	listAllService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("list", this.listAllService);
		super.addBasicCommand("show", this.showService);
	}

}
