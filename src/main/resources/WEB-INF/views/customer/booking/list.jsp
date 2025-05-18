<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking.list.label.locator-code" path="locatorCode" width="25%"/>
	<acme:list-column code="customer.booking.list.label.purchase-moment" path="purchaseMoment" width="25%"/>
	<acme:list-column code="customer.booking.list.label.price" path="price" width="25%"/>
	<acme:list-column code="customer.booking.list.label.draft-mode" path="draftMode" width="25%"/>	
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="customer.booking.list.button.create" action="/customer/booking/create"/>