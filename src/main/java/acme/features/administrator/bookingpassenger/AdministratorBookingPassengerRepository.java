
package acme.features.administrator.bookingpassenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.booking.Passenger;

@Repository
public interface AdministratorBookingPassengerRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select br from BookingPassenger br where br.booking.id = :bookingId")
	Collection<BookingPassenger> findBookingPassengersByBookingId(int bookingId);

	@Query("select br from BookingPassenger br where br.id = :bookingPassengerId")
	BookingPassenger findBookingPassengerById(int bookingPassengerId);

	@Query("select p from Passenger p, BookingPassenger br where p.id=br.passenger.id and br.id = :bookingPassengerId")
	Collection<Passenger> findPassengersByBookingPassengerId(int bookingPassengerId);

	@Query("select p from Passenger p where p.draftMode = false and p.customer.id = :customerId")
	Collection<Passenger> findAllPublishedPassengersFromCustomerId(final int customerId);
}
