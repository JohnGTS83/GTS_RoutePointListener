package com.s5.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.s5.dto.PushNotificationEmun;
import com.s5.dto.PushResposeDTO;

public class PushNotification {
	private static String AUTH_KEY_FCM = "AAAA_OmItPI:APA91bHuF0RsVw5XTzKJn93xW9d2ed0gTRFTh5X_UPfyzeZbEsRuc63eyBD38HR8H8UNr8rrncE6IPGSLw0zIdEuuoGXs601xun_0PSIZGYQSTVRR_PWBi0CI9uAXLAvBpsd7ApHJW1M";
	// public final static String AUTH_KEY_FCM = "API_KEY_HERE";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	//IOS
	public static int sendNotificationToSmartphone(String token,int flag, int getExactDetails,long tokenid) throws ClientProtocolException, IOException, JSONException {
		try{
			String title = PushNotificationEmun.getTitleStringFromType(getExactDetails);
			String messageBody=PushNotificationEmun.getMessageStringFromType(getExactDetails);
			
			JsonObject mainData = new JsonObject();		
			JsonObject dataPayload = new JsonObject();	
			JsonObject notification = new JsonObject();
			if(messageBody.isEmpty())
				messageBody = "Message on notification";
			if(title.isEmpty())
				title = "Title Notification";
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
			Calendar cal = Calendar.getInstance(); 
			cal.add(Calendar.MILLISECOND,(-4 * 60 * 1000));
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			String stringDate = dateFormat.format(cal.getTime());
			
			dataPayload.addProperty("title",title);
			dataPayload.addProperty("body", messageBody);
			dataPayload.addProperty("notificationdate", stringDate);
			dataPayload.addProperty("launch-image","not_icon");

			notification.addProperty("body",messageBody);
			notification.addProperty("sound", "default");
			notification.addProperty("vibrate",true);
			notification.addProperty("title", title);
			notification.addProperty("icon", "push_icon");
			
			mainData.add("data",dataPayload);
			mainData.addProperty("to", token);
			mainData.addProperty("content_available", true);
			mainData.addProperty("priority", "high");
			mainData.add("notification", notification);			
			String resp = post(mainData);
			if(resp != null) {
				System.out.println("iOS:" + resp);
				JSONObject responseJson = new JSONObject(resp);
				int isSuccess = responseJson.getInt("success");
				if( isSuccess > 0) {
					System.out.println("Success");
				}else {
					System.out.println("Failure");
					NotificationProcessDAO processDAO = new NotificationProcessDAO();
					processDAO.cancelNotification(tokenid);
				}
				if(isSuccess > 0 ) {
					if(flag ==0 ) {
						flag= 1;
					}
					if(flag ==1) {
						flag= 2;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	private static String post(JsonObject notification) throws ClientProtocolException, IOException{
		String result = null;
		try {
			StringEntity postDataEn = new StringEntity(notification.toString());
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(API_URL_FCM);
			httppost.setHeader("Authorization" , "key=" + AUTH_KEY_FCM);
			httppost.setHeader("Content-Type"  , "application/json");
			httppost.setEntity(postDataEn);
			CloseableHttpResponse response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			response.close();
			client.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	//Other
	public static void pushFCMNotification(String DeviceIdKey, int getExactDetails,long tokenid,int busStopID) throws Exception {
		String authKey = AUTH_KEY_FCM; // You FCM AUTH key
		String FMCurl = API_URL_FCM;
		String title = PushNotificationEmun.getTitleStringFromType(getExactDetails);
		String messageBody=PushNotificationEmun.getMessageStringFromType(getExactDetails);
		
		URL url = new URL(FMCurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + authKey);
		conn.setRequestProperty("Content-Type", "application/json");

		JsonObject datapayload = new JsonObject();
		JsonObject info = new JsonObject();

		info.addProperty("title",title);
		info.addProperty("body", messageBody);
		info.addProperty("sound", "default");
		info.addProperty("icon", "notifications-outline");
		info.addProperty("forceStart", "1");
		info.addProperty("content-available", "1");
		info.addProperty("notId", ""+busStopID);
		datapayload.add("notification", info);

		JsonObject data = new JsonObject();	
		data.addProperty("notification_foreground","1");
		data.addProperty("notification_title",title);
		data.addProperty("notification_body", messageBody);
		datapayload.add("data", data);

		datapayload.addProperty("to", DeviceIdKey.trim());
		datapayload.addProperty("priority", "high");

//		info.addProperty("foreground", true);
//		info.addProperty("click_action", "FCM_PLUGIN_ACTIVITY");

//		JsonObject data = new JsonObject();
//		data.addProperty("param1", "value1");
//		data.addProperty("param2", "value2");
//		data.addProperty("android_channel_id", "testchannel2");
//		datapayload.add("data", data);

		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(datapayload.toString());
		wr.flush();
		wr.close();

		
		System.out.println("MESSAGE TO SEND : " + datapayload.toString());
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		System.out.println(response.toString());
		
		if(conn.getResponseCode() == HttpStatus.SC_OK) {
//			System.out.println("Response Code : " + conn.getResponseCode());
//			{"multicast_id":283505441812897705,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1649887609519646%8ec26cc28ec26cc2"}]}
//			{"multicast_id":6251534611723373982,"success":0,"failure":1,"canonical_ids":0,"results":[{"error":"NotRegistered"}]}
			try {
				Gson gson = new Gson();
				PushResposeDTO dto = gson.fromJson(response.toString(), PushResposeDTO.class);
				NotificationProcessDAO processDAO = new NotificationProcessDAO();
				if(dto.getSuccess() == 0) {
					System.out.println(dto.toString());
					processDAO.cancelNotification(tokenid);
				} 
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		in.close();
	}
//	public static void main(String [] arg) {
//		int flag = 0;
//		try {
//			flag = sendNotificationToSmartphone("djqyXAMMS1-Gkmlxk4Ppxp:APA91bFSBhevtzg63PYtyujWHQvVCezosfdwL4ifnWnpL0yM_a6YPZuDTidxv-Vs5kv9gGfuKe1AYzbGR2NvBzBfsc1fn-iKqXHbu5vv2ud_xgY90YTAJL0FvIhNiIT15iKFU2bi8DUp",flag,PushNotificationEmun.BUSREACHEDATSCHOOL,646);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}