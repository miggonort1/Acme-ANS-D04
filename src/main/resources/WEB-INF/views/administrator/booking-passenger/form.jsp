

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
<acme:input-textbox code="administrator.booking-passenger.form.label.passport-number" path="passenger.passportNumber" readonly="true"/>
    <acme:input-textbox code="administrator.booking-passenger.form.label.full-name" path="passenger.fullName" readonly="true"/>
	<acme:input-textbox code="administrator.booking-passenger.form.label.email" path="passenger.email" readonly="true"/>	
	<acme:input-moment code="administrator.booking-passenger.form.label.date-of-birth" path="passenger.dateOfBirth" readonly="true"/>
	<acme:input-textbox code="administrator.booking-passenger.form.label.special-needs" path="passenger.specialNeeds" readonly="true"/>
	
</acme:form>

