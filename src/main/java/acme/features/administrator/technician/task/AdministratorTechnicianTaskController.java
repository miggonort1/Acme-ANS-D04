
package acme.features.administrator.technician.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenancerecord.Task;

@GuiController
public class AdministratorTechnicianTaskController extends AbstractGuiController<Administrator, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorTechnicianTaskShowService						showService;

	@Autowired
	private AdministratorTechnicianTaskListInMaintenanceRecordService	listMaintenanceRecordService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("list", this.listMaintenanceRecordService);

	}

}
