<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<div>

    <table class="table table-sm table-bordered">

        <tr>
            <th><fmt:message key="customer.dashboard.form.label.last-five-destinations"/></th>
            <td>
<jstl:choose>
    <jstl:when test="${not empty lastFiveDestinations}">
        <ul>
            <jstl:forEach var="dest" items="${lastFiveDestinations}">
                <li><acme:print value="${dest}"/></li>
            </jstl:forEach>
        </ul>
    </jstl:when>
    <jstl:otherwise>
        <acme:print value="N/A"/>
    </jstl:otherwise>
</jstl:choose>
            </td>
        </tr>

        <tr>
    <th><fmt:message key="customer.dashboard.form.label.spent-money-last-year"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty spentMoneyLastYear}">
                <ul>
                    <jstl:forEach var="entry" items="${spentMoneyLastYear}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
                </ul>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.bookings-by-travel-class"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingsGroupedByTravelClass}">
                <ul>
                    <li><fmt:message key="customer.dashboard.form.label.economy-bookings"/>: 
                        <acme:print value="${bookingsGroupedByTravelClass['ECONOMY']}"/></li>
                    <li><fmt:message key="customer.dashboard.form.label.business-bookings"/>: 
                        <acme:print value="${bookingsGroupedByTravelClass['BUSINESS']}"/></li>
                </ul>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-count-cost"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingCountCost}">
                <ul>
                    <jstl:forEach var="entry" items="${bookingCountCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
                </ul>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-average-cost"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingAverageCost}">
                <ul>
                    <jstl:forEach var="entry" items="${bookingAverageCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
                </ul>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-minimum-cost"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingMinimumCost}">
                <ul>
                    <jstl:forEach var="entry" items="${bookingMinimumCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
                </ul>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-maximum-cost"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingMaximumCost}">
                <ul>
                    <jstl:forEach var="entry" items="${bookingMaximumCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
                </ul>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-deviation-cost"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingDeviationCost}">
                <ul>
                    <jstl:forEach var="entry" items="${bookingDeviationCost}">
                        <li><acme:print value="${entry.key}"/>: <acme:print value="${entry.value}"/></li>
                    </jstl:forEach>
                </ul>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>


     <tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-count-passengers"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingCountPassengers}">
                <acme:print value="${bookingCountPassengers}"/>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-average-passengers"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingAveragePassengers}">
                <acme:print value="${bookingAveragePassengers}"/>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-minimum-passengers"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingMinimumPassengers}">
                <acme:print value="${bookingMinimumPassengers}"/>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-maximum-passengers"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingMaximumPassengers}">
                <acme:print value="${bookingMaximumPassengers}"/>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>

<tr>
    <th><fmt:message key="customer.dashboard.form.label.booking-deviation-passengers"/></th>
    <td>
        <jstl:choose>
            <jstl:when test="${not empty bookingDeviationPassengers}">
                <acme:print value="${bookingDeviationPassengers}"/>
            </jstl:when>
            <jstl:otherwise>
                <acme:print value="N/A"/>
            </jstl:otherwise>
        </jstl:choose>
    </td>
</tr>


    </table>
</div>
