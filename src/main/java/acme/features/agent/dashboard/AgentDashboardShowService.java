
package acme.features.agent.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.TrackingLogStatus;
import acme.entities.forms.AgentDashboard;
import acme.realms.Agent;

@GuiService
public class AgentDashboardShowService extends AbstractGuiService<Agent, AgentDashboard> {

	@Autowired
	private AgentDashboardRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Agent.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();

		Collection<Claim> claims = this.repository.findClaimsByAgentId(agentId);

		AgentDashboard dashboard = new AgentDashboard();

		int totalClaims = claims.size();
		long resolved = claims.stream().filter(c -> c.getStatus() == TrackingLogStatus.ACCEPTED).count();
		long rejected = claims.stream().filter(c -> c.getStatus() == TrackingLogStatus.REJECTED).count();

		dashboard.setRatioOfClaimsResolved(totalClaims == 0 ? 0. : resolved * 1.0 / totalClaims);
		dashboard.setRatioOfClaimsRejected(totalClaims == 0 ? 0. : rejected * 1.0 / totalClaims);

		Map<String, Integer> monthCounts = claims.stream().collect(Collectors.groupingBy(c -> MomentHelper.format(c.getRegistrationMoment(), "yyyy-MM"), Collectors.summingInt(x -> 1)));
		Map<String, Integer> topMonths = monthCounts.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed()).limit(3).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		dashboard.setTopMonthsWithHighestNumberOfClaims(topMonths);

		List<Integer> logsPerClaim = claims.stream().map(c -> this.repository.findTrackingLogsByClaimId(c.getId()).size()).collect(Collectors.toList());

		double avgLogs = logsPerClaim.stream().mapToInt(Integer::intValue).average().orElse(0.0);
		int minLogs = logsPerClaim.stream().mapToInt(Integer::intValue).min().orElse(0);
		int maxLogs = logsPerClaim.stream().mapToInt(Integer::intValue).max().orElse(0);
		double stdDevLogs = Math.sqrt(logsPerClaim.stream().mapToDouble(n -> Math.pow(n - avgLogs, 2)).average().orElse(0.0));

		dashboard.setAverageNumberOfLogsTheirClaimsHave(avgLogs);
		dashboard.setMinimumNumberOfLogsTheirClaimsHave(minLogs);
		dashboard.setMaximumNumberOfLogsTheirClaimsHave(maxLogs);
		dashboard.setDeviationNumberOfLogsTheirClaimsHave(stdDevLogs);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime oneMonthAgo = now.minusMonths(1);

		Collection<Claim> recentClaims = this.repository.findClaimsByAgentId(agentId).stream().filter(c -> {
			LocalDateTime regDate = c.getRegistrationMoment().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			return !regDate.isBefore(oneMonthAgo);
		}).collect(Collectors.toList());

		Map<LocalDate, Long> claimsCountByDay = recentClaims.stream().collect(Collectors.groupingBy(c -> c.getRegistrationMoment().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), Collectors.counting()));

		List<LocalDate> allDays = new ArrayList<>();
		for (LocalDate date = oneMonthAgo.toLocalDate(); !date.isAfter(now.toLocalDate()); date = date.plusDays(1))
			allDays.add(date);

		List<Integer> claimsPerDay = allDays.stream().map(day -> claimsCountByDay.getOrDefault(day, 0L).intValue()).collect(Collectors.toList());

		double avg = claimsPerDay.stream().mapToInt(Integer::intValue).average().orElse(0.0);
		int min = claimsPerDay.stream().mapToInt(Integer::intValue).min().orElse(0);
		int max = claimsPerDay.stream().mapToInt(Integer::intValue).max().orElse(0);
		double stddev = Math.sqrt(claimsPerDay.stream().mapToDouble(n -> Math.pow(n - avg, 2)).average().orElse(0.0));

		dashboard.setAverageNumberOfClaimsTheyAssisted(avg);
		dashboard.setMinimumNumberOfClaimsTheyAssisted(min);
		dashboard.setMaximumNumberOfClaimsTheyAssisted(max);
		dashboard.setDeviationNumberOfClaimsTheyAssisted(stddev);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AgentDashboard object) {
		Dataset dataset = super.unbindObject(object, "ratioOfClaimsResolved", "ratioOfClaimsRejected", "topMonthsWithHighestNumberOfClaims", "averageNumberOfLogsTheirClaimsHave", "minimumNumberOfLogsTheirClaimsHave", "maximumNumberOfLogsTheirClaimsHave",
			"deviationNumberOfLogsTheirClaimsHave", "averageNumberOfClaimsTheyAssisted", "minimumNumberOfClaimsTheyAssisted", "maximumNumberOfClaimsTheyAssisted", "deviationNumberOfClaimsTheyAssisted");

		super.getResponse().addData(dataset);
	}

}
