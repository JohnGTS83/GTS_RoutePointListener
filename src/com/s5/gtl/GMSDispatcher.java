package com.s5.gtl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.s5.common.EmailUtil;
import com.s5.common.db.QueryHelper;
import com.s5.dao.PushNotification;
import com.s5.dto.AlarmEnum;
import com.s5.dto.AlertDTO;
import com.s5.dto.MessageDTO;
import com.s5.dto.ParentDTO;
import com.s5.dto.PushNotificationEmun;
import com.s5.dto.UserTrackerDTO;
import com.s5.util.DateTimeUtil;

public class GMSDispatcher implements Runnable {

	private String message;
	ExecutorService service = Executors.newFixedThreadPool(5);

	private static final SimpleDateFormat formatString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	private static final SimpleDateFormat formatDateString = new SimpleDateFormat("yyyy-MM-dd"); //2020-11-27

	public GMSDispatcher(String messageData) {
			message = messageData;
	}
	
//	26 Enter Sleep Enter Sleep
//	10 Input 2 Inactive In2 Inactive
	
//	27 Exit Sleep Exit Sleep
//	2 Input 2 Active In2 Active

	public void run() {
		try {
			String[] messageFields = message.split("\\|");
			MessageDTO messageDto = new MessageDTO();
			messageDto.setSn_imei_id(messageFields[0]);
			messageDto.setLatitude(Double.parseDouble(messageFields[1]));
			messageDto.setLongitude(Double.parseDouble(messageFields[2]));
			messageDto.setAlarm_id(Integer.parseInt(messageFields[4]));
			messageDto.setL_datetime(messageFields[5]); //yyyy-MM-dd HH:mm:ss -  2020-10-06 11:53:44
			UserTrackerDTO dtoUserTracker = getUserTracker(messageDto.getSn_imei_id());
			if(dtoUserTracker != null) {
				messageDto.setlDate(DateTimeUtil.parseToDefaultDateTimeWitoutTz(messageFields[5],"yyyy-MM-dd HH:mm:ss",dtoUserTracker.getTimeZoneOffSet(),dtoUserTracker.isDayLight()));	
				int checkInCheckOut = isCheckin(messageDto.getAlarm_id(),dtoUserTracker.getTracker_type_id());
				if(checkInCheckOut != -1 ) {
//					System.out.println("CheckIn: "+ message);
					processCheckIn(messageDto,checkInCheckOut);
				} else {
					if(isStop(messageDto.getAlarm_id(),dtoUserTracker.getTracker_type_id())) {
//						System.out.println("Stop: "+ message);
						insertArrivalTimeNew(messageDto,dtoUserTracker);
					}
				}
				
//			} else {
//				System.out.println("Not Route Tracker: "+ message);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public UserTrackerDTO getUserTracker(String imei) {
		UserTrackerDTO dtoUserTracker = null;
		QueryHelper helper = new QueryHelper();
		try {
			helper.addParam(imei);
			ResultSet rs = helper.runQueryStreamResults("select ut.user_id,ut.tracker_type_id,ut.timezone_offset,ut.isDayLight  from user_feature_permission uf  WITH(NOLOCK), user_tracker_view ut WITH(NOLOCK) where  uf.userid = ut.user_id   and uf.pvalue = 'true' and  uf.pkey = 'routeservice' and ut.sn_imei_id = ?");
			if(rs.next()) {
				dtoUserTracker = new UserTrackerDTO(rs.getInt("tracker_type_id"));
				dtoUserTracker.setUserID(rs.getInt("user_id"));
				dtoUserTracker.setTimeZoneOffSet(rs.getInt("timezone_offset"));
				dtoUserTracker.setDayLight(rs.getBoolean("isDayLight"));
			}
			rs.close();
		} catch(Exception e) {
			 e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
		return dtoUserTracker;
	}

	public int getTrackerTypeIfRouteUser(String imei) {
		QueryHelper helper = new QueryHelper();
		int typeid = 0;
		try {
			helper.addParam(imei);
			ResultSet rs = helper.runQueryStreamResults("SELECT t.tracker_type_id FROM user_feature_permission uf,user_tracker_relation ut,tracker_info t where ut.user_id = uf.userid and uf.pkey = 'routeservice' and uf.pvalue = 'true' and t.sn_imei_id = ut.sn_imei_id and t.sn_imei_id = ?");
			if(rs.next()) {
				typeid = rs.getInt("tracker_type_id");	
			}
			rs.close();
		} catch(Exception e) {
			 e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
		return typeid;
	}
	
	public boolean isStop(int alarmId,int trackerTypeid) {
		if(trackerTypeid == AlarmEnum.GMS50_Device_Tracker_ID && (ArrayUtils.contains(AlarmEnum.FETCH_GMS50_ALARM_ENUM, alarmId))) { //GMS50
			return true;
		} else if(ArrayUtils.contains(AlarmEnum.FETCH_ALARM_ENUM, alarmId)) { //T366/T3/T333
			return true;
		} else  if(ArrayUtils.contains(AlarmEnum.FETCH_T399_ALARM_ENUM, alarmId)) { //T399 - 70
			return true;
		} else {
			return false;
		}
	}
	
	public int isCheckin(int alarmId,int trackerTypeid) {
		int checkInCheckOut = -1;
		if(alarmId == 26 || alarmId == 10) { //Checkpout
			checkInCheckOut = 0;
		} else if(alarmId == 27 || alarmId == 2) { //CheckIn
			checkInCheckOut = 1;
		}
		return checkInCheckOut;
	}
	
	public Date getFormatedTime(String data) {
		SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
		SimpleDateFormat formatterOther = new SimpleDateFormat("HH:mm");
		 try {
			 if((data.contains("AM") || data.contains("PM")) && data.length()>5)
				 return formatter.parse(data);
			 else {
				 return formatterOther.parse(data);
			 }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 return null;
	}
	
	public void processCheckIn(MessageDTO messageDto,int is_check_inInt) {
		QueryHelper helper = new QueryHelper();
		try {
			Calendar cal = Calendar.getInstance();
			String day = "X";
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case 2:
				day = "M";
				break;
			case 3:
				day = "T";
				break;
			case 4:
				day = "W";
				break;
			case 5:
				day = "R";
				break;
			case 6:
				day = "F";
				break;
			default:
				break;
			}

			if(!day.equalsIgnoreCase("X")) {
				String sql = "SELECT rtr.d_driver_id,rtr.routeid AS route_fk_id,rtr.sn_imei_id AS VehicleID,tirp.id,tirp.RunId,tirp.Description,tirp.RouteID,tirp.StartTime,tirp.EndTime, tirp.Type "
						 	+"FROM route_tracker_relation rtr WITH(NOLOCK) "
						 	+"INNER JOIN tdsb_i_run_points tirp WITH(NOLOCK) ON tirp.route_fk_id = rtr.routeid AND tirp.ComponentVariation LIKE '%"+day+"%' "
						 	+"INNER JOIN run_assignment_mapping ar on rtr.id = ar.assignment_id  and ((ar.run_type = '10' and tirp.id = ar.segment_id ) or (ar.run_type = tirp.Type )) "
						 	+"WHERE CONVERT(DATE, rtr.start_date) <= CONVERT(DATE, GETDATE()) AND (CONVERT(DATE, rtr.end_date) >= CONVERT(DATE, GETDATE()) OR rtr.end_date IS NULL) "
						 	+"AND rtr.is_active = 1 AND tirp.Active = 1  AND rtr.sn_imei_id = ?";

				helper.addParam(messageDto.getSn_imei_id());
				ResultSet rsRoutes = helper.runQueryStreamResults(sql);
				long diff = 0;
				ArrayList<AlertDTO> routeList = new ArrayList<AlertDTO>();
				while(rsRoutes.next()) {
					AlertDTO dto = new AlertDTO();
					dto.setId(rsRoutes.getInt("id"));
					dto.setRouteName(rsRoutes.getString("RouteID"));
					dto.setRunName(rsRoutes.getString("RunId") + " [" + rsRoutes.getString("Description")  +"]");
					dto.setUnitName(rsRoutes.getString("VehicleID"));
					dto.setStarTime(rsRoutes.getString("StartTime"));
					dto.setEndTime(rsRoutes.getString("EndTime"));
					dto.setRouteFKId(rsRoutes.getInt("route_fk_id"));
					dto.setD_driver_id(rsRoutes.getInt("d_driver_id"));
					dto.setType(rsRoutes.getInt("Type"));
					routeList.add(dto);
				}
				rsRoutes.close();
				
				if(!routeList.isEmpty()) {
					Calendar cal1 = Calendar.getInstance();

					for (AlertDTO dto : routeList) {
						try {
							Date dtSchedule  = null;
							if(is_check_inInt == 1) {
								dtSchedule  = getFormatedTime(dto.getStarTime());
							} else {
								dtSchedule  =  getFormatedTime(dto.getEndTime());
							}
							cal1.setTime(dtSchedule);
							cal1.set(Calendar.YEAR, cal.get(Calendar.YEAR));
							cal1.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
							diff = DateTimeUtil.timeDiffInMin(cal1.getTime(),messageDto.getlDate());
							
							if(diff>=-60 && diff<=60) {
								String sql1 = "SELECT checkid FROM driver_checks WITH(NOLOCK) WHERE sn_imei_id = '"+messageDto.getSn_imei_id()+"' AND run_id = '"+ dto.getId() +"' AND ((CAST(check_time AS DATE) = CAST(GETDATE() AS DATE)) OR (CAST(m_check_time AS DATE) = CAST(GETDATE() AS DATE)) or (CAST(check_out_time AS DATE) = CAST(GETDATE() AS DATE )))";
								helper.clearParams();
								ResultSet rs1 = helper.runQueryStreamResults(sql1);
//								boolean is_check_in = true;
								int checkInId = 0;
								if(rs1.next()) {
//									is_check_in = false;
									checkInId = rs1.getInt("checkid");
								}
								rs1.close();
								
//								CheckIn: 861836055839273|50.333425|-102.265963|120388154|10|2021-09-08 12:28:26
//								CheckIn: 861836055839273|50.33342|-102.26596099999999|120388161|2|2021-09-08 12:28:27
//								Update TimeDiff: 59, 861836055839273, 28292, rpid: 7349 : 59 Wed Sep 08 07:28:00 EDT 2021 2021-09-08 12:28:27
//								Update TimeDiff: 18, 861412043053052, 28441, rpid: 7401 : 18 Wed Sep 08 07:28:00 EDT 2021 2021-09-08 13:09:23 : 2021-09-08 07:09:23.0 : Wed Sep 08 07:09:23 EDT 2021
								if(checkInId == 0) {
//									Date now = new Date();
									sql = "INSERT INTO driver_checks (sn_imei_id,driver_id,check_time,key_id,is_check_in,route_id,is_valid_checkin,run_id,latitude,longitude) VALUES (?,?,?,?,?,?,1,?,?,?)";
									helper.clearParams();
									helper.addParam(messageDto.getSn_imei_id());
									helper.addParam(dto.getD_driver_id());
//									helper.addParam(new java.sql.Timestamp(now.getTime()));
									helper.addParam(formatString.format(messageDto.getlDate()));
									helper.addParam("");
									helper.addParam(is_check_inInt);
									helper.addParam(dto.getRouteFKId());
									helper.addParam(dto.getId());
									helper.addParam(messageDto.getLatitude());
									helper.addParam(messageDto.getLongitude());
									helper.runQuery(sql);
									newNotificationThread(dto.getId(), messageDto.getlDate(),messageDto.getSn_imei_id(),dto.getType());
								} else {
									//2021-09-08 09:08:31.237
//									System.out.println("Update TimeDiff: "+diff + ", " + messageDto.getSn_imei_id() + ", "+dto.getId()+", rpid: "+dto.getRouteFKId() + " : "+ diff + " " + cal1.getTime() + " " + messageDto.getL_datetime()+ " : " + formatString.format(messageDto.getlDate()) + " : " + messageDto.getlDate());
									//Update TimeDiff: 27, 861412043013593, 28259, rpid: 7309 : 27 Wed Sep 08 07:35:00 EDT 2021 2021-09-08 13:07:32 : 2021-09-08 07:07:32.0 : Wed Sep 08 07:07:32 EDT 2021
//									Date now = new Date();
									if(is_check_inInt == 0) {
										sql = "update driver_checks set check_out_time = ? where checkid = ? ";
									} else {
										sql = "update driver_checks set check_time = ? where checkid = ? ";
									}
									helper.clearParams();
//									helper.addParam(new java.sql.Timestamp(now.getTime()));
									helper.addParam(formatString.format(messageDto.getlDate()));
									helper.addParam(checkInId);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch(Exception e) {
			 e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
	}

	public void insertArrivalTimeNew(MessageDTO messageDto,UserTrackerDTO dtoUserTracker){
		Calendar cal = Calendar.getInstance();
		String day = "X";
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 2:
			day = "M";
			break;
		case 3:
			day = "T";
			break;
		case 4:
			day = "W";
			break;
		case 5:
			day = "R";
			break;
		case 6:
			day = "F";
			break;
		default:
			break;
		}
//		String imei = "KN006368";
//		13	44.073046646326	-80.1758895204039
		if(!day.equalsIgnoreCase("X")) {
			QueryHelper helper = new QueryHelper();
			try {
				String sql = " SELECT r.id,r.RunId,r.Description,r.RouteID,r.Type,r.route_fk_id,rtr.ampm_type,rtr.sn_imei_id AS VehicleID "+
					  " FROM tdsb_i_run_points r WITH(NOLOCK) "+
					  " INNER JOIN tdsb_i_route rr WITH(NOLOCK) ON rr.id = r.route_fk_id "+
					  " INNER JOIN route_tracker_relation rtr WITH(NOLOCK) ON rtr.routeid = rr.id "+
					  " INNER JOIN run_assignment_mapping ar on rtr.id = ar.assignment_id  and ((ar.run_type = '10' and r.id = ar.segment_id ) or (ar.run_type = r.Type )) "+
					  " WHERE r.Active = 1 AND r.ComponentVariation LIKE '%"+day+ "%' AND rtr.sn_imei_id = ?  AND rr.route_key = r.routeGuid AND rtr.is_active = 1 "+
					  " AND (rtr.start_date <= GETDATE()) AND (rtr.end_date IS NULL OR rtr.end_date >= GETDATE()) ";
				
//				String sql = "select run.id,run.RunId,run.Description,run.RouteID,rv.VehicleID from tdsb_i_run_points run WITH(NOLOCK),tdsb_i_GPSRouteVehicle rv WITH(NOLOCK) where run.Active = 1 and rv.VehicleId = ? and rv.RetireDate >= getDate() and "
//						+"rv.EffectiveDate <= getDate() and rv.RouteGuid = run.routeGuid and ComponentVariation like  '%"+day+"%'";	
				helper.addParam(messageDto.getSn_imei_id());
				ResultSet rs = helper.runQueryStreamResults(sql);
				ArrayList<AlertDTO> rid = new ArrayList<AlertDTO>();
				while(rs.next()) {
					AlertDTO alertDTO = new AlertDTO();
					alertDTO.setId(rs.getInt("id"));
					alertDTO.setRouteFKId(rs.getInt("route_fk_id"));
					alertDTO.setRouteName(rs.getString("RouteID"));
					alertDTO.setRunName(rs.getString("RunId") + " [" + rs.getString("Description")  +"]");
					alertDTO.setUnitName(rs.getString("VehicleID"));
					rid.add(alertDTO);	
				}
				rs.close();
				
				if(!rid.isEmpty()) {
					ResultSet rs1 = null;
					Calendar cal1 = Calendar.getInstance();
					long diff = 0;
					
					for (AlertDTO dto : rid) {
						sql = "SELECT busStopIdForParent,runGuid,id, distanceMet,StartTime,OrderNumber,StopType,Description,BusStopId from (select " +
								"((ACOS(SIN(PI()*latitude/180.0)*SIN(PI()* ? /180.0)+COS(PI()*latitude/180.0)*COS(PI()* ? /180.0)*COS(PI()* ? /180.0-PI()*longitude/180.0))*6371)*1000) AS distanceMet, " +
								"p.id,p.BusStopId,tbp.StartTime,tbp.OrderNumber,tbp.StopType,tbp.Description,tbp.id As busStopIdForParent,tbp.runGuid " +
									"from  tdsb_i_runpoints_position p WITH(NOLOCK),tdsb_i_BusStopBasic tbp WITH(NOLOCK) " +
								"where p.route_point_id = ? and tbp.busStopGuid = p.busStopGuid and p.active = 1 ) as temp " +
								"where distanceMet < 150 "; 
						
							helper.clearParams(); 
							helper.addParam(messageDto.getLatitude());
							helper.addParam(messageDto.getLatitude());
							helper.addParam(messageDto.getLongitude());
							helper.addParam(dto.getId());
			
							ResultSet rsPoints = helper.runQueryStreamResults(sql);
							int arrId = 0 ;
							while(rsPoints.next()) {
								int rpid = rsPoints.getInt("id");
								int busStopIdForParent = rsPoints.getInt("busStopIdForParent");
								dto.setStopType(StringUtils.trimToEmpty(rsPoints.getString("StopType")));
								dto.setStopNumber(rsPoints.getInt("OrderNumber"));
								dto.setStopDesc(rsPoints.getString("Description"));
								dto.setStopId(rsPoints.getString("BusStopId"));
//								double distanceMet = rsPoints.getDouble("distanceMet");
								cal1.setTime(rsPoints.getTimestamp("StartTime"));
								cal1.set(Calendar.YEAR, cal.get(Calendar.YEAR));
								cal1.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
								diff = DateTimeUtil.timeDiffInMin(cal1.getTime(),messageDto.getlDate());
//								System.out.println("distanceMet: "+distanceMet + ", " + messageDto.getSn_imei_id()+ ", "+dto.getId()+", rpid: "+rpid + " : "+ diff + messageDto.getlDate());
								if(diff>=-20 && diff<=20) {
									
									helper.clearParams();
									helper.addParam(dto.getId());
									helper.addParam(rpid);
									helper.addParam(messageDto.getSn_imei_id());
//									if(StringUtils.isNotBlank(messageDto.getL_datetime())) {
										sql = "SELECT id FROM tdsb_i_run_arrival WITH(NOLOCK)";
										sql += " WHERE route_id = ? AND rpid = ? AND sn_imei_id = ?";
										sql += " AND CONVERT(CHAR(10), arrival_time , 120) =  ? ";
										helper.addParam(formatDateString.format(messageDto.getlDate()));
//									} else {
//										sql = "SELECT id FROM tdsb_i_run_arrival WITH(NOLOCK)";
//										sql += " WHERE route_id = ? AND rpid = ? AND sn_imei_id = ?";
//										sql += " AND CONVERT(CHAR(10), arrival_time , 120) = CONVERT(CHAR(10), getDate() , 120)";
//									}
									rs1 = helper.runQueryStreamResults(sql);
									if(rs1.next()) {
										arrId = rs1.getInt("id");
									} else {
										arrId = 0;
									}
									rs1.close();
//									System.out.println("Adding/Updating: " + arrId);
									if(arrId == 0) {
										sql = "INSERT INTO tdsb_i_run_arrival (route_id, rpid, sn_imei_id, arrival_time,point_order,BusStopId,departure_time,routeId,busStopType) VALUES(?,?,?,?,?,?,?,?,?)";
										helper.clearParams();
										helper.addParam(dto.getId());
										helper.addParam(rpid);
										helper.addParam(messageDto.getSn_imei_id());
										helper.addParam(formatString.format(messageDto.getlDate()));
										helper.addParam(dto.getStopNumber());
										helper.addParam(dto.getStopId());
										helper.addParam(formatString.format(Date.from(messageDto.getlDate().toInstant().plusSeconds(5))));
										helper.addParam(dto.getRouteFKId());
										helper.addParam(getStopType(dto.getStopType()));
										helper.runQuery(sql);
										dto.setLateMinuts(diff);
										processRouteNotification(messageDto,dto);
										processParentNotification(dto.getId(),busStopIdForParent,getStopType(dto.getStopType())+"",0);

//											System.out.println("Push notification sent.");
//											getNextStopIdByBusStopId(dto.getId(),dto.getStopType(),dto.getStopNumber(),StringUtils.trimToEmpty(rsPoints.getString("runGuid")));
									} else {
										sql = "UPDATE tdsb_i_run_arrival SET departure_time = ? WHERE id = ?";
										helper.clearParams();
										helper.addParam(formatString.format(messageDto.getlDate()));
										helper.addParam(arrId);
										helper.runQuery(sql);
									}
//							} else {
//								System.out.println("Not In Time");
							}
						}
						rsPoints.close();					
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				helper.releaseConnection();
			}
		}
	}
	private int getStopType(String stop) {
		int newStop=4;
		
		if(stop.equalsIgnoreCase("1")) {
			newStop =1;
		}
		

		if(stop.equalsIgnoreCase("2")) {
			newStop =2;
		}
		

		if(stop.equalsIgnoreCase("3")) {
			newStop =3;
		}
		

		if(stop.equalsIgnoreCase("0")) {
			newStop =0;
		}
		
		if(stop.equalsIgnoreCase("PICKUP")) {
			newStop =0;
		}
		
		if(stop.equalsIgnoreCase("DROPOFF")) {
			newStop =1;
		}
		
		if(stop.equalsIgnoreCase("SCHOOL_PICKUP")) {
			newStop =2;
		}
		
		if(stop.equalsIgnoreCase("SCHOOL_DROPOFF")) {
			newStop =3;
		}
		
		return newStop;
	}
	public static String getRunFirstStartTime(int rid){
		QueryHelper helper = new QueryHelper();
		Calendar cal = Calendar.getInstance();
		String dt = null;
		try{
			String sql = "SELECT  r.StartTime,r.id as runs_id, rr.route_name,rr.localroute  " + 
					"						 FROM tdsb_i_run_points r WITH(NOLOCK)  " + 
					"						 INNER JOIN tdsb_i_route rr WITH(NOLOCK) ON rr.id = r.route_fk_id  " + 
					"						 INNER JOIN route_tracker_relation rtr WITH(NOLOCK) ON rtr.routeid = rr.id " + 
					"						 INNER JOIN run_assignment_mapping ar on rtr.id = ar.assignment_id  and ((ar.run_type = '10' and r.id = ar.segment_id ) or (ar.run_type = r.Type ))  " + 
					"						 WHERE rtr.is_active = 1  AND  r.Active = 1 " + 
					"						 AND (rtr.start_date <= GETDATE()) AND (rtr.end_date IS NULL OR rtr.end_date >= GETDATE()) AND r.id = ? ";
			helper.addParam(rid);
			ResultSet rs = helper.runQueryStreamResults(sql);
			while(rs.next()){
				if(rs.getString("StartTime")!=null) {
					if(rs.getInt("runs_id")==rid) {
						dt=rs.getString("StartTime");					
					}
				}
			}
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
		return dt;
	}
	
	public static boolean processParentNotification(int runid,int busStopID, String getStopType, int notiType) {
		boolean done = false;
		QueryHelper qh = new QueryHelper();
		try {
			String sql="";
		if(notiType==999 || getStopType.equalsIgnoreCase("2") || getStopType.equalsIgnoreCase("3")) {
			sql = "SELECT m.id as tokenid,m.tokenKey,m.devicePlatform,pr.BusStopID,pr.id FROM student_parent_relation pr WITH(NOLOCK) INNER JOIN parent_token_mapping m WITH(NOLOCK) ON pr.parent_id = m.parentId AND m.isActive = 1 WHERE pr.run_id = ?";
				qh.addParam(runid);
				qh.setTimeoutInSec(2);
				
		} else {
			sql = "SELECT m.id as tokenid,m.tokenKey,m.devicePlatform,pr.BusStopID,pr.id FROM student_parent_relation pr WITH(NOLOCK) INNER JOIN parent_token_mapping m WITH(NOLOCK) ON pr.parent_id = m.parentId AND m.isActive = 1 WHERE pr.run_id = ? and pr.BusStopID = ?";
				qh.addParam(runid);
				qh.addParam(busStopID);
				qh.setTimeoutInSec(2);
			
		}
			ResultSet rs = qh.runQueryStreamResults(sql);

			ArrayList<ParentDTO> list = new ArrayList<ParentDTO>(); 
			ArrayList<String> checkList = new ArrayList<>();
			while (rs.next()) {
				String token = StringUtils.trimToEmpty(rs.getString("tokenKey"));
				if(checkList.indexOf(token)==-1) {
					ParentDTO parent = new ParentDTO();
					parent.setTokenid(rs.getLong("tokenid")); 
					parent.setParentId(rs.getInt("id"));
					parent.setPlatform(StringUtils.trimToEmpty(rs.getString("devicePlatform")));
					parent.setTokenKey(StringUtils.trimToEmpty(rs.getString("tokenKey")));
					parent.setStopId(rs.getInt("BusStopID"));
					list.add(parent);
					checkList.add(token);
				}
			}
			rs.close();

			int getExactDetails = 0;
			for (ParentDTO parentDTO : list) {
				if(busStopID == parentDTO.getStopId()) {
					//On stop notification
					if(getStopType.equalsIgnoreCase("0")) {
						getExactDetails = PushNotificationEmun.BUSREACHEDATYOURSTOPFORPICKUP;
					}else if(getStopType.equalsIgnoreCase("1")) {
						getExactDetails = PushNotificationEmun.BUSREACHEDATYOURSTOPFORPICKUP;
					}
				} else if(getStopType.equalsIgnoreCase("2")) {
					getExactDetails = PushNotificationEmun.BUSRREADYFORDROP;
				} else if(getStopType.equalsIgnoreCase("3")) {
					getExactDetails = PushNotificationEmun.BUSREACHEDATSCHOOL;
				} else if(getStopType.equalsIgnoreCase("4")) {
					getExactDetails = PushNotificationEmun.BUSREACHEDATFIRSTSTOP;
				} else if(getStopType.equalsIgnoreCase("5")) {
					getExactDetails = PushNotificationEmun.BUSRREADYFORDROPAFTER;
				} else {
					System.out.println("RunID "+runid+"   BusId  "+ busStopID+" StopType "+getStopType+" Noti "+notiType);
					/*
					sql = "SELECT TOP 1 b.id FROM tdsb_i_BusStopBasic b WITH(NOLOCK) "
							+ "INNER JOIN tdsb_i_runpoints_position rp WITH(NOLOCK) ON b.busStopGuid = rp.busStopGuid "
							+ "WHERE runGuid = ? and CAST(EfectiveDate AS DATE) <=  CAST(DATEADD(DAY, 30, GETDATE()) AS DATE) "
							+ "  and (CAST(RetireDate AS DATE) >=  CAST(GETDATE() AS DATE)  or RetireDate = '1900-01-01 00:00:00.000') AND b.Active = 1 "
							+ "  AND rp.OrderNumber > ? "
							+ "order by rp.OrderNumber,StartTime";
					*/
				}
				if(getExactDetails != 0) {
					if(StringUtils.isNotEmpty(parentDTO.getTokenKey())) {
						done = true;
						System.out.println(parentDTO.toString());
						if(parentDTO.getPlatform().equals("ios"))
							PushNotification.sendNotificationToSmartphone(parentDTO.getTokenKey(),0,getExactDetails,parentDTO.getTokenid());
						else
							PushNotification.pushFCMNotification(parentDTO.getTokenKey(),getExactDetails,parentDTO.getTokenid(),busStopID);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			qh.releaseConnection();
		}
		return done;
	}
	private void processRouteNotification(MessageDTO messageDto,AlertDTO dto) {
		String alarmtype = "lateall";
		if(dto.getStopNumber() == 0) {
			alarmtype = "latefirst";
		} else if(dto.getStopType().equalsIgnoreCase("SCHOOL_PICKUP") || dto.getStopType().equalsIgnoreCase("SCHOOL_DROPOFF")) {
			alarmtype = "latesch";
		}
//		SCHOOL_PICKUP
//		SCHOOL_DROPOFF
//		PICKUP
//		DROPOFF

		QueryHelper helperNotification = new QueryHelper();
		try {
			helperNotification.addParam(messageDto.getSn_imei_id());
			helperNotification.addParam(alarmtype);
			String SELECT_ALERT_SQL = "select c.*,u.tracker_name,ISNULL(di.driver_cellphone,'-') as cell,   ISNULL(di.driver_name,'-') AS driver_name "
					+" from custom_route_alert c WITH(NOLOCK),user_tracker_view u  left join driver_tracker_relation dtr WITH(NOLOCK) on dtr.sn_imei_id = u.sn_imei_id COLLATE DATABASE_DEFAULT LEFT JOIN [driver_info] di ON di.driver_id = dtr.driver_id "
					+" where c.active = 1 and u.sn_imei_id = ? and c.userid = u.user_id and c.alerttype = ? ";

			
			ResultSet rsCN = helperNotification.runQueryStreamResults(SELECT_ALERT_SQL);
			if(rsCN.next()) {
				if(NumberUtils.isParsable(rsCN.getString("timespan"))) {
					int duration = Integer.parseInt(rsCN.getString("timespan"));
					if(dto.getLateMinuts() <= (duration*-1)) {
						String body = "<br/>BUS# : <b>" + rsCN.getString("tracker_name");
						body += "</b><br/>Route: " + dto.getRouteName();
						body += "<br/>Driver: " + rsCN.getString("driver_name");
						body += "<br/>Cell Phone:  " + rsCN.getString("cell");
						body += "<br/>Run: " + dto.getRunName();
						body += "<br/>Late by(min.) : " + dto.getLateMinuts();
						body += "<br/>Stop: [" + dto.getStopNumber() +"] "+ dto.getStopDesc();
						body += "<br/>Stop Type : " + dto.getStopType();
						
						body += "<br/>Location : <a href='https://www.google.com/maps/search/?api=1&query=" + messageDto.getLatitude() +","+messageDto.getLongitude() +"'>" + dto.getStopDesc() + "</a>";
						String subject = dto.getRouteName() + " Route Alert for " + rsCN.getString("tracker_name");
						EmailUtil.sendEmail(subject, body, StringUtils.trimToEmpty(rsCN.getString("email")));

						QueryHelper alert_helper = new QueryHelper();
						try {
							String sql_alert = "INSERT INTO late_trip_data (run_point_id, tracker_name, RouteID, driver_name,sn_imei_id,arrival_time,late_minuts,bus_stop)"
									+ "  VALUES(?,?,?,?,?,getDate(),?,?)";
							alert_helper.clearParams();
							alert_helper.addParam(dto.getId());
							alert_helper.addParam(rsCN.getString("tracker_name"));
							alert_helper.addParam(dto.getRouteName());
							alert_helper.addParam(rsCN.getString("driver_name"));
							alert_helper.addParam(messageDto.getSn_imei_id());
							alert_helper.addParam(dto.getLateMinuts());
							alert_helper.addParam(dto.getStopDesc());
							alert_helper.runQuery(sql_alert);
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							alert_helper.releaseConnection();
						}

//						if(EmailUtil.sendReportMail(subject, body, "aawte.umesh@avibha.com"))
//							System.out.println("Email Sent: " + subject);
					}
				}
			}
			rsCN.close();
//			tmam
//			DONE - latefirst
//			DONE - lateall
//			DONE - latesch
//			tmnoon
//			tmpm
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			helperNotification.releaseConnection();
		}
	}
	private void newNotificationThread(int runID,Date currenttime,String sn_imei_id,int isCheckIn) {
		QueryHelper helper = new QueryHelper();
		try {
			helper.addParam(runID);
			helper.setTimeoutInSec(2);
			ResultSet rs = helper.runQueryStreamResults("SELECT m.id as tokenid,m.tokenKey,m.devicePlatform,pr.BusStopID,pr.id FROM student_parent_relation pr WITH(NOLOCK) INNER JOIN parent_token_mapping m WITH(NOLOCK) ON pr.parent_id = m.parentId AND m.isActive = 1 WHERE pr.run_id = ?");
			ArrayList<ParentDTO> list = new ArrayList<ParentDTO>(); 
			ArrayList<String> checkList = new ArrayList<>();
			while (rs.next()) {
				String token = StringUtils.trimToEmpty(rs.getString("tokenKey"));
				if(checkList.indexOf(token)==-1) {
					ParentDTO parent = new ParentDTO();
					parent.setTokenid(rs.getLong("tokenid")); 
					parent.setParentId(rs.getInt("id"));
					parent.setPlatform(StringUtils.trimToEmpty(rs.getString("devicePlatform")));
					parent.setTokenKey(StringUtils.trimToEmpty(rs.getString("tokenKey")));
					parent.setStopId(rs.getInt("BusStopID"));
					list.add(parent);
					checkList.add(token);
				}
			}
			rs.close();
			if(!checkList.isEmpty()) {
				Calendar cal = Calendar.getInstance();
				Calendar cal1 = Calendar.getInstance();
				Date dtSchedule  = null;
				dtSchedule  = getFormatedTime(getRunFirstStartTime(runID));
				cal.setTime(dtSchedule);
				cal.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
				cal.set(Calendar.DAY_OF_YEAR, cal1.get(Calendar.DAY_OF_YEAR));
				
				long diff = DateTimeUtil.timeDiffInMin(cal.getTime(),currenttime);
				final long scheduleMinute = diff - 15;
				if(scheduleMinute>1 && isCheckIn==0) {
					 
					Runnable notifyCheckInRunnable = new Runnable() {
					      public void run(){
					    	  try {
						    	  System.out.println(runID +"CheckIn Thread is Running.....");
						          TimerTask task =new TimerTask() {
									@Override
									public void run() {
										System.out.println(runID+"CheckIn Thread is called...."+scheduleMinute);
										QueryHelper qh = new QueryHelper();
										ResultSet rs2 = null;
										try {
											String arrIdd ="0";
											qh.clearParams();
											qh.addParam(1);
											qh.addParam(sn_imei_id);
										     String sql = "SELECT sn_imei_id FROM tdsb_i_run_initial WITH(NOLOCK)";
												sql += " WHERE morning = ? AND sn_imei_id = ?";
												sql += " AND CONVERT(CHAR(10), arrival_time , 120) =  ? ";
												qh.addParam(formatDateString.format(currenttime));

											rs2 = qh.runQueryStreamResults(sql);
											if(rs2.next()) {
												arrIdd = rs2.getString("sn_imei_id");
											} else {
												arrIdd = "0";
											}
											rs2.close();
											if(arrIdd=="0") {
												sql = "SELECT  gi.ignitionstatus, ((ACOS(SIN(PI()*dc.latitude/180.0)*SIN(PI()* gi.latitude /180.0)+COS(PI()*dc.latitude/180.0)*COS(PI()* gi.latitude /180.0)*COS(PI()* gi.longitude /180.0-PI()*dc.longitude/180.0))*6371)*1000) AS distanceMet " + 
														" FROM driver_checks dc WITH(NOLOCK) INNER JOIN gps_info_last gi WITH(NOLOCK) ON gi.sn_imei_id = dc.sn_imei_id COLLATE database_default where dc.sn_imei_id= ? and CONVERT(CHAR(10), dc.check_time , 120)= ? and dc.run_id = ? "; 
												
												qh.clearParams(); 
												qh.addParam(sn_imei_id);
												qh.addParam(formatDateString.format(currenttime));
												qh.addParam(runID);
													ResultSet rsPoints1 = qh.runQueryStreamResults(sql);
													while(rsPoints1.next()) {
														int ignitionStatus = rsPoints1.getInt("ignitionstatus");
														System.out.println("ignitionStatus on Thread is "+ignitionStatus+"distance  "+rsPoints1.getDouble("distanceMet"));
														if(ignitionStatus==144 || ignitionStatus==4) {
															if(rsPoints1.getDouble("distanceMet") > 250.0) {
																System.out.println("Notification Sent");
																sql = "INSERT INTO tdsb_i_run_initial (route_id, sn_imei_id, arrival_time,morning,after_school,departure_time,routeId) VALUES(?,?,?,?,?,?,?)";
																helper.clearParams();
																helper.addParam(runID);
																helper.addParam(sn_imei_id);
																helper.addParam(formatString.format(currenttime));
																helper.addParam(1);
																helper.addParam(0);
																helper.addParam(formatString.format(Date.from(currenttime.toInstant().plusSeconds(5))));
																helper.addParam(runID);
																helper.runQuery(sql);
																System.out.println("Notification Sent");
																processParentNotification(runID,999,"4",999);
																this.cancel();
															}
														}
													}
													rsPoints1.close();
																								
											}
										} catch(Exception e) {
											 e.printStackTrace();
										} finally {
											qh.releaseConnection();
										}
									}
								  };
								  new Timer().schedule(task, scheduleMinute*60*1000);
					          } catch (Exception e) {
					              e.printStackTrace();
					              System.out.println("CheckIn Thread isInterrepted");
					          }
					      }
					};
					service.submit(notifyCheckInRunnable);		
					
				}else if(diff > 0) {
					Runnable notifyOutRunnable = new Runnable() {
					      public void run(){
					    	 
					    	  try {
					    		  TimerTask task = new TimerTask() {

									@Override
									public void run() {
										
										System.out.println("CheckOut Thread called "+cal.getTime());
										QueryHelper qh = new QueryHelper();
										try {
											ResultSet rs3 = null;
											ResultSet rs5 = null;
											String arrIddd="0";
											Timestamp currentDate = null;
											qh.clearParams();
											qh.addParam(1);
											qh.addParam(sn_imei_id);
												String sql = "SELECT sn_imei_id  FROM tdsb_i_run_initial WITH(NOLOCK)";
												sql += " WHERE after_school = ? AND sn_imei_id = ?";
												sql += " AND CONVERT(CHAR(10), arrival_time , 120) =  ? ";
												qh.addParam(formatDateString.format(currenttime));

											rs3 = qh.runQueryStreamResults(sql);
											if(rs3.next()) {
												arrIddd = rs3.getString("sn_imei_id");
											} else {
												arrIddd = "0";
											}
											rs3.close();
											sql = "SELECT GETDATE() as date ";
											rs5 = qh.runQueryStreamResults(sql);
											if(rs5.next()) {
												currentDate = rs5.getTimestamp("date");
											}
											rs5.close();
											Calendar cal2 = Calendar.getInstance();
											cal2.setTime(currentDate);
											cal2.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
											cal2.set(Calendar.DAY_OF_YEAR, cal1.get(Calendar.DAY_OF_YEAR));
											
											
											long newDiff = DateTimeUtil.timeDiffInMin(cal.getTime(),cal2.getTime());
											newDiff+=60;
											System.out.println(cal2.getTime()+" current date & first date"+cal.getTime() + "Diff "+newDiff);
											if(newDiff>720 || newDiff<-720) {
												this.cancel();
												System.out.println("Looks like Time is already gone");
											}
											
											if(arrIddd == "0") {
												sql = "SELECT * FROM tdsb_i_run_arrival t INNER JOIN gps_info_last gi ON t.sn_imei_id = gi.sn_imei_id COLLATE database_default WHERE t.sn_imei_id= ? AND t.busStopType=2 AND CONVERT(CHAR(10), t.arrival_time , 120) = ?"; 
												
												qh.clearParams(); 
												qh.addParam(sn_imei_id);
												qh.addParam(formatDateString.format(currenttime));
												ResultSet rsPoints2 = qh.runQueryStreamResults(sql);
												String busStopid="0";
												int ignitionStatus=0;
												
												if(rsPoints2.next()) {
													 busStopid = rsPoints2.getString("BusStopId");
													 ignitionStatus = rsPoints2.getInt("ignitionstatus");
													 
												}else {
													busStopid="0";
												}

											
												System.out.println("is Pick Up "+busStopid +"ignition "+ignitionStatus);
												rsPoints2.close();
												if(ignitionStatus==144 |ignitionStatus==4) {

													if(busStopid!="0") {
														sql = "SELECT ((ACOS(SIN(PI()*trp.latitude/180.0)*SIN(PI()* gi.latitude /180.0)+COS(PI()*trp.latitude/180.0)*COS(PI()* gi.latitude /180.0)*COS(PI()* gi.longitude /180.0-PI()*trp.longitude/180.0))*6371)*1000) AS distanceMet  FROM tdsb_i_Runpoints_position trp , gps_info_last gi  WHERE  trp.BusStopId = ? AND trp.route_point_id = ? AND gi.sn_imei_id = ?"; 
														
														qh.clearParams();
														qh.addParam(busStopid);
														qh.addParam(runID);
														qh.addParam(sn_imei_id);

															ResultSet rsPoints4 = qh.runQueryStreamResults(sql);
															Double distanceMeet=0.0;
															if(rsPoints4.next()) {
																distanceMeet = rsPoints4.getDouble("distanceMet");
															}
															 if(distanceMeet>250 && distanceMeet<1000) {
																sql = "INSERT INTO tdsb_i_run_initial (route_id, sn_imei_id, arrival_time,morning,after_school,departure_time,routeId) VALUES(?,?,?,?,?,?,?)";
																qh.clearParams();
																qh.addParam(runID);
																qh.addParam(sn_imei_id);
																qh.addParam(formatString.format(currenttime));
																qh.addParam(0);
																qh.addParam(1);
																qh.addParam(formatString.format(Date.from(currenttime.toInstant().plusSeconds(5))));
																qh.addParam(runID);
																qh.runQuery(sql);
																System.out.println(" After School Notification Sent");
																processParentNotification(runID,999,"5",999);
																this.cancel();
																
															}
															 if(distanceMeet>1000) {
																 this.cancel();
																 System.out.println("Distance is too large");
															 }
												      }			
												 }
											}else {
												this.cancel();
											}
											
											
										} catch(Exception e) {
											 e.printStackTrace();
											 this.cancel();
										} finally {
											qh.releaseConnection();
										}
										
									}
									 
					    		  };
					    		 
								new Timer().schedule(task,1000, 30*1000);
								
							
					    	  }catch (Exception e) {
					              e.printStackTrace();
					              System.out.println("CheckOut Thread isInterrepted");
					          }
					      }
					};
					service.submit(notifyOutRunnable);
				}	
			}
		} catch(Exception e) {
			 e.printStackTrace();
		} finally {
			helper.releaseConnection();
		}
		 
	}
//	public static void main(String [] arg) {
//		try {
//			String routeWorkString = "860147041678507"+"|" + 44.073046646326 + "|" + -80.1798895204029 + "|" + 86014133 + "|"+2+"|"+"2022-08-26 19:00:58.17";
//			GMSDispatcher gms=new GMSDispatcher(routeWorkString);
//			gms.run();
//			System.out.println("Final ");
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}