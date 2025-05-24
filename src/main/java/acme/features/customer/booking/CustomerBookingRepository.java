
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(final int bookingId);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(final int customerId);

	@Query("select count(bp) from BookingPassenger bp where bp.booking.id = :bookingId")
	Integer findNumberOfPassengersBookingPassengerBookingById(final int bookingId);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(final String locatorCode);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<BookingPassenger> findAllBookingPassengerByBookingId(final int bookingId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND (l.scheduledDeparture <= :referenceMoment OR l.draftMode = true)")
	Collection<Leg> findInvalidLegsForFlight(int flightId, Date referenceMoment);

	@Query("SELECT f FROM Flight f WHERE f.draftMode = false")
	Collection<Flight> findAllPublishedFlights();

	@Query("SELECT f FROM Flight f where f.id = :id")
	Flight findFlightById(@Param("id") int id);

}
