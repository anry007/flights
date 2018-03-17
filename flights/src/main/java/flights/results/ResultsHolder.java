package flights.results;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResultsHolder {
	Map<String, RouteStatusInfo> holder = new ConcurrentHashMap<>();
	
	public void startRoute(String destination, Date startDate, Date endDate) {
		updateRoute(destination, startDate, endDate);
	}
	
	public void updateRoute(String destination, Date startDate, Date endDate) {
		RouteStatusInfo routeInfo = holder.getOrDefault(destination, new RouteStatusInfo());
		routeInfo.updateStartDate(startDate);
		routeInfo.updateEndDate(endDate);
		holder.put(destination, routeInfo);
	}
	
	public void completeRoute(String destination) {
		RouteStatusInfo routeInfo = getRoute(destination);
		holder.put(destination, new FinishedRouteStatusInfo(routeInfo));
	}

	private RouteStatusInfo getRoute(String destination) {
		RouteStatusInfo routeInfo = holder.get(destination);
		if(routeInfo == null) {
			throw new IllegalArgumentException("No destination {" + destination + "} found.");
		}
		return routeInfo;
	}
	
	public void routeFailed(String destination) {
		RouteStatusInfo routeInfo = getRoute(destination);
		routeInfo.incrementRetries();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String destination : holder.keySet()) {
			sb.append("Destination: " + destination + "; ")
			.append(holder.get(destination).toString())
			.append("\n");
		}
		
		return sb.toString();
	}
}
