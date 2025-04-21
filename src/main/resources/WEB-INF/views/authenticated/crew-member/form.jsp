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
	<acme:input-textbox code="authenticated.crew-member.form.label.employeeCode" path="employeeCode"/>
	<acme:input-textbox code="authenticated.crew-member.form.label.phoneNumber" path="phoneNumber"/>
	<acme:input-textbox code="authenticated.crew-member.form.label.languageSkills" path="languageSkills"/>
	<acme:input-select 
	    code="authenticated.crew-member.form.label.availabilityStatus" 
	    path="availabilityStatus" 
	    choices="${availabilityStatusChoices}"/>
	 <acme:input-select 
        code="authenticated.crew-member.form.label.airline" 
        path="airline" 
        choices="${airlineChoices}"/>
	<acme:input-double code="authenticated.crew-member.form.label.yearsOfExperience" path="yearsOfExperience"/>
	<acme:input-money code="authenticated.crew-member.form.label.salary" path="salary" readonly="true"/>
	
	<jstl:if test="${_command == 'create'}">
		<acme:submit code="authenticated.crew-member.form.button.create" action="/authenticated/crew-member/create"/>
	</jstl:if>
	<jstl:if test="${_command == 'update'}">
	<acme:submit  code="authenticated.crew-member.form.button.update" action="/authenticated/crew-member/update"/>
	</jstl:if>
</acme:form>
