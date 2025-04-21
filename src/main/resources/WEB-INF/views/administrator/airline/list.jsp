<%--
- list.jsp
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

<acme:list>
	<acme:list-column code="administrator.airline.list.label.name" path="name" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.iataCode" path="iataCode" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.website" path="website" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.type" path="type" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.foundationMoment" path="foundationMoment" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.emailAdress" path="emailAdress" width="10%"/>
		<acme:list-column code="administrator.airline.list.label.phoneNumber" path="phoneNumber" width="10%"/>		
	<acme:list-payload path="payload"/>
	</acme:list>
	
	<jstl:if test="${_command == 'list'}">
		<acme:button code="administrator.airline.list.button.create" action="/administrator/airline/create"/>
	</jstl:if>
	
