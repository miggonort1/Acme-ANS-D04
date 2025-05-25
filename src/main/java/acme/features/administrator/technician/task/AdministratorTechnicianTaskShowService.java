
package acme.features.administrator.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.Task;
import acme.entities.maintenancerecord.Type;

@GuiService
public class AdministratorTechnicianTaskShowService extends AbstractGuiService<Administrator, Task> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorTechnicianTaskRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Task object;

		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTaskById(id);
		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final Task object) {
		Dataset dataset;
		SelectChoices choicesType;

		choicesType = SelectChoices.from(Type.class, object.getType());
		dataset = super.unbindObject(object, "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("type", choicesType);

		super.getResponse().addData(dataset);
	}
}
