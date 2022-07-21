package com.s5.dao;

import com.s5.common.db.QueryHelper;

public class NotificationProcessDAO {

	public void cancelNotification(long messageid) {
		QueryHelper qh = new QueryHelper();
		try {
			qh.addParam(messageid);
			qh.runQuery("update parent_token_mapping set isActive = 0 where id = ?");
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			qh.releaseConnection();
		}
	}
}
