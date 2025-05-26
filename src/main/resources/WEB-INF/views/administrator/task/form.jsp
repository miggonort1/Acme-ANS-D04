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

	 <acme:input-select code="administrator.task.form.label.type" path="type" choices="${type}" />
	<acme:input-textbox code="administrator.task.form.label.description" path="description"/>
	<acme:input-double code="administrator.task.form.label.priority" path="priority"/>
	<acme:input-textbox code="administrator.task.form.label.estimatedDuration" path="estimatedDuration"/>
	
	
</acme:form>
