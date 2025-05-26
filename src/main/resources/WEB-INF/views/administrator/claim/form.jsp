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
	<acme:input-select code="administrator.claim.form.label.leg" path="leg" choices="${legs}" />
	<acme:input-moment code="administrator.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-email code="administrator.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textbox code="administrator.claim.form.label.description" path="description"/>
	<acme:input-select code="administrator.claim.form.label.type" path="type" choices="${type}" />
		<acme:input-textbox code="administrator.claim.form.label.status" path="status" readonly="true"/>
	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false}">
				<acme:button code="administrator.trackingLog.list.title" action="/administrator/tracking-log/list?masterId=${id}"/>			
		</jstl:when>	
	</jstl:choose>
</acme:form>
