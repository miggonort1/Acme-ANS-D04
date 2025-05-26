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

<acme:form>
    <acme:input-textbox code="manager.leg.form.label.flightNumber" path="flightNumber"/>
    <acme:input-moment code="manager.leg.form.label.scheduledDeparture" path="scheduledDeparture"/>
    <acme:input-moment code="manager.leg.form.label.scheduledArrival" path="scheduledArrival"/>
    <acme:input-select code="manager.leg.form.label.status" path="status" choices="${statuses}"/>
    <acme:input-select code="manager.leg.form.label.departureAirport" path="departureAirport" choices="${departureAirportChoices}"/>
	<acme:input-select code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirportChoices}"/>
	<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${aircraftChoices}"/>
    <acme:input-double code="manager.leg.form.label.durationHours" path="durationHours" readonly="True"/>

	<jstl:choose>
	<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
		<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update?masterId=${masterId}"/>
		<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
		<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
	</jstl:when>
	<jstl:when test="${_command == 'create' && allowCreate}">
		<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create?masterId=${masterId}"/>
	</jstl:when>
</jstl:choose>

</acme:form>	