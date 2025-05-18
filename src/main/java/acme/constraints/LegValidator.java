
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.entities.flight.Flight;
import acme.entities.flight.FlightRepository;
import acme.entities.flight.Leg;

public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (leg == null)
			return false;

		if (leg.getFlight() == null || leg.getScheduledDeparture() == null || leg.getScheduledArrival() == null)
			return true;
		else {
			Flight flight = leg.getFlight();
			FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
			List<Leg> allLegs = repository.findLegsByFlight(flight.getId());

			// Validar que el leg no se solape con otro leg del mismo vuelo
			for (Leg otherLeg : allLegs) {
				if (Objects.equals(otherLeg.getId(), leg.getId()))
					continue;

				boolean overlaps = MomentHelper.isInRange(leg.getScheduledDeparture(), otherLeg.getScheduledDeparture(), otherLeg.getScheduledArrival());

				super.state(context, !overlaps, "scheduledDeparture", "acme.validation.manager.leg.valid-leg.overlap");
				result = result && !overlaps;
			}

			// Validar que scheduledArrival ≥ scheduledDeparture + 1 minuto
			boolean validDuration = !MomentHelper.isBefore(leg.getScheduledArrival(), MomentHelper.deltaFromMoment(leg.getScheduledDeparture(), 1, ChronoUnit.MINUTES));

			super.state(context, validDuration, "scheduledArrival", "acme.validation.manager.leg.valid-leg.time-conflict");
			result = result && validDuration;

			// Validar que las legs estén en orden cronológico (llegada[i] ≤ salida[i+1])
			allLegs.sort(Comparator.comparing(Leg::getScheduledDeparture));

			for (int i = 0; i < allLegs.size() - 1; i++) {
				Leg current = allLegs.get(i);
				Leg next = allLegs.get(i + 1);

				if (current == null || next == null || current.getScheduledArrival() == null || next.getScheduledDeparture() == null)
					continue; // por si acaso

				boolean correctOrder = !MomentHelper.isAfter(current.getScheduledArrival(), next.getScheduledDeparture());

				super.state(context, correctOrder, "scheduledArrival", "acme.validation.manager.leg.valid-leg.sequence-order");
				result = result && correctOrder;
			}
		}

		return result;
	}
}
