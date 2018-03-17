package flights.results;

import java.util.Date;

public class FinishedRouteStatusInfo extends RouteStatusInfo {
	private final Date endedAt;
	
	public FinishedRouteStatusInfo(RouteStatusInfo routeInfo) {
		super(routeInfo.getStartedAt(), routeInfo.getRetries());
		endedAt = new Date();
	}
	
	@Override
	public String toString() {
		return "StartedAt: " + dateTimeFormater.format(getStartedAt()) + 
				"; EndedAt: " + dateTimeFormater.format(endedAt) + 
				"; Retries: " + getRetries() + ";";
	}
}
