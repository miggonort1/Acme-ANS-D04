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
	<acme:input-select code="agent.claim.form.label.leg" path="leg" choices="${legs}" />
	<acme:input-moment code="agent.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-email code="agent.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textbox code="agent.claim.form.label.description" path="description"/>
	<acme:input-select code="agent.claim.form.label.type" path="type" choices="${type}" />
	<acme:input-select code="agent.claim.form.label.status" path="status" choices="${status}" />
	
	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false}">
				<acme:button code="agent.trackingLog.list.tittle" action="/agent/tracking-log/list-mine?masterId=${id}"/>			
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit  code="agent.claim.form.button.create" action="/agent/claim/create?masterId=${masterId}"/>
		</jstl:when>
			<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode==true }">
			<acme:submit  code="agent.claim.form.button.update" action="/agent/claim/update?masterId=${masterId}"/>
			<acme:submit  code="agent.claim.form.button.publish" action="/agent/claim/publish"/>
			<acme:submit  code="agent.claim.form.button.delete" action="/agent/claim/delete"/>
			<acme:button code="agent.trackingLog.list.title" action="/agent/tracking-log/list-mine?masterId=${id}"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>
