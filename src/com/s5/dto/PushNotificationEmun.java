package com.s5.dto;


public class PushNotificationEmun {

	public static final int BUSREACHEDATSCHOOL = 1;
	public static final int BUSRREADYFORDROP = 2;
	public static final int BUSREACHEDATYOURSTOPFORPICKUP = 3;
	public static final int BUSREACHEDATYOURSTOPFORDROP = 4;
	
	public static final int BUSONTHEWAYFORPICKUP = 5;
	public static final int BUSONTHEWAYFORDROP = 6;
	
//	public static final String BUSREACHEDATSCHOOLTEXT = "Bus is reached to school";
//	public static final String BUSRREADYFORDROPTEXT ="Bus is reached to school and will depart shortly.!";
//	public static final String BUSREACHEDATYOURSTOPTEXT = "Bus is arrived at your stop.!";
//	public static final String BUSONTHEWAYFORPICKUPTEXT ="The Bus will be arriving in some time! Please get ready for Pick up!";
//	public static final String BUSONTHEWAYFORDROPTEXT ="The Bus will be arriving in some time for drop! Please get ready for Drop-off!";
	
	
	public static final String BUSREACHEDATSCHOOLTEXT = "The Bus has arrived at school";
	public static final String BUSRREADYFORDROPTEXT ="The Bus has arrived at school and will depart shortly.";
	public static final String BUSREACHEDATYOURSTOPTEXT = "The Bus has arrived at your stop.";
	
	public static final String BUSONTHEWAYFORPICKUPTEXT ="The School vehicle has started the route, please expect the pick up in some time.";
	public static final String BUSONTHEWAYFORDROPTEXT ="The School vehicle has started the route, please expect the drop off in some time.";
	
	
	public static String getTitleStringFromType(int enumType){
		return "GT Guardian";
	}
	
	public static String getMessageStringFromType(int enumType){
		 String s = "";
		 switch (enumType) {
		 	case BUSREACHEDATYOURSTOPFORPICKUP:
	        	s = PushNotificationEmun.BUSREACHEDATYOURSTOPTEXT;
	            break;
		 	case BUSREACHEDATYOURSTOPFORDROP:
	        	s = PushNotificationEmun.BUSREACHEDATYOURSTOPTEXT;
	            break; 
		 	case BUSRREADYFORDROP:
	            s = PushNotificationEmun.BUSRREADYFORDROPTEXT;
	            break;
	        case BUSREACHEDATSCHOOL:
	        	s = PushNotificationEmun.BUSREACHEDATSCHOOLTEXT;
	            break;
	            
	        case BUSONTHEWAYFORPICKUP:
	        	s = PushNotificationEmun.BUSONTHEWAYFORPICKUPTEXT;
	            break;
	        case BUSONTHEWAYFORDROP:
	            s = PushNotificationEmun.BUSONTHEWAYFORDROPTEXT;
	            break;
	    }
		 return s;
	}
}
