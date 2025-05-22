
package acme.features.customer.dashboard;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.forms.CustomerDashboard;
import acme.realms.Customer;

@GuiService
public class CustomerDashboardShowService extends AbstractGuiService<Customer, CustomerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void load() {
		Integer customerId = this.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<Booking> bookings = this.repository.findAllBookingsOf(customerId);

		if (bookings == null || bookings.isEmpty()) {
			System.out.println("No se han encontrado bookings para el cliente " + customerId);
			CustomerDashboard emptyDashboard = new CustomerDashboard();
			super.getBuffer().addData(emptyDashboard);
			return;
		}
		CustomerDashboard dashboard = new CustomerDashboard();

		//----------------------------------
		List<String> lastFiveDestinations = bookings.stream().sorted(Comparator.comparing(Booking::getPurchaseMoment).reversed()).limit(5).map(b -> b.getFlight().getDestinationCity()).collect(Collectors.toList());
		//----------------------------------
		Map<String, Double> spentMoneyLastYear = bookings.stream().filter(b -> b.getPurchaseMoment() != null && MomentHelper.isAfterOrEqual(b.getPurchaseMoment(), MomentHelper.deltaFromCurrentMoment(-1, ChronoUnit.YEARS))).map(Booking::getPrice)
			.filter(Objects::nonNull).collect(Collectors.groupingBy(Money::getCurrency, Collectors.summingDouble(Money::getAmount)));
		spentMoneyLastYear.putIfAbsent("EUR", 0.0);
		//----------------------------------
		Map<String, Integer> bookingsGroupedByTravelClass = bookings.stream().filter(b -> b.getTravelClass() != null && (b.getTravelClass() == TravelClass.ECONOMY || b.getTravelClass() == TravelClass.BUSINESS))
			.collect(Collectors.groupingBy(b -> b.getTravelClass().toString(), Collectors.summingInt(b -> 1)));

		bookingsGroupedByTravelClass.putIfAbsent("ECONOMY", 0);
		bookingsGroupedByTravelClass.putIfAbsent("BUSINESS", 0);
		//----------------------------------
		List<Booking> recentBookings = bookings.stream().filter(b -> b.getPurchaseMoment() != null && MomentHelper.isAfterOrEqual(b.getPurchaseMoment(), MomentHelper.deltaFromCurrentMoment(-5, ChronoUnit.YEARS)) && b.getPrice() != null)
			.collect(Collectors.toList());
		Map<String, List<Double>> amountsByCurrency = recentBookings.stream().collect(Collectors.groupingBy(b -> b.getPrice().getCurrency(), Collectors.mapping(b -> b.getPrice().getAmount(), Collectors.toList())));
		Map<String, Double> bookingCountCost = new HashMap<>();
		Map<String, Double> bookingAverageCost = new HashMap<>();
		Map<String, Double> bookingMinimumCost = new HashMap<>();
		Map<String, Double> bookingMaximumCost = new HashMap<>();
		Map<String, Double> bookingDeviationCost = new HashMap<>();
		for (Map.Entry<String, List<Double>> entry : amountsByCurrency.entrySet()) {
			String currency = entry.getKey();
			List<Double> amounts = entry.getValue();

			double count = amounts.size();
			double average = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
			double min = amounts.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
			double max = amounts.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
			double stddev = Math.sqrt(amounts.stream().mapToDouble(a -> Math.pow(a - average, 2)).average().orElse(0.0));

			bookingCountCost.put(currency, count);
			bookingAverageCost.put(currency, average);
			bookingMinimumCost.put(currency, min);
			bookingMaximumCost.put(currency, max);
			bookingDeviationCost.put(currency, stddev);
		}
		bookingCountCost.putIfAbsent("EUR", 0.0);
		bookingAverageCost.putIfAbsent("EUR", 0.0);
		bookingMinimumCost.putIfAbsent("EUR", 0.0);
		bookingMaximumCost.putIfAbsent("EUR", 0.0);
		bookingDeviationCost.putIfAbsent("EUR", 0.0);
		//----------------------------------
		List<Integer> passengerCounts = bookings.stream().map(Booking::getNumberOfPassengers).collect(Collectors.toList());

		int bookingCountPassengers = passengerCounts.size();
		double bookingAveragePassengers = passengerCounts.stream().mapToInt(Integer::intValue).average().orElse(0.0);
		int bookingMinimumPassengers = passengerCounts.stream().mapToInt(Integer::intValue).min().orElse(0);
		int bookingMaximumPassengers = passengerCounts.stream().mapToInt(Integer::intValue).max().orElse(0);
		double bookingDeviationPassengers = Math.sqrt(passengerCounts.stream().mapToDouble(p -> Math.pow(p - bookingAveragePassengers, 2)).average().orElse(0.0));
		//----------------------------------
		dashboard.setLastFiveDestinations(lastFiveDestinations);
		dashboard.setSpentMoneyLastYear(spentMoneyLastYear);
		dashboard.setBookingsGroupedByTravelClass(bookingsGroupedByTravelClass);
		dashboard.setBookingCountCost(bookingCountCost);
		dashboard.setBookingAverageCost(bookingAverageCost);
		dashboard.setBookingMinimumCost(bookingMinimumCost);
		dashboard.setBookingMaximumCost(bookingMaximumCost);
		dashboard.setBookingDeviationCost(bookingDeviationCost);
		dashboard.setBookingCountPassengers(bookingCountPassengers);
		dashboard.setBookingAveragePassengers(bookingAveragePassengers);
		dashboard.setBookingMinimumPassengers(bookingMinimumPassengers);
		dashboard.setBookingMaximumPassengers(bookingMaximumPassengers);
		dashboard.setBookingDeviationPassengers(bookingDeviationPassengers);
		//----------------------------------

		super.getBuffer().addData(dashboard);

	}
	@Override
	public void unbind(final CustomerDashboard object) {
		Dataset dataset = super.unbindObject(object, "lastFiveDestinations", "spentMoneyLastYear", "bookingsGroupedByTravelClass", "bookingCountCost", "bookingAverageCost", "bookingMinimumCost", "bookingMaximumCost", "bookingDeviationCost",
			"bookingCountPassengers", "bookingAveragePassengers", "bookingMinimumPassengers", "bookingMaximumPassengers", "bookingDeviationPassengers");

		super.getResponse().addData(dataset);
	}

}
