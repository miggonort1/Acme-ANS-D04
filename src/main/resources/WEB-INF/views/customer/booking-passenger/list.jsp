
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking-passenger.list.label.full-name" path="passenger.fullName" width="40%"/>
	<acme:list-column code="customer.booking-passenger.list.label.email" path="passenger.email" width="40%"/>
	<acme:list-column code="customer.booking-passenger.list.label.passport-number" path="passenger.passportNumber" width="20%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:choose>	
	<jstl:when test="${draftMode == true}">
		<acme:button code="customer.booking-passenger.list.button.add" action="/customer/booking-passenger/create?masterId=${masterId}"/>
	</jstl:when> 		
</jstl:choose>	
