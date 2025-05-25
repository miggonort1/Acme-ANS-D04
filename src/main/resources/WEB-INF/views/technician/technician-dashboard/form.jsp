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
			<acme:print code="administrator.dashboard.form.label.totalInProgress"/>
		</th>
		<td>
			<acme:print value="${totalMaintenanceInProgress}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="administrator.dashboard.form.label.totalCompleted"/>
		</th>
		<td>
			<acme:print value="${totalMaintenanceCompleted}"/>
		</td>
	</tr>	
</table>

<acme:return/>

