package ac.ictwsn.core.util;

import java.util.Calendar;
import java.util.Date;

public class TimeHelper {
	
	public static Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	public static Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}
	
	public static Date timeTravel(Date now, int field, int count){
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(field, count);
		Date from = cal.getTime();
		return from;
	}
	
	public static boolean isDaytime(Date now, int sunrise, int sunset){
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		int hourOfADay = cal.get(Calendar.HOUR_OF_DAY);
		if(hourOfADay < sunrise || hourOfADay > sunset)
			return false;
		return true;
	}
}
