
package acme.features.customer.bookingPassenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.booking.Passenger;

@Repository
public interface CustomerBookingPassengerRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(final int bookingId);

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(final int passengerId);

	@Query("select bp from BookingPassenger bp where bp.id = :BookingPassengerId")
	BookingPassenger findBookingPassengerById(final int BookingPassengerId);

	@Query("select bp from BookingPassenger bp where bp.booking.customer.id = :customerId")
	Collection<BookingPassenger> findBookingPassengersByCustomerId(final int customerId);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<BookingPassenger> findBookingPassengersByBookingId(final int bookingId);

	@Query("select b from Booking b where b.draftMode = true and b.customer.id = :customerId")
	Collection<Booking> findAllNotPublishedBookingsFromCustomerId(final int customerId);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findAllBookingsFromCustomerId(final int customerId);

	@Query("select p from Passenger p where p.draftMode = false and p.customer.id = :customerId")
	Collection<Passenger> findAllPublishedPassengersFromCustomerId(final int customerId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findAllPassengersFromCustomerId(final int customerId);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId and bp.passenger.id = :passengerId")
	Collection<BookingPassenger> findAssignationFromBookingIdAndPassengerId(final int bookingId, final int passengerId);

}
