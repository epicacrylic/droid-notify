package apps.droidnotify.k9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "K9 Notifications" applications preference Activity.
 * 
 * @author Camille S�vigny
 */
public class K9PreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context = null;
    private SharedPreferences _preferences = null;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    checkK9PackageInstallation(false);
	    this.addPreferencesFromResource(R.xml.k9_preferences);
	    this.setContentView(R.layout.k9_preferences);
	    setupCustomPreferences();
	}
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(Constants.K9_NOTIFICATIONS_ENABLED_KEY)){
			if(_preferences.getBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, true)){
				checkK9PackageInstallation(true);
			}
		}
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    super.onResume();
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause(){
	    super.onPause();
	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	@SuppressWarnings("deprecation")
	private void setupCustomPreferences(){
		//Status Bar Notification Settings Preference/Button
		Preference statusBarNotificationSettingsPref = (Preference)findPreference(Constants.SETTINGS_STATUS_BAR_NOTIFICATIONS_PREFERENCE);
		statusBarNotificationSettingsPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, K9StatusBarNotificationsPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "K9PreferenceActivity() Status Bar Notifications Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Customize Preference/Button
		Preference customizePref = (Preference)findPreference(Constants.SETTINGS_CUSTOMIZE_PREFERENCE);
		customizePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, K9CustomizePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "K9PreferenceActivity() Customize Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
	}
	
	/**
	 * Check the users phone for the K-9 or Kaiten package installation.
	 * If this is not found on the users phone, display a custom dialog box.
	 */
	@SuppressWarnings("deprecation")
	private void checkK9PackageInstallation(boolean notifyFlag){
        try{
			//Look for K-9 Mail, Kaiten Mail or K-9 Mail for Pure Widget.
			boolean packageInstalledFlag = Common.packageExists(_context, "com.fsck.k9") || 
					Common.packageExists(_context, "com.kaitenmail") || 
					Common.packageExists(_context, "org.koxx.k9ForPureWidget");
			if(packageInstalledFlag){
				return;
			}else{
				Log.v(_context, "K9PreferenceActivity().checkK9PackageInstallation() K9 Client Packages Not Found!");	
				SharedPreferences.Editor editor = _preferences.edit();
	        	editor.putBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, false);
	            editor.commit();	
	            if(notifyFlag){
					//Disable K9 notifications.
		            CheckBoxPreference k9NotificationsEnabledCheckbox = (CheckBoxPreference)findPreference(Constants.K9_NOTIFICATIONS_ENABLED_KEY);
		            if(k9NotificationsEnabledCheckbox != null) k9NotificationsEnabledCheckbox.setChecked(false);
		            //Prompt user to download a K-9 email client.
		    		startActivity(new Intent(_context, K9DownloadPreferenceActivity.class));	 
	            }
			}
        }catch(Exception ex){
        	Log.e(_context, "K9PreferenceActivity().checkK9PackageInstallation() ERROR: " + ex.toString());
        }
	}
	
}