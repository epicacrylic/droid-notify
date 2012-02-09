package apps.droidnotify.services;

import java.util.ArrayList;

import com.google.code.linkedinapi.client.LinkedInApiClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.linkedin.LinkedInCommon;
import apps.droidnotify.log.Log;

public class LinkedInAlarmBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public LinkedInAlarmBroadcastReceiverService() {
		super("LinkedInAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.LinkedInAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if LinkedIn notifications are disabled.
		    if(!preferences.getBoolean(Constants.LINKEDIN_NOTIFICATIONS_ENABLED_KEY, false)){
				if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() LinkedIn Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = true;
		    boolean rescheduleNotificationInQuickReply = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean inQuickReplyApp = Common.isUserInQuickReplyApp(context);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else if(inQuickReplyApp){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInQuickReply = preferences.getBoolean(Constants.IN_QUICK_REPLY_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, LinkedInService.class));
		    }else{
			    //Get LinkedIn Object
				LinkedInApiClient linkedInClient = LinkedInCommon.getLinkedIn(context);
			    if(linkedInClient == null){
			    	if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() LinkedInClient object is null. Exiting... ");
			    	return;
			    }
			    ArrayList<String> linkedInUpdateArray = LinkedInCommon.getLinkedInupdates(context, linkedInClient);
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.LINKEDIN_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		//Get the Twitter message info.
					String contactName = null;
					String messageAddress = null;
					String messageBody = null;
				    if(preferences.getBoolean(Constants.LINKEDIN_UPDATES_ENABLED_KEY, true)){
					    if(linkedInUpdateArray != null){
					    	int linkedInUpdateArraySize = linkedInUpdateArray.size();
					    	for(int i=0; i<linkedInUpdateArraySize; i++ ){
					    		String facebookArrayItem = linkedInUpdateArray.get(i);
								String[] facebookInfo = facebookArrayItem.split("\\|");
				    			int arraySize = facebookInfo.length;
				    			if(arraySize > 0){
									if(arraySize >= 1) messageAddress = facebookInfo[1];
									if(arraySize >= 2) messageBody = facebookInfo[3];
									if(arraySize >= 8) contactName = facebookInfo[7];
				    			}
								//Display Status Bar Notification
							    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_LINKEDIN, Constants.NOTIFICATION_TYPE_LINKEDIN_UPDATE, callStateIdle, contactName, messageAddress, messageBody, null, null);
					    	}
						}else{
							if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() No Facebook Nnotifications were found. Exiting...");
						}
					}
			    }					
		    	//if(linkedInUpdateArray != null) Common.rescheduleBlockedNotification(context, rescheduleNotificationInCall, rescheduleNotificationInQuickReply, Constants.NOTIFICATION_TYPE_LINKEDIN, linkedInUpdateArray);
		    }
		}catch(Exception ex){
			Log.e("LinkedInAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}