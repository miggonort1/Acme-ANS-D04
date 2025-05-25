
package acme.features.technician.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenancerecord.Task;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianTaskController extends AbstractGuiController<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskShowService						showService;

	@Autowired
	private TechnicianTaskListAllService					listAllService;

	@Autowired
	private TechnicianTaskCreateService						createService;

	@Autowired
	private TechnicianTaskDeleteService						deleteService;

	@Autowired
	private TechnicianTaskUpdateService						updateService;

	@Autowired
	private TechnicianTaskListInMaintenanceRecordService	listMaintenanceRecordService;

	@Autowired
	private TechnicianTaskPublishService					publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("list-all", "list", this.listAllService);
		super.addCustomCommand("list-maintenance-record", "list", this.listMaintenanceRecordService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
