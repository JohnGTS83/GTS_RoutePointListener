package com.s5.dto;

public class AlarmEnum {
	public static final int SOSButton_Pressed_Input1_Active = 1;
	public static final int Input2_Active = 2;
	public static final int Input3_Active = 3;
	public static final int SOSButton_Pressed_Input1_InActive = 9;
	public static final int Input2_InActive = 10;
	public static final int Input3_InActive = 11;
	public static final int Enter_Sleep = 26;
	public static final int Exit_Sleep = 27;

	public static final int GT_IN1_VALID                   = 1    ;
	public static final int GT_IN2_VALID                   = 2    ;
	public static final int GT_IN3_VALID                   = 3    ;
	public static final int GT_IN1_INVALID                 = 49   ;
	public static final int GT_IN2_INVALID                 = 50   ;
	public static final int GT_IN3_INVALID                 = 51   ;
	public static final int GT_ENTERSLEEP                  = 116  ;
	public static final int GT_EXITSLEEP                   = 117  ;
	
	public static final int GMS50_Device_Tracker_ID = 101;
	public static final int GT_IGNITION_ON                 = 144  ;
	public static final int GT_IGNITION_OFF                = 145  ;
	
	public static final int GT_REVERSE_ACCELERATION_ALARM  = 205  ;
	public static final int GT_ALARMRESOURCE_DOOROPEN      = 401  ;
	public static final int GT_ALARMRESOURCE_DOORCLOSE     = 402  ;
	public static final int GT_ReverseDoneAlarm            = 403  ;
	public static final int GT_LIGHTS_ON                   = 404  ;
	public static final int GT_LIGHTS_OFF                  = 405  ;
	public static final int GT_Default                  = 1  ;
			
	public final static int[] FETCH_GMS50_ALARM_ENUM = {
		GT_REVERSE_ACCELERATION_ALARM,
		GT_ALARMRESOURCE_DOOROPEN,
		GT_ALARMRESOURCE_DOORCLOSE,
		GT_ReverseDoneAlarm,
		GT_LIGHTS_ON,
		GT_LIGHTS_OFF,
		GT_Default
	};
	
	public final static int[] FETCH_ALARM_ENUM = {
		GT_IN1_VALID,
		GT_IN2_VALID,
		GT_IN3_VALID,    
		GT_IN1_INVALID,  
		GT_IN2_INVALID,  
		GT_IN3_INVALID,  
	    GT_ENTERSLEEP,  
        GT_EXITSLEEP
	};
	
	public final static int[] FETCH_T399_ALARM_ENUM = {
		SOSButton_Pressed_Input1_Active,
		Input2_Active,
		Input3_Active,    
		SOSButton_Pressed_Input1_InActive,  
		Input2_InActive,  
		Input3_InActive,  
		Enter_Sleep,  
		Exit_Sleep
	};
}