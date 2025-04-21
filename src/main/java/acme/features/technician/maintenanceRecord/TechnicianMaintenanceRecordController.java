
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianMaintenanceRecordController extends AbstractGuiController<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordShowService		showService;

	@Autowired
	private TechnicianMaintenanceRecordListMineService	listMineService;

	@Autowired
	private TechnicianMaintenanceRecordCreateService	createService;

	@Autowired
	private TechnicianMaintenanceRecordDeleteService	deleteService;

	@Autowired
	private TechnicianMaintenanceRecordUpdateService	updateService;

	@Autowired
	private TechnicianMaintenanceRecordListAllService	listAllService;

	@Autowired
	private TechnicianMaintenanceRecordPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("list-mine", "list", this.listMineService);
		super.addCustomCommand("list-all", "list", this.listAllService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
