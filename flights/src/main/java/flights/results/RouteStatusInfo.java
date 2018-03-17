package flights.results;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RouteStatusInfo {
	protected static final DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
	protected static final DateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd_H:m:s");
	private final Date startedAt;
	private Date startDate;
	private Date endDate;
	private int retries;
	
	protected RouteStatusInfo(Date statedAt, int retries) {
		this.startedAt = statedAt;
		this.retries = retries;
	}
	
	public RouteStatusInfo() {
		this(new Date(), 0);
	}
	
	public Date getStartedAt() {
		return startedAt;
	}
	
	@Override
	public String toString() {
		return "StartedAt: " + dateTimeFormater.format(startedAt) + 
				"; StartDate: " + dateFormater.format(startDate) + 
				"; EndDate: " + dateFormater.format(endDate) + 
				"; Retries: " + retries + ";";
	}

	public void updateStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void updateEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void incrementRetries() {
		retries++;
	}

	public int getRetries() {
		return retries;
	}
}
