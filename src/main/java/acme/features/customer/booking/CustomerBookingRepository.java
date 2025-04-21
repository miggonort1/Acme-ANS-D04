
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.flight.Flight;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(final int bookingId);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(final int customerId);

	@Query("select f from Flight f where f.draftMode = false")
	Collection<Flight> findAllFlightsInNoDraftMode();

	@Query("select count(bp) from BookingPassenger bp where bp.booking.id = :bookingId")
	Integer findNumberOfPassengersBookingPassengerBookingById(final int bookingId);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(final String locatorCode);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<BookingPassenger> findAllBookingPassengerByBookingId(final int bookingId);

}
