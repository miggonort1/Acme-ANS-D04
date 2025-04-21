
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Date moment;
		Customer customer;
		Booking booking;

		moment = MomentHelper.getCurrentMoment();
		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setLocatorCode("");
		booking.setPurchaseMoment(moment);
		booking.setTravelClass(TravelClass.ECONOMY);
		booking.setLastNibble(null);
		booking.setCustomer(customer);
		booking.setDraftMode(true);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble", "flight");
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		SelectChoices travelClassesChoices;
		Collection<Flight> flights;
		SelectChoices flightsChoices;
		Dataset dataset;

		flights = this.repository.findAllFlightsInNoDraftMode();

		travelClassesChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		flightsChoices = SelectChoices.from(flights, "id", null);

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "lastNibble", "flight", "draftMode");
		dataset.put("travelClasses", travelClassesChoices);
		dataset.put("flights", flightsChoices);

		super.getResponse().addData(dataset);
	}

}
