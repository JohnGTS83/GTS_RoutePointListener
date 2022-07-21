package com.s5.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.s5.dao.TimeZoneDatesDAO;
import com.s5.dto.TimeZoneDateDTO;


public class DateTimeUtil {

	public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

	public static String formatDateTime(Date date, int timezoneOffset,String format) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, timezoneOffset);
		
		if(timezoneOffset != 0 ) {
			int newTimezone = formatDateTimeZone(date);
			if(newTimezone<0)
				cal.add(Calendar.MINUTE, newTimezone);
		}
		return new SimpleDateFormat(format).format(cal.getTime());
	}
	
	public static String formatDateTimeWithoutTz(Date date,String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new SimpleDateFormat(format).format(cal.getTime());
	}

	public static Date parseToDefaultDateTimeWitoutTz(String dateStr,String format,int userTimezone,boolean isDST) throws ParseException  {
		Date date = null;
		Calendar cal = Calendar.getInstance();

			date = new SimpleDateFormat(format).parse(dateStr);
			cal.setTime(date);
			cal.add(Calendar.MINUTE,userTimezone);
			if(isDST) {
				int newTimezone = formatDateTimeZone(date);
				if(newTimezone<0)
					cal.add(Calendar.MINUTE, newTimezone);
			}
		return cal.getTime();
	}
	
	/*
	public static Date parseToDefaultDateTimeWitoutTz(String dateStr) {
		Date date = null;
		Calendar cal = Calendar.getInstance();
		try {
			date = new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(dateStr);
			cal.setTime(date);
			cal.add(Calendar.MINUTE, -240);
			int newTimezone = formatDateTimeZone(date);
			if(newTimezone<0)
				cal.add(Calendar.MINUTE, newTimezone);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cal.getTime();
	}
	*/
	
	public static Date parseToDateTimeWitoutTz(String dateStr,String format) {
		Date date = null;
		try {
			date = new SimpleDateFormat(format).parse(dateStr);
		} catch (Exception e) {
		}
		return date;
	}
	
	public static int formatDateTimeZone(java.util.Date date) {
		int newTimezone = 0;
		try {
			int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
	    	TimeZoneDatesDAO timezone = TimeZoneDatesDAO.getInstance();
	    	HashMap<Integer, TimeZoneDateDTO> timeZoneMap = timezone.getAllTimeZoneDates();
	    	TimeZoneDateDTO timeZone = timeZoneMap.get(year);
	    	if(timeZone != null && (date.after(timeZone.getEndDate()) || date.before(timeZone.getStartDate()))){
	    		newTimezone =  -60;
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return newTimezone;
    }
	
	public static long timeDiffInMin(Date d2,Date d1) {
		long diff = d2.getTime() - d1.getTime();//as given
		long minutes = TimeUnit.MILLISECONDS.toMinutes(diff); 
		return minutes;
	}
}
