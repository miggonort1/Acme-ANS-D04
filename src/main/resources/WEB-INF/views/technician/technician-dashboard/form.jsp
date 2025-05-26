<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:print code="technician.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.totalPending"/>
		</th>
		<td>
			<acme:print value="${totalMaintenancePending}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.totalInProgress"/>
		</th>
		<td>
			<acme:print value="${totalMaintenanceInProgress}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.totalCompleted"/>
		</th>
		<td>
			<acme:print value="${totalMaintenanceCompleted}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.mr"/>
		</th>
		<td>
			<acme:print value="${maintentanceRecordWithNearestDueDate}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.higernumber"/>
		</th>
		<td>
			<acme:print value="${aircraftHigherNumberOfTask}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.avg"/>
		</th>
		<td>
			<jstl:forEach var="entry" items="${averageNumberOfEstimatedCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
		</td>
	</tr>
	<tr>
		<th scope="row">
		
			<acme:print code="technician.dashboard.form.label.min"/>
		</th>
		<td>
			<jstl:forEach var="entry" items="${minimumNumberOfEstimatedCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.max"/>
		</th>
		<td>
			<jstl:forEach var="entry" items="${maximumNumberOfEstimatedCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.dev"/>
		</th>
		<td>
			<jstl:forEach var="entry" items="${deviationNumberOfEstimatedCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.avg"/>
		</th>
		<td>
		
			<acme:print value="${averageNumberOfEstimatedDuration}"/>
			
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.min"/>
		</th>
		<td>
		
			<acme:print value="${minimumNumberOfEstimatedDuration}"/>
			
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.max"/>
		</th>
		<td>
		
			<acme:print value="${maximumNumberOfEstimatedDuration}"/>
			
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="technician.dashboard.form.label.dev"/>
		</th>
		<td>
		
			<acme:print value="${deviationNumberOfEstimatedDuration}"/>
			
		</td>
	</tr>	
</table>

<acme:return/>

