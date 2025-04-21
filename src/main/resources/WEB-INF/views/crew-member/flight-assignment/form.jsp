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

<acme:form>
	<acme:input-moment code="crew-member.flight-assignment.form.label.moment" path="moment" readonly="true" />
	<acme:input-textbox code="crew-member.flight-assignment.form.label.crewMember" path="crewMember" readonly="true"/>
	<acme:input-select code="crew-member.flight-assignment.form.label.duty" path="duty" choices="${duties}" readonly="draftMode"/>
	<acme:input-select code="crew-member.flight-assignment.form.label.leg" path="leg" choices= "${legs}" readonly="draftMode"/>
	<acme:input-select code="crew-member.flight-assignment.form.label.status" path="currentStatus" choices= "${statusChoices}" readonly="draftMode"/>
	<acme:input-textarea code="crew-member.flight-assignment.form.label.remarks" path="remarks" readonly="draftMode"/>
	
	
	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="crew-member.flight-assignment.form.button.activity-log" action="/crew-member/activity-log/list?assignmentId=${id}"/>					
		</jstl:when> 
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')  && draftMode == true}">
			<acme:submit code="crew-member.flight-assignment.form.button.update" action="/crew-member/flight-assignment/update"/>
			<acme:submit code="crew-member.flight-assignment.form.button.delete" action="/crew-member/flight-assignment/delete"/>
			<acme:submit code="crew-member.flight-assignment.form.button.publish" action="/crew-member/flight-assignment/publish"/>
			<acme:button code="crew-member.flight-assignment.form.button.activity-log" action="/crew-member/activity-log/list?assignmentId=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="crew-member.flight-assignment.form.button.create" action="/crew-member/flight-assignment/create"/>
		</jstl:when>		
	</jstl:choose>	
</acme:form>

		
