
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

	@Query("SELECT COUNT(l) = 0 FROM Leg l WHERE l.flight.id = :flightId AND (l.scheduledDeparture <= :referenceMoment OR l.draftMode = true)")
	boolean allLegsArePublishedAndInFutureByFlightId(@Param("flightId") int flightId, @Param("referenceMoment") Date referenceMoment);

	@Query("SELECT f FROM Flight f WHERE f.draftMode = false AND NOT EXISTS (SELECT l FROM Leg l WHERE l.flight.id = f.id AND (l.scheduledDeparture <= :referenceMoment OR l.draftMode = true))")
	Collection<Flight> findAllFlightsInNoDraftModeAndWithFuturePublishedLegs(@Param("referenceMoment") Date referenceMoment);

	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(@Param("id") int id);

}
