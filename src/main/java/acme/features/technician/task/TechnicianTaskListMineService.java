
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.Task;
import acme.entities.maintenancerecord.Type;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskListMineService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Task> objects;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		objects = this.repository.findManyTaskByTechnicianId(technicianId);

		super.getBuffer().addData(objects);
	}
	@Override
	public void unbind(final Task object) {
		Dataset dataset;
		SelectChoices choicesType;

		choicesType = SelectChoices.from(Type.class, object.getType());
		dataset = super.unbindObject(object, "description");
		dataset.put("type", choicesType);

		super.getResponse().addData(dataset);
	}

}
