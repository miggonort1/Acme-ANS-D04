<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<div>
    <table class="table table-sm table-bordered">

        <tr>
            <th><fmt:message key="agent.dashboard.form.label.ratio-of-claims-resolved"/></th>
            <td>
                <jstl:choose>
                    <jstl:when test="${not empty ratioOfClaimsResolved}">
                        <acme:print value="${ratioOfClaimsResolved}"/>
                    </jstl:when>
                    <jstl:otherwise>
                        <acme:print value="N/A"/>
                    </jstl:otherwise>
                </jstl:choose>
            </td>
        </tr>

        <tr>
            <th><fmt:message key="agent.dashboard.form.label.ratio-of-claims-rejected"/></th>
            <td>
                <jstl:choose>
                    <jstl:when test="${not empty ratioOfClaimsRejected}">
                        <acme:print value="${ratioOfClaimsRejected}"/>
                    </jstl:when>
                    <jstl:otherwise>
                        <acme:print value="N/A"/>
                    </jstl:otherwise>
                </jstl:choose>
            </td>
        </tr>

        <tr>
            <th><fmt:message key="agent.dashboard.form.label.top-months-with-claims"/></th>
            <td>
                <jstl:choose>
                    <jstl:when test="${not empty topMonthsWithHighestNumberOfClaims}">
                        <ul>
                            <jstl:forEach var="entry" items="${topMonthsWithHighestNumberOfClaims}">
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
		    <th><fmt:message key="agent.dashboard.form.label.average-logs-per-claim"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty averageNumberOfLogsTheirClaimsHave}">
		                <acme:print value="${averageNumberOfLogsTheirClaimsHave}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>
		
		<tr>
		    <th><fmt:message key="agent.dashboard.form.label.minimum-logs-per-claim"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty minimumNumberOfLogsTheirClaimsHave}">
		                <acme:print value="${minimumNumberOfLogsTheirClaimsHave}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>
		
		<tr>
		    <th><fmt:message key="agent.dashboard.form.label.maximum-logs-per-claim"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty maximumNumberOfLogsTheirClaimsHave}">
		                <acme:print value="${maximumNumberOfLogsTheirClaimsHave}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>
		
		<tr>
		    <th><fmt:message key="agent.dashboard.form.label.deviation-logs-per-claim"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty deviationNumberOfLogsTheirClaimsHave}">
		                <acme:print value="${deviationNumberOfLogsTheirClaimsHave}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>

		<tr>
		    <th><fmt:message key="agent.dashboard.form.label.average-claims-assisted"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty averageNumberOfClaimsTheyAssisted}">
		                <acme:print value="${averageNumberOfClaimsTheyAssisted}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>
		
		<tr>
		    <th><fmt:message key="agent.dashboard.form.label.minimum-claims-assisted"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty minimumNumberOfClaimsTheyAssisted}">
		                <acme:print value="${minimumNumberOfClaimsTheyAssisted}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>
		
		<tr>
		    <th><fmt:message key="agent.dashboard.form.label.maximum-claims-assisted"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty maximumNumberOfClaimsTheyAssisted}">
		                <acme:print value="${maximumNumberOfClaimsTheyAssisted}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>
		
		<tr>
		    <th><fmt:message key="agent.dashboard.form.label.deviation-claims-assisted"/></th>
		    <td>
		        <jstl:choose>
		            <jstl:when test="${not empty deviationNumberOfClaimsTheyAssisted}">
		                <acme:print value="${deviationNumberOfClaimsTheyAssisted}"/>
		            </jstl:when>
		            <jstl:otherwise>
		                <acme:print value="N/A"/>
		            </jstl:otherwise>
		        </jstl:choose>
		    </td>
		</tr>
		
    </table>
</div>
