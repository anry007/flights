package flights;

import java.util.Calendar;

import java.util.Date;



public class DateIterator {

	private final Date minStartDate;

	private final Date maxStartDate;
	private final Date maxEndDate;

	private final int minDuration;

	private final int maxDuration;

	private int startShift;

	private int endShift;

	

	public DateIterator(Date minStartDate, Date maxStartDate, Date maxEndDate, int minDuration, int maxDuration) {

		this.minStartDate = minStartDate;

		this.maxStartDate = maxStartDate;
		this.maxEndDate = maxEndDate;

		this.minDuration = minDuration;

		this.maxDuration = maxDuration;

		this.startShift = 0;

		this.endShift = 0;

	}

	

	public Date getStartDate() {

		return addDays(minStartDate, startShift);

	}



	public Date getEndDate() {

		return addDays(minStartDate, startShift + minDuration + endShift);

	}

	

	public boolean hasNext() {

		Date startDate = getStartDate();

		return maxStartDate.after(startDate) || maxStartDate.equals(startDate);

	}

	

	public void next() {

		if(maxEndDate.after(getEndDate()) && minDuration + endShift < maxDuration) {

			endShift++;

		} else {

			endShift = 0;

			startShift++;

		}

	}



	private static Date addDays(Date addTo, int numberOfDays) {

		Calendar c = Calendar.getInstance();

		c.setTime(addTo);

		c.add(Calendar.DATE, numberOfDays);  // number of days to add

		return c.getTime();

	}

}

