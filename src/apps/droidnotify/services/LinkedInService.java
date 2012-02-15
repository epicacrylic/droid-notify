package apps.droidnotify.services;

import com.google.code.linkedinapi.client.LinkedInApiClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.linkedin.LinkedInCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class handles the work of processing incoming LinkedIn messages.
 * 
 * @author Camille S�vigny
 */
public class LinkedInService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public LinkedInService() {
		super("LinkedInService");
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInService.LinkedInService()");
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
		if (_debug) Log.v("LinkedInService.doWakefulWork()");
		try{
			_context = getApplicationContext();
		    //Get LinkedIn Object
			LinkedInApiClient linkedInClient = LinkedInCommon.getLinkedIn(_context);
		    if(linkedInClient == null){
		    	if (_debug) Log.v("LinkedInService.doWakefulWork() LinkedInClient object is null. Exiting... ");
		    	return;
		    }
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			//Get LinkedIn updates.
		    if(_preferences.getBoolean(Constants.LINKEDIN_UPDATES_ENABLED_KEY, true)){
			    Bundle linkedInUpdateNotificationBundle = LinkedInCommon.getLinkedInupdates(_context, linkedInClient);
			    if(linkedInUpdateNotificationBundle != null){
					Bundle bundle = new Bundle();
					bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_LINKEDIN);
					bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, linkedInUpdateNotificationBundle);
			    	Common.startNotificationActivity(_context, bundle);
				}else{
					if (_debug) Log.v("LinkedInService.doWakefulWork() No LinkedIn Updates were found. Exiting...");
				}
		    }
		}catch(Exception ex){
			Log.e("LinkedInService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}