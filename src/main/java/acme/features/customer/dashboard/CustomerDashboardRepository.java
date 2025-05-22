
package acme.features.customer.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.customer.id =:customerId and b.draftMode = false")
	Collection<Booking> findAllBookingsOf(int customerId);

}
