
package acme.features.customer.bookingPassenger;

import java.util.Collection;
import java.util.Date;

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

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<BookingPassenger> findBookingPassengersByBookingId(final int bookingId);

	@Query("select b.purchaseMoment from Booking b where b.id = :bookingId")
	Date findPurchaseMomentByBookingId(int bookingId);

	@Query("select bp.passenger.id from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<Integer> findPassengerIdsInBooking(int bookingId);

	@Query("select p from Passenger p where p.draftMode = false and p.customer.id = :customerId and p.dateOfBirth<:purchaseMoment")
	Collection<Passenger> findValidPassengers(int customerId, Date purchaseMoment);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId and bp.passenger.id = :passengerId")
	Collection<BookingPassenger> findAssignationFromBookingIdAndPassengerId(final int bookingId, final int passengerId);

}
