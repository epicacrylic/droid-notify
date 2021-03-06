package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.PhoneAlarmBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for scheduled Missed Call notifications that we want to display.
 * 
 * @author Camille S�vigny
 */
public class PhoneAlarmReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug(context);
		if (_debug) Log.v(context, "PhoneAlarmReceiver.onReceive()");
		try{
			WakefulIntentService.sendWakefulWork(context, new Intent(context, PhoneAlarmBroadcastReceiverService.class));
		}catch(Exception ex){
			Log.e(context, "PhoneAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}