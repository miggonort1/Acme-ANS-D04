
package acme.features.customer.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.forms.CustomerDashboard;
import acme.realms.Customer;

@GuiController
public class CustomerDashboardController extends AbstractGuiController<Customer, CustomerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerDashboardShowService customerDashboardService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.customerDashboardService);
	}

}
