<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>		
	<acme:input-select code="customer.booking-passenger.form.label.passport-number" path="passenger" choices="${passengers}" readonly="${_command != 'create'}"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|delete')}">	
			<acme:input-textbox code="customer.booking-passenger.form.label.full-name" path="passenger.fullName" readonly="true"/>
			<acme:input-textbox code="customer.booking-passenger.form.label.email" path="passenger.email" readonly="true"/>	
			<acme:input-moment code="customer.booking-passenger.form.label.date-of-birth" path="passenger.dateOfBirth" readonly="true"/>
			<acme:input-textbox code="customer.booking-passenger.form.label.special-needs" path="passenger.specialNeeds" readonly="true"/>
			<jstl:if test="${draftMode == true }">
				<acme:submit code="customer.booking-passenger.form.button.delete" action="/customer/booking-passenger/delete"/>
			</jstl:if>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking-passenger.list.button.add" action="/customer/booking-passenger/create?masterId=${masterId}"/>
		</jstl:when>
	</jstl:choose>

</acme:form>