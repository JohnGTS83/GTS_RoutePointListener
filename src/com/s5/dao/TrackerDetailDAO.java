package com.s5.dao;

import java.sql.ResultSet;
import java.util.HashMap;

import com.s5.common.db.QueryHelper;
import com.s5.dto.TrackerDTO;

public class TrackerDetailDAO {

	private static TrackerDetailDAO trackerTypeDao = null;

	private static HashMap<String, TrackerDTO> trackerTypeMap = new HashMap<String, TrackerDTO>();

	static {
		System.out.println("Adding trackig list");
		QueryHelper helper = new QueryHelper();
		try {
			ResultSet rs = helper.runQueryStreamResults("SELECT t.sn_imei_id, t.tracker_type_id FROM user_feature_permission uf,user_tracker_relation ut,tracker_info t where ut.user_id = uf.userid and uf.pkey = 'routeservice' and uf.pvalue = 'true' and t.sn_imei_id = ut.sn_imei_id");
			while(rs.next()) {
				TrackerDTO dto = new TrackerDTO(rs.getInt("tracker_type_id"));
				trackerTypeMap.put(rs.getString("sn_imei_id"), dto);
			}
			rs.close();
		} catch(Exception e) {
			 e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
	}
	
	private TrackerDetailDAO() {
	}
	
	public void updateList() {
		trackerTypeMap.clear();
		System.out.println("Updating trackig list");
		QueryHelper helper = new QueryHelper();
		try {
			ResultSet rs = helper.runQueryStreamResults("SELECT t.sn_imei_id, t.tracker_type_id FROM user_feature_permission uf,user_tracker_relation ut,tracker_info t where ut.user_id = uf.userid and uf.pkey = 'routeservice' and uf.pvalue = 'true' and t.sn_imei_id = ut.sn_imei_id");
			while(rs.next()) {
				TrackerDTO dto = new TrackerDTO(rs.getInt("tracker_type_id"));
				trackerTypeMap.put(rs.getString("sn_imei_id"), dto);
			}
			rs.close();
		} catch(Exception e) {
			 e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
	}
	
	public static TrackerDetailDAO getInstance() {
		if(trackerTypeDao == null) {
			trackerTypeDao = new TrackerDetailDAO();
		}
		return trackerTypeDao;
	}

	public TrackerDTO getTracker(String sn_imei_id) {
		return trackerTypeMap.get(sn_imei_id);
	}
}