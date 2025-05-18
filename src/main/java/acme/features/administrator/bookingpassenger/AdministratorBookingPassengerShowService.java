
package acme.features.administrator.bookingpassenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.entities.booking.Passenger;

@GuiService
public class AdministratorBookingPassengerShowService extends AbstractGuiService<Administrator, BookingPassenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;

		BookingPassenger bookingPassenger;

		id = super.getRequest().getData("id", int.class);

		bookingPassenger = this.repository.findBookingPassengerById(id);

		status = bookingPassenger != null && !bookingPassenger.getPassenger().isDraftMode() && !bookingPassenger.getBooking().isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		BookingPassenger bookingPassenger;

		int id;

		id = super.getRequest().getData("id", int.class);
		bookingPassenger = this.repository.findBookingPassengerById(id);

		super.getBuffer().addData(bookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {
		Collection<Passenger> passengers;
		SelectChoices passengerChoices;
		Passenger selectedPassenger;
		Dataset dataset;
		int customerId;

		// Obtener el ID del Customer activo
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		// Obtener los pasajeros publicados de este customer
		passengers = this.repository.findAllPublishedPassengersFromCustomerId(customerId);
		selectedPassenger = bookingPassenger.getPassenger();

		// Crear las opciones de selección con protección si el passenger no está en la lista
		passengerChoices = SelectChoices.from(passengers, "passportNumber", !passengers.contains(selectedPassenger) ? null : selectedPassenger);

		// Descomponer el objeto en sus campos
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger", "passenger.fullName", "passenger.email", "passenger.passportNumber", "passenger.dateOfBirth", "passenger.specialNeeds");

		// Añadir datos extra al dataset
		dataset.put("passenger", selectedPassenger != null ? selectedPassenger.getId() : null);
		dataset.put("passengers", passengerChoices);
		dataset.put("draftMode", bookingPassenger.getBooking().isDraftMode());

		super.getResponse().addData(dataset);
	}

}
