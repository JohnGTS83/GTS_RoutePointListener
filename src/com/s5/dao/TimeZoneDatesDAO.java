package com.s5.dao;

import java.sql.ResultSet;
import java.util.HashMap;

import com.s5.common.db.QueryHelper;
import com.s5.dto.TimeZoneDateDTO;


public class TimeZoneDatesDAO {

	private static HashMap<Integer, TimeZoneDateDTO> timeZoneMap = new HashMap<Integer, TimeZoneDateDTO>();

	static {
		QueryHelper qh = new QueryHelper();
		try {
			String sql = "SELECT [Tz_Ini_Date],[Tz_End_Date],[Tz_Year] FROM [gt_time_zone_dates]";
			qh.clearParams();
			ResultSet rs = qh.runQueryStreamResults(sql);
			while(rs.next()) {
				TimeZoneDateDTO timeZone = new TimeZoneDateDTO();
				timeZone.setStartDate(rs.getTimestamp("Tz_Ini_Date"));
				timeZone.setEndDate(rs.getTimestamp("Tz_End_Date"));
				timeZone.setYear(rs.getInt("Tz_Year"));
				timeZoneMap.put(rs.getInt("Tz_Year"), timeZone);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qh.releaseConnection();
		}
	}
	
	public static TimeZoneDatesDAO getInstance() {
		if(timezone == null) {
			timezone = new TimeZoneDatesDAO();
		}
		return timezone;
	}

	public HashMap<Integer, TimeZoneDateDTO> getAllTimeZoneDates() {
		return timeZoneMap;
	}
	
	public TimeZoneDateDTO getYearTimeZone(int year) {
		return timeZoneMap.get(year);
	}

	private static TimeZoneDatesDAO timezone = null;

}