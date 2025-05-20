
package acme.features.administrator.bookingpassenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.booking.BookingPassenger;

@GuiController
public class AdministratorBookingPassengerController extends AbstractGuiController<Administrator, BookingPassenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingPassengerListService	listService;

	@Autowired
	private AdministratorBookingPassengerShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);

	}

}
