<%--
- list.jsp
-
- Copyright (C) 2012-2024 Rafael Corchuelo.
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


<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form >
	<acme:input-textbox code="crew-member.activity-log.form.label.incidentType" path="incidentType" readonly="draftMode"/>
	<acme:input-textarea code="crew-member.activity-log.form.label.description" path="description" readonly="draftMode"/>
	<acme:input-textbox code="crew-member.activity-log.form.label.severityLevel" path="severityLevel" readonly="draftMode"/>
	
	<jstl:choose>
		<jstl:when test="${_command == 'create' && showAction}">
			<acme:submit code="crew-member.activity-log.form.button.create" action="/crew-member/activity-log/create?assignmentId=${assignmentId}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update') && showAction && draftMode == true}">
			<acme:submit code="crew-member.activity-log.form.button.update" action="/crew-member/activity-log/update"/>
			<acme:submit code="crew-member.activity-log.form.button.delete" action="/crew-member/activity-log/delete"/>
		</jstl:when>
	</jstl:choose>
		<jstl:if test="${acme:anyOf(_command, 'show|update') && showAction && draftMode == true && draftModeFlightAssignment == false}">
			<acme:submit code="crew-member.activity-log.form.button.publish" action="/crew-member/activity-log/publish"/>
		</jstl:if>
</acme:form>

		
