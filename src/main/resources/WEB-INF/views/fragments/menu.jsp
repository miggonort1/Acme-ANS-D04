<%--
- menu.jsp
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
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-student1" action="https://stackoverflow.com"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-student2" action="https://chatgpt.com"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-student3" action="https://gmail.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-student4" action="http://ev.us.es/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-student5" action="https://github.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airports" action="/administrator/airport/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-aircraft" action="/administrator/aircraft/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.manager" access="hasRealm('Manager')">
			<acme:menu-suboption code="master.menu.manager.my-flights" action="/manager/flight/list-mine"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.my-maintenance-record" action="/technician/maintenance-record/list-mine"/>
			<acme:menu-suboption code="master.menu.technician.all-maintenance-record" action="/technician/maintenance-record/list-all"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.technician.my-tasks" action="/technician/task/list-mine"/>
			<acme:menu-separator/>	
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.bookings" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.passengers" action="/customer/passenger/list"/>
    </acme:menu-option> 
    
		<acme:menu-option code="master.menu.agent" access="hasRealm('Agent')">
			<acme:menu-suboption code="master.menu.agent.my-claim" action="/agent/claim/list-mine"/>
		</acme:menu-option>
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated">
			<acme:menu-suboption code="master.menu.technician.all-maintenance-record" action="/technician/maintenance-record/list-all"/>
			
			<acme:menu-separator/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.crew-member" access="hasRealm('CrewMember')">
			<acme:menu-suboption code="master.menu.crew-member.flight-assignments-completed" action="/crew-member/flight-assignment/list-completed"/>
			<acme:menu-suboption code="master.menu.crew-member.flight-assignments-planned" action="/crew-member/flight-assignment/list-planned"/>
		</acme:menu-option>
	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-technician" action="/authenticated/technician/create" access="!hasRealm('Technician')"/>
			<acme:menu-suboption code="master.menu.user-account.technician-profile" action="/authenticated/technician/update" access="hasRealm('Technician')"/>
			<acme:menu-suboption code="master.menu.user-account.become-crew-member" action="/authenticated/crew-member/create" access="!hasRealm('CrewMember')"/>
			<acme:menu-suboption code="master.menu.user-account.crew-member-profile" action="/authenticated/crew-member/update" access="hasRealm('CrewMember')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>

