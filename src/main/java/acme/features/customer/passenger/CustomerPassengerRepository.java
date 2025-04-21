
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.Passenger;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(final int passengerId);

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(final int bookingId);

	@Query("select bp.booking from BookingPassenger bp where bp.passenger.id = :passengerId")
	Collection<Booking> findBookingsByPassengerId(final int passengerId);

	@Query("select bp.passenger from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(final int bookingId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findPassengersByCustomerId(final int customerId);

	@Query("select p from Passenger p where p.passportNumber = :passportNumber")
	Passenger findPassengerByPassportNumber(String passportNumber);

}
