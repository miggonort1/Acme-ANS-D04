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

<acme:list>
	<acme:list-column code="technician.maintenance-record-task.list.label.maintenance-record" path="maintenanceRecord" width="10%"/>
	<acme:list-column code="technician.maintenance-record-task.list.label.task" path="task" width="40%"/>
</acme:list>

<acme:button code="technician.maintenance-record-task.list.button.create" action="/technician/maintenance-record-task/create?masterId=${masterId}"/>