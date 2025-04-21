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

	<acme:input-select code="technician.maintenance-record.form.label.aircraft" path="aircraft" choices="${aircrafts}" />	
	 <acme:input-select code="technician.maintenance-record.form.label.status" path="status" choices="${status}" />
	<acme:input-moment code="technician.maintenance-record.form.label.inspectionDueDate" path="inspectionDueDate"/>
	<acme:input-money code="technician.maintenance-record.form.label.estimatedCost" path="estimatedCost"/>
	<acme:input-textbox code="technician.maintenance-record.form.label.notes" path="notes"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode==false }">
	 <acme:button code="technician.task.list.title" action="/technician/task/list-maintenance-record?masterId=${id}"/>
	 </jstl:when>
	</jstl:choose>
	
	<jstl:choose>
	<jstl:when test="${_command == 'create'}">
		<acme:submit  code="technician.maintenance-record.form.button.create" action="/technician/maintenance-record/create"/>
	</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode==true }">
	 <acme:input-moment code="technician.maintenance-record.form.label.execution" path="moment" readonly="true" />
		<acme:submit  code="technician.maintenance-record.form.button.update" action="/technician/maintenance-record/update"/>
		<acme:submit  code="technician.maintenance-record.form.button.publish" action="/technician/maintenance-record/publish"/>
		<acme:submit  code="technician.maintenance-record.form.button.delete" action="/technician/maintenance-record/delete"/>
			<acme:button code="technician.maintenance-record.form.button.task" action="/technician/task/list-maintenance-record?masterId=${id}"/>		
			<acme:button code="technician.maintenance-record.form.button.maintenance-record-task" action="/technician/maintenance-record-task/list-maintenance-record?masterId=${id}"/>
	</jstl:when>	
	</jstl:choose>
</acme:form>
