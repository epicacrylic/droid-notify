package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.PhoneAlarmReceiver;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille S�vigny
 */
public class PhoneBroadcastReceiverService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public PhoneBroadcastReceiverService() {
		super("PhoneBroadcastReceiverService");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		Context context = getApplicationContext();
		try{
			boolean debug = Log.getDebug(context);
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (debug) Log.v(context, "PhoneBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
		    //Check the state of the users phone.
		    int callState = preferences.getInt(Constants.CALL_STATE_KEY, 0);
		    if (debug) Log.v(context, "PhoneService.doWakefulWork() PREVIOUS_CALL_STATE: " + preferences.getInt(Constants.PREVIOUS_CALL_STATE_KEY, TelephonyManager.CALL_STATE_IDLE));
		    if(callState == TelephonyManager.CALL_STATE_IDLE){
		    	if (debug) Log.v(context, "PhoneService.doWakefulWork() Phone Idle.");
		    	if(preferences.getInt(Constants.PREVIOUS_CALL_STATE_KEY, TelephonyManager.CALL_STATE_IDLE) != TelephonyManager.CALL_STATE_RINGING){
		    		if (debug) Log.v(context, "PhoneService.doWakefulWork() Previous call state not 'CALL_STATE_RINGING'. Exiting...");
		    	}else{
		    		if (debug) Log.v(context, "PhoneService.doWakefulWork() Previous call state 'CALL_STATE_RINGING'. Missed Call Occurred");
					//Schedule phone task x seconds after the broadcast.
					//This time is set by the users advanced preferences. 5 seconds is the default value.
					//This should allow enough time to pass for the phone log to be written to.
					long timeoutInterval = Long.parseLong(preferences.getString(Constants.CALL_LOG_TIMEOUT_KEY, "5")) * 1000;
					String intentActionText = "apps.droidnotify.alarm." + String.valueOf(System.currentTimeMillis());
					long alarmTime = System.currentTimeMillis() + timeoutInterval;
					Common.startAlarm(context, PhoneAlarmReceiver.class, null, intentActionText, alarmTime);
		    	}
		    }else if(callState == TelephonyManager.CALL_STATE_RINGING){
		    	if (debug) Log.v(context, "PhoneService.doWakefulWork() Phone Ringing.");
		    }else if(callState == TelephonyManager.CALL_STATE_OFFHOOK){
		    	if (debug) Log.v(context, "PhoneService.doWakefulWork() Phone Call In Progress.");
		    }else{
		    	if (debug) Log.v(context, "PhoneService.doWakefulWork() Unknown Call State: " + callState);
		    }
		    setPreviousCallStateFlag(preferences, callState);
	    }catch(Exception ex){
			Log.e(context, "PhoneBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set the phone state flag.
	 */
	private void setPreviousCallStateFlag(SharedPreferences preferences, int callState){
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Constants.PREVIOUS_CALL_STATE_KEY, callState);
		editor.commit();
	}
		
}