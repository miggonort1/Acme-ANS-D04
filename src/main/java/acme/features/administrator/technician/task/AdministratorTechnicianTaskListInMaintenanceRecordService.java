
package acme.features.administrator.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.Task;

@GuiService
public class AdministratorTechnicianTaskListInMaintenanceRecordService extends AbstractGuiService<Administrator, Task> {

	@Autowired
	private AdministratorTechnicianTaskRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Task> objects;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		objects = this.repository.findManyTaskTaskByMaintenanceRecordId(masterId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Task object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "description");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Task> objects) {
		assert objects != null;

		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		super.getResponse().addGlobal("masterId", masterId);
	}
}
