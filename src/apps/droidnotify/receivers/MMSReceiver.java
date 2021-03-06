package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.MMSBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;
import apps.droidnotify.common.Constants;

/**
 * This class listens for incoming MMS messages.
 * 
 * @author Camille S�vigny
 */
public class MMSReceiver extends BroadcastReceiver{
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * This function starts the service that will handle the work or reschedules the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug(context);
		if (_debug) Log.v(context, "MMSReceiver.onReceive()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "MMSReceiver.onReceive() App Disabled. Exiting...");
				return;
			}
			//Read preferences and exit if MMS notifications are disabled.
		    if(!preferences.getBoolean(Constants.SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "MMSReceiver.onReceive() SMS Notifications Disabled. Exiting...");
				return;
			}
			WakefulIntentService.sendWakefulWork(context, new Intent(context, MMSBroadcastReceiverService.class));
		}catch(Exception ex){
			Log.e(context, "MMSReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}