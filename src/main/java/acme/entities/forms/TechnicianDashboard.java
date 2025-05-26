/*
 * Dashboard.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.entities.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Integer						totalMaintenancePending;
	Integer						totalMaintenanceInProgress;
	Integer						totalMaintenanceCompleted;

	String						maintentanceRecordWithNearestDueDate;

	String						aircraftHigherNumberOfTask;

	Map<String, Double>			averageNumberOfEstimatedCost;
	Map<String, Double>			minimumNumberOfEstimatedCost;
	Map<String, Double>			maximumNumberOfEstimatedCost;
	Map<String, Double>			deviationNumberOfEstimatedCost;

	Double						averageNumberOfEstimatedDuration;
	Double						minimumNumberOfEstimatedDuration;
	Double						maximumNumberOfEstimatedDuration;
	Double						deviationNumberOfEstimatedDuration;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
