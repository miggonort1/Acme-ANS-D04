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
	<acme:input-textbox code="authenticated.technician.form.label.licenseNumber" path="licenseNumber"/>
	<acme:input-textbox code="authenticated.technician.form.label.phoneNumber" path="phoneNumber"/>
	<acme:input-textbox code="authenticated.technician.form.label.specialisation" path="specialisation"/>
	<acme:input-checkbox code="authenticated.technician.form.label.annualHealthTest" path="annualHealthTest"/>
	<acme:input-double code="authenticated.technician.form.label.yearsOfExperience" path="yearsOfExperience"/>
	<acme:input-textarea code="authenticated.technician.form.label.certifications" path="certifications"/>
	
	<jstl:choose>
	<jstl:when test="${_command == 'create'}">
		<acme:submit  code="authenticated.technician.form.button.create" action="/authenticated/technician/create"/>
	</jstl:when>
	<jstl:when test="${acme:anyOf(_command, 'show|update') }">
	<acme:submit  code="authenticated.technician.form.button.update" action="/authenticated/technician/update"/>
	</jstl:when>
	</jstl:choose>
</acme:form>