package com.s5.dao;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;

import com.s5.common.db.QueryHelper;
import com.s5.dto.RouteCustomAlartDTO;

public class RouteCustomAlartDAO {

	public static ArrayList<RouteCustomAlartDTO> getAllAlert() {
		ArrayList<RouteCustomAlartDTO> alerts = new ArrayList<RouteCustomAlartDTO>();
		String sql = "select * from custom_route_alert where active = 1 ";
		QueryHelper helper = new QueryHelper();
		try {
			ResultSet rs = helper.runQueryStreamResults(sql);
			while(rs.next()) {
				if(NumberUtils.isCreatable(rs.getString("timespan"))) {
					RouteCustomAlartDTO dto = new RouteCustomAlartDTO();
					dto.setAlerttype(rs.getString("alerttype"));
					dto.setTimespan(Integer.parseInt(rs.getString("timespan")));
					dto.setEmail(rs.getString("email"));
					alerts.add(dto);
				}
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
		return alerts;
	}
}