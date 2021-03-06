package apps.droidnotify;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import apps.droidnotify.k9.K9Common;
import apps.droidnotify.log.Log;
import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.contacts.ContactsCommon;
import apps.droidnotify.phone.PhoneCommon;
import apps.droidnotify.preferences.PreferencesActivity;
import apps.droidnotify.receivers.ScreenManagementAlarmReceiver;
import apps.droidnotify.sms.SMSCommon;

/**
 * This is the main activity that runs the notifications.
 * 
 * @author Camille S�vigny
 */
public class NotificationActivity extends Activity{

	//================================================================================
    // Constants
    //================================================================================
	
	//Context Menu Constants
	private static final int MENU_ITEM_SETTINGS = R.id.settings;
	private static final int MENU_ITEM_SHARE = R.id.share;
	private static final int MENU_ITEM_DISMISS_ALL = R.id.dismiss_all;
	private static final int MENU_ITEM_DISMISS_ALL_SAME_USER = R.id.dismiss_all_same_user;
	private static final int MENU_ITEM_DISMISS_ALL_SAME_APP = R.id.dismiss_all_same_app;
	
	private static final int CONTACT_WRAPPER_LINEAR_LAYOUT = R.id.contact_wrapper_linear_layout;
	private static final int ADD_CONTACT_CONTEXT_MENU = R.id.add_contact_context_menu;	
	private static final int EDIT_CONTACT_CONTEXT_MENU = R.id.edit_contact_context_menu;
	private static final int VIEW_CONTACT_CONTEXT_MENU = R.id.view_contact_context_menu;	
	private static final int VIEW_CALL_LOG_CONTEXT_MENU = R.id.view_call_log_context_menu;
	private static final int CALL_CONTACT_CONTEXT_MENU = R.id.call_contact_context_menu;
	private static final int MESSAGING_INBOX_CONTEXT_MENU = R.id.messaging_inbox_context_menu;
	private static final int VIEW_THREAD_CONTEXT_MENU = R.id.view_thread_context_menu;
	private static final int TEXT_CONTACT_CONTEXT_MENU = R.id.text_contact_context_menu;
	private static final int ADD_CALENDAR_EVENT_CONTEXT_MENU = R.id.add_calendar_event_context_menu;
	private static final int EDIT_CALENDAR_EVENT_CONTEXT_MENU = R.id.edit_calendar_event_context_menu;
	private static final int VIEW_CALENDAR_CONTEXT_MENU = R.id.view_calendar_context_menu;
	private static final int VIEW_K9_INBOX_CONTEXT_MENU = R.id.view_k9_inbox_context_menu;
	private static final int RESCHEDULE_NOTIFICATION_CONTEXT_MENU = R.id.reschedule_notification_context_menu;
	private static final int SPEAK_NOTIFICATION_CONTEXT_MENU = R.id.speak_notification_context_menu;
	private static final int DISMISS_NOTIFICATION_CONTEXT_MENU = R.id.dismiss_notification_context_menu;
	private static final int DISMISS_ALL_CONTEXT_MENU = R.id.dismiss_all_context_menu;
	private static final int DISMISS_ALL_USER_CONTEXT_MENU = R.id.dismiss_all_same_user_context_menu;
	private static final int DISMISS_ALL_APP_CONTEXT_MENU = R.id.dismiss_all_same_app_context_menu;	
	private static final int DISMISS_BUTTON = R.id.dismiss_button;	

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private NotificationViewFlipper _notificationViewFlipper = null;
	private MotionEvent _downMotionEvent = null;
	private SharedPreferences _preferences = null;
	private PendingIntent _screenTimeoutPendingIntent = null;
	private TextToSpeech _tts = null;

	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return notificationViewFlipper - Applications' ViewFlipper.
	 */
	public NotificationViewFlipper getNotificationViewFlipper(){
	    return _notificationViewFlipper;
	}
	
	/**
	 * Creates the menu item for this activity.
	 * 
	 * @param menu - Menu object.
	 * 
	 * @return boolean - Returns super onCreateOptionsMenu().
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.optionsmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Handle the users selecting of the menu items.
	 * 
	 * @param menuItem - Menu Item .
	 * 
	 * @return boolean - Returns true to indicate that the action was handled.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
	    // Handle item selection
	    switch (menuItem.getItemId()){	    	
	    	case MENU_ITEM_DISMISS_ALL:{
	    		dismissAllNotifications();
	    		break;
	    	}	    	
	    	case MENU_ITEM_DISMISS_ALL_SAME_USER:{
	    		dismissAllUserNotifications();
	    		break;
	    	}	    	
	    	case MENU_ITEM_DISMISS_ALL_SAME_APP:{
	    		dismissAllAppNotifications();
	    		break;
	    	}	    	
	    	case MENU_ITEM_SHARE:{
	    		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	    		shareIntent.setType("text/plain");
	    		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, _context.getString(R.string.share_title));
	    		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, _context.getString(R.string.share_description));
	    		startActivity(shareIntent);
	    		break;
	    	}
	    	case MENU_ITEM_SETTINGS:{
	    		launchPreferenceScreen();
	    		break;
	    	}
	    }
	    return super.onOptionsItemSelected(menuItem);
	}
	
	/**
	 * Create Context Menu (Long-press menu).
	 * 
	 * @param contextMenu - ContextMenu
	 * @param view - View
	 * @param contextMenuInfo - ContextMenuInfo
	 */
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo){
	    super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
	    if(_preferences.getBoolean(Constants.CONTEXT_MENU_DISABLED_KEY, false)){
	    	return;
	    }
	    switch (view.getId()){
	        /*
	         * Contact info/photo ConextMenu.
	         */
			case CONTACT_WRAPPER_LINEAR_LAYOUT:{
				MenuInflater menuInflater = getMenuInflater();
				Notification notification = _notificationViewFlipper.getActiveNotification();
				int notificationType = notification.getNotificationType();
				//Adjust for Preview notifications.
				if(notificationType > 1999){
					notificationType -= 2000;
				}
				//Add the header text to the menu.
				if(notificationType == Constants.NOTIFICATION_TYPE_GENERIC){
					//Don't add a title if it's a generic notification.
				}else{
					if(notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
						contextMenu.setHeaderTitle(_context.getString(R.string.calendar_event_text));
					}else{
						if(notification.getContactExists()){
							contextMenu.setHeaderTitle(notification.getContactName()); 
						}else{
							contextMenu.setHeaderTitle(notification.getSentFromAddress()); 
						}
					}
				}
				menuInflater.inflate(R.menu.notificationcontextmenu, contextMenu);
				//Remove menu options based on the NotificationType.
				if(notification.getContactExists()){
					MenuItem addContactMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
					addContactMenuItem.setVisible(false);
				}else{
					MenuItem editContactMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
					editContactMenuItem.setVisible(false);
					MenuItem viewContactMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
					viewContactMenuItem.setVisible(false);
				}
				setupContextMenus(contextMenu, notificationType);
				break;
			}
	        /*
	         * Dismiss Button ConextMenu.
	         */
			case DISMISS_BUTTON:{
				MenuInflater menuInflater = getMenuInflater();
				menuInflater.inflate(R.menu.dismissbuttoncontextmenu, contextMenu);
				break;
			}
	    }  
	}

	/**
	 * Context Menu Item Selected (Long-press menu item selected).
	 * 
	 * @param menuItem - Create the context menu items for this Activity.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem menuItem){
		if(_debug) Log.v(_context, "NotificationActivity.onContextItemSelected()");
		final Notification notification = _notificationViewFlipper.getActiveNotification();
	    //Complete the selected action.
		switch (menuItem.getItemId()){
			case ADD_CONTACT_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return ContactsCommon.startContactAddActivity(_context, this, notification.getSentFromAddress(), Constants.ADD_CONTACT_ACTIVITY);
			}
			case EDIT_CONTACT_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return ContactsCommon.startContactEditActivity(_context, this, notification.getContactID(), Constants.EDIT_CONTACT_ACTIVITY);
			}
			case VIEW_CONTACT_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return ContactsCommon.startContactViewActivity(_context, this, notification.getContactID(), Constants.VIEW_CONTACT_ACTIVITY);
			}
			case VIEW_CALL_LOG_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return PhoneCommon.startCallLogViewActivity(_context, this, Constants.VIEW_CALL_LOG_ACTIVITY);
			}
			case CALL_CONTACT_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					final String[] phoneNumberArray = getPhoneNumbers(notification);
					if(phoneNumberArray == null){
						Toast.makeText(_context, _context.getString(R.string.app_android_no_number_found_error), Toast.LENGTH_LONG).show();
						return false;
					}else if(phoneNumberArray.length == 1){
						return makePhoneCall(phoneNumberArray[0]);
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setTitle(_context.getString(R.string.select_number_text));
						builder.setSingleChoiceItems(phoneNumberArray, -1, new DialogInterface.OnClickListener(){
						    public void onClick(DialogInterface dialog, int selectedPhoneNumber){
						        //Launch the SMS Messaging app to send a text to the selected number.
						    	String[] phoneNumberInfo = phoneNumberArray[selectedPhoneNumber].split(":");
						    	if(phoneNumberInfo.length == 2){
						    		makePhoneCall(phoneNumberInfo[1].trim());
						    	}else{
						    		Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
						    	}
						    	//Close the dialog box.
						    	dialog.dismiss();
						    }
						});
						builder.create().show();
						return true;
					}
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() CALL_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
					return false;
				}
			}
			case MESSAGING_INBOX_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				if(SMSCommon.startMessagingAppViewInboxActivity(_context, this, Constants.MESSAGING_ACTIVITY)){
					return true;
				}else{
					return false;
				}
			}
			case VIEW_THREAD_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				if(SMSCommon.startMessagingAppViewThreadActivity(_context, this, notification.getThreadID(), Constants.VIEW_SMS_THREAD_ACTIVITY)){
					return true;
				}else{
					return false;
				}
			}
			case TEXT_CONTACT_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					final String[] phoneNumberArray = getPhoneNumbers(notification);
					if(phoneNumberArray == null){
						Toast.makeText(_context, _context.getString(R.string.app_android_no_number_found_error), Toast.LENGTH_LONG).show();
						return false;
					}else if(phoneNumberArray.length == 1){
						return sendSMSMessage(phoneNumberArray[0]);
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setTitle(_context.getString(R.string.select_number_text));
						builder.setSingleChoiceItems(phoneNumberArray, -1, new DialogInterface.OnClickListener(){
						    public void onClick(DialogInterface dialog, int selectedPhoneNumber){
						        //Launch the SMS Messaging app to send a text to the selected number.
						    	String[] phoneNumberInfo = phoneNumberArray[selectedPhoneNumber].split(":");
						    	if(phoneNumberInfo.length == 2){
						    		sendSMSMessage(phoneNumberInfo[1].trim());
						    	}else{
						    		Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
						    	}
						    	dialog.dismiss();
						    }
						});
						builder.create().show();
						return true;
					}
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() TEXT_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
					return false;
				}
			}
			case ADD_CALENDAR_EVENT_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return CalendarCommon.startAddCalendarEventActivity(_context, this, Constants.ADD_CALENDAR_ACTIVITY);
			}
			case EDIT_CALENDAR_EVENT_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return CalendarCommon.startEditCalendarEventActivity(_context, this, notification.getCalendarEventID(), notification.getCalendarEventStartTime(), notification.getCalendarEventEndTime(), Constants.EDIT_CALENDAR_ACTIVITY);
			}
			case VIEW_CALENDAR_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return CalendarCommon.startViewCalendarActivity(_context, this, Constants.CALENDAR_ACTIVITY);
			}
			case VIEW_K9_INBOX_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				return K9Common.startK9EmailAppViewInboxActivity(_context, this, notification.getNotificationSubType(), Constants.K9_VIEW_EMAIL_ACTIVITY);
			}
			case RESCHEDULE_NOTIFICATION_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					_notificationViewFlipper.rescheduleNotification();
					return true;
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() RESCHEDULE_NOTIFICATION_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			case SPEAK_NOTIFICATION_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					speak();
					return true;
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() SPEAK_NOTIFICATION_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			case DISMISS_NOTIFICATION_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
					return true;
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() DISMISS_NOTIFICATION_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			case DISMISS_ALL_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					dismissAllNotifications();
					return true;
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() DISMISS_ALL_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			case DISMISS_ALL_USER_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					dismissAllUserNotifications();
					return true;
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() DISMISS_ALL_USER_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			case DISMISS_ALL_APP_CONTEXT_MENU:{
				//Cancel the reminder.
				notification.cancelReminder();
				try{
					dismissAllAppNotifications();
					return true;
				}catch(Exception ex){
					Log.e(_context, "NotificationActivity.onContextItemSelected() DISMISS_ALL_APP_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			default:{
				return super.onContextItemSelected(menuItem);
			}
		}
	}
	
	/**
	 * Handles the activity when the configuration changes (e.g. The phone switches from portrait view to landscape view).
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);   
        if(_debug) Log.v(_context, "NotificationActivity.onConfigurationChanged()");
        if(!_preferences.getBoolean(Constants.AUTO_ROTATE_SCREEN_KEY, false)){
        	return;
        }
        //Adjust the width of the ViewFlipper Views.
        _notificationViewFlipper.setNotificationProperties();
        //Adjust the vertical location of the ViewFlipper.
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		String verticalLocation = _preferences.getString(Constants.POPUP_VERTICAL_LOCATION_KEY, Constants.POPUP_VERTICAL_LOCATION_DEFAULT);
		if(verticalLocation.equals(Constants.POPUP_VERTICAL_LOCATION_TOP)){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}else if(verticalLocation.equals(Constants.POPUP_VERTICAL_LOCATION_BOTTOM)){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}else{			
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		}
		_notificationViewFlipper.setLayoutParams(layoutParams);
		//Adjust the max lines in the notifications body.
		//Device properties.
		Display display = this.getWindowManager().getDefaultDisplay(); 
        Point size = new Point();
        int screenWidth;
        if(Common.getDeviceAPILevel() >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
	        display.getSize(size);
	        screenWidth = size.x;
        }else{
        	screenWidth = display.getWidth();
        }
    	int maxLines = Integer.parseInt(_preferences.getString(Constants.NOTIFICATION_BODY_MAX_LINES_KEY, Constants.NOTIFICATION_BODY_MAX_LINES_DEFAULT));
        int phoneOrientation = getResources().getConfiguration().orientation;
	    if(phoneOrientation == Configuration.ORIENTATION_LANDSCAPE){
	    	if(screenWidth >= 800 && screenWidth < 1280){
	    	    if(maxLines >= 3) maxLines = ((maxLines / 3) > 2) ? (maxLines / 3) : 2;
	        }else if(screenWidth >= 1280){
	    	    if(maxLines >= 2) maxLines = ((maxLines / 2) > 2) ? (maxLines / 2) : 2;
	        }else{
	        	if(maxLines < 4){
	        		maxLines = 2;
	        	}else{
	        		maxLines = ((maxLines / 4) > 2) ? (maxLines / 4) : 2;
	        	}
	        }
        }
    	_notificationViewFlipper.setNotificationBodyMaxLines(maxLines);
	}

	/**
	 * This function intercepts all the touch events.
	 * In here we decide what to pass on to child items and what to handle ourselves.
	 * 
	 * @param motionEvent - The touch event that occurred.
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent){
	    switch (motionEvent.getAction()){
	        case MotionEvent.ACTION_DOWN:{
		        //Keep track of the starting down-event.
		        _downMotionEvent = MotionEvent.obtain(motionEvent);
			    //Poke the screen timeout.
			    setScreenTimeoutAlarm();
		        break;
	        }
	        case MotionEvent.ACTION_UP:{
	            //Consume if necessary and perform the fling / swipe action if it has been determined to be a fling / swipe.
	        	float deltaX = motionEvent.getX() - _downMotionEvent.getX();
	        	final ViewConfiguration viewConfiguration = ViewConfiguration.get(_context); 
		        if(Math.abs(deltaX) > viewConfiguration.getScaledTouchSlop()*2){
		        	if(deltaX < 0){
		        		_notificationViewFlipper.showNext();
	           	    	return true;
					}else if(deltaX > 0){
						_notificationViewFlipper.showPrevious();
	           	    	return true;
	               	}
		        }
	            break;
	        }
	    }
	    return super.dispatchTouchEvent(motionEvent);
	}
	
	/**
	 * Speak the notification message using TTS.
	 */
	public void speak(){
		if(_tts == null){
			setupTextToSpeech();
		}else{
			Notification notification = _notificationViewFlipper.getActiveNotification();
			notification.speak(_tts);
		}
	}

	/**
	 * Sets the alarm that will clear the KeyguardLock & WakeLock.
	 */
	public void setScreenTimeoutAlarm(){
		if (_debug) Log.v(_context, "NotificationActivity.setScreenTimeoutAlarm()");
		new SetScreenTimeoutTask().execute();
	}

	/**
	 * Stops the playback of the TTS message.
	 */
	public void stopTextToSpeechPlayback(){
	    if(_tts != null){
	    	_tts.stop();
	    }
	}
  	
  	/**
  	 * Dismiss all notifications and close the activity.
  	 */
  	public void dismissAllNotifications(){
  		try{
  			new DismissAllNotificationsTask().execute();
  		}catch(Exception ex){
  			Log.e(_context, "NotificationActivity.dismissAllNotifications() ERROR: " + ex.toString());
  		}
  	}
  	
  	/**
  	 * Dismiss all notifications from the same user.
  	 */
  	public void dismissAllUserNotifications(){
  		try{
  			_notificationViewFlipper.dismissAllUserNotifications(_context);
  		}catch(Exception ex){
  			Log.e(_context, "NotificationActivity.dismissAllUsersNotifications() ERROR: " + ex.toString());
  		}
  	}
  	
  	/**
  	 * Dismiss all notifications from the same app.
  	 */
  	public void dismissAllAppNotifications(){
  		try{
  			_notificationViewFlipper.dismissAllAppNotifications(_context);
  		}catch(Exception ex){
  			Log.e(_context, "NotificationActivity.dismissAllAppNotifications() ERROR: " + ex.toString());
  		}
  	}
  	
  	/**
  	 * Start the Speech-To-Text voice recognition activity.
  	 */
  	public void startStt(){
  		try{
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		    //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
		    startActivityForResult(intent, Constants.STT_ACTIVITY);
  		}catch(Exception ex){
  			Log.e(_context, "NotificationActivity.startStt() ERROR: " + ex.toString());	
  			Toast.makeText(_context, _context.getString(R.string.voice_recognizer_error), Toast.LENGTH_LONG).show();
  		}
  	}
	
	//================================================================================
	// Protected Methods
	//================================================================================

	/**
	 * When a result is returned from an Activity that this activity launched, react based on the returned result.
	 * 
	 * @param requestCode - The Activity code id that the result came from.
	 * @param resultCode - The result from the Activity.
	 * @param returnedIntent - The intent that was returned.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent){		
		try{
	    	Common.setInLinkedAppFlag(_context, false);
		    switch(requestCode){
			    case Constants.ADD_CONTACT_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.EDIT_CONTACT_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.VIEW_CONTACT_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.SEND_SMS_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.VIEW_SMS_MESSAGE_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.VIEW_SMS_THREAD_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.MESSAGING_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.CALL_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_phone_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.ADD_CALENDAR_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.EDIT_CALENDAR_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.VIEW_CALENDAR_ACTIVITY:{ 
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.VIEW_CALL_LOG_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
			    		_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_call_log_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.CALENDAR_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
			    		_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.K9_VIEW_INBOX_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
			    		_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_email_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.K9_VIEW_EMAIL_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
			    		_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_email_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.K9_SEND_EMAIL_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			        	//Remove notification from ViewFlipper.
			    		_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
			    		//Remove notification from ViewFlipper.
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.app_email_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.TEXT_TO_SPEECH_ACTIVITY:{
			        if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
			            //Success, create the TTS instance.
			            _tts = new TextToSpeech(_context, ttsOnInitListener);
			        }else{
			            //Missing data, install it.
			            Intent installIntent = new Intent();
			            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			            startActivity(installIntent);
			        }
			        break;
			    }
			    case Constants.BROWSER_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			    		_notificationViewFlipper.removeActiveNotification(false);
			    	}else if(resultCode == RESULT_CANCELED){
						_notificationViewFlipper.removeActiveNotification(false);
			    	}else{
			        	Toast.makeText(_context, _context.getString(R.string.browser_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
			    	}
			        break;
			    }
			    case Constants.STT_ACTIVITY:{
			    	if(resultCode == RESULT_OK){
			    		//Populate the current notification reply message with the text from the voice recognition engine.
			            ArrayList<String> recognizedTextResults = returnedIntent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			            _notificationViewFlipper.setQuickReplyText(recognizedTextResults);
			    	}
			        break;
			    }
			}
			super.onActivityResult(requestCode, resultCode, returnedIntent);
		}catch(Exception ex){
			Log.e(_context, "NotificationActivity.onActivityResult() ERROR: " + ex.toString());
	    	Common.setInLinkedAppFlag(_context, false);
			_notificationViewFlipper.removeActiveNotification(false);
		}
    }
	
	/**
	 * Called when the activity is created. Set up views and notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
	    _context = getApplicationContext();
		_debug = Log.getDebug(_context);
	    if(_debug) Log.v(_context, "NotificationActivity.onCreate()");
		int apiLevel = Common.getDeviceAPILevel();
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    Common.setApplicationLanguage(_context, this);
	    Common.setInLinkedAppFlag(_context, false);
	    final Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt(Constants.BUNDLE_NOTIFICATION_TYPE);
	    if(_debug) Log.v(_context, "NotificationActivity.onCreate() Notification Type: " + notificationType);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!_preferences.getBoolean(Constants.AUTO_ROTATE_SCREEN_KEY, false)){
	    	if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
	    		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    	}else{
	    		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    	}
	    }
	    //Get main window for this Activity.
	    Window mainWindow = getWindow();
	    //Don't automatically show the soft keyboard.
	    mainWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    	//Set Background Blur Flags
	    if(_preferences.getBoolean(Constants.BLUR_SCREEN_BACKGROUND_ENABLED_KEY, false)){
	    	if(apiLevel <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
	    		mainWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	    	}
	    }	 
	    //Set Background Dim Flags
	    if(_preferences.getBoolean(Constants.DIM_SCREEN_BACKGROUND_ENABLED_KEY, false)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		    WindowManager.LayoutParams params = mainWindow.getAttributes(); 
		    int dimAmt = Integer.parseInt(_preferences.getString(Constants.DIM_SCREEN_BACKGROUND_AMOUNT_KEY, "50"));
		    params.dimAmount = dimAmt / 100f;
		    mainWindow.setAttributes(params); 
	    }
	    //Hide Status Bar
	    if(_preferences.getBoolean(Constants.HIDE_STATUS_BAR_KEY, false)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    }
	    setContentView(R.layout.notification_wrapper);
	    setupViews(notificationType);
	    setupViewFlipperStyles();
	    switch(notificationType){ 
		    case Constants.NOTIFICATION_TYPE_PHONE:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
		    	if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_SMS:{
			    if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			    if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}else{
					if(_preferences.getBoolean(Constants.SMS_DISPLAY_UNREAD_KEY, false)){
						new getAllUnreadSMSMessagesAsyncTask().execute();
				    }
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_MMS:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
		    	if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}else{
					if(_preferences.getBoolean(Constants.MMS_DISPLAY_UNREAD_KEY, false)){
						new getAllUnreadMMSMessagesAsyncTask().execute();
				    }
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
		    	if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}
		    	break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_K9");
				if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}
				break;
		    }
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_GENERIC");
				if(!setupGenericBundleNotifications(extrasBundle)){
					finishActivity();
					return;
				}
				break;
		    }
		    case Constants.NOTIFICATION_TYPE_PREVIEW_PHONE:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
		    	if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_PREVIEW_SMS:{
			    if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			    if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_PREVIEW_CALENDAR:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
		    	if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}
		    	break;
			}
			case Constants.NOTIFICATION_TYPE_PREVIEW_K9:{
				if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_K9");
				if(!setupBundleNotifications(extrasBundle, true, true)){
					finishActivity();
					return;
				}
				break;
		    }
			default:{
				finishActivity();
				return;
			}
	    }
	    Common.acquireKeyguardLock(_context, this);
	    setScreenTimeoutAlarm();
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause(){
	    if(_debug) Log.v(_context, "NotificationActivity.onPause()");
	    if(_tts != null){
	    	_tts.stop();
	    }
	    super.onPause();
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop(){
	    if(_debug) Log.v(_context, "NotificationActivity.onStop()");
    	if(_preferences.getBoolean(Constants.APPLICATION_CLOSE_WHEN_PUSHED_TO_BACKGROUND_KEY, false)){
    		if(_preferences.getBoolean(Constants.IGNORE_LINKED_APPS_WHEN_PUSHED_TO_BACKGROUND_KEY, false)){
    	    	finishActivity();
    	    }else{
    	    	if(!Common.isUserInLinkedApp(_context)){
    		    	finishActivity();
    		    }
    	    }
    	}
	    super.onStop();
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy(){
	    if(_debug) Log.v(_context, "NotificationActivity.onDestroy()");
	    if(_tts != null){
	    	_tts.shutdown();
	    }
		if(_preferences.getBoolean(Constants.CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY, false)){
			Common.clearAllNotifications(_context);
		}
	    cancelScreenTimeout();
	    Common.setInLinkedAppFlag(_context, false);
	    Common.clearKeyguardLock(this);
	    Common.clearWakeLock();
	    super.onDestroy();
	}

    /**
     * This is called when the activity is running and it is triggered and run again for a different notification.
     * This is a copy of the onCreate() method but without the initialization calls.
     * 
     * @param intent - Activity intent.
     */
	@Override
	protected void onNewIntent(Intent intent){
	    super.onNewIntent(intent);
	    if(_debug) Log.v(_context, "NotificationActivity.onNewIntent()");
	    //Resend/Reschedule incoming notification. Fix for !@#$# Home Key Pressed action. 
	    //This is needed when there is only a single notification and it was removed prior to this method being called.
	    if(_notificationViewFlipper.getTotalNotifications() == 0){
	    	Common.resendNotification(_context, intent);
	    }
	    Common.setInLinkedAppFlag(_context, false);
	    setIntent(intent);
	    final Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    //Add conversion from Preview types to normal types.
	    if(notificationType > 1999){
	    	notificationType -= 2000;
	    }
	    switch(notificationType){
	    	case Constants.NOTIFICATION_TYPE_PHONE:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_PHONE");
		    	setupBundleNotifications(extrasBundle, true, true);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_SMS:{
			    if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_SMS");
			    setupBundleNotifications(extrasBundle, true, true);
				if(_preferences.getBoolean(Constants.SMS_DISPLAY_UNREAD_KEY, false)){
					if(_notificationViewFlipper.getSMSCount() <= 1){
						new getAllUnreadSMSMessagesAsyncTask().execute();
					}
			    }
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_MMS:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_MMS");
		    	setupBundleNotifications(extrasBundle, true, true);
				if(_preferences.getBoolean(Constants.MMS_DISPLAY_UNREAD_KEY, false)){
					if(_notificationViewFlipper.getMMSCount() <= 1){			
						new getAllUnreadMMSMessagesAsyncTask().execute();
					}
			    }
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_CALENDAR");
			    setupBundleNotifications(extrasBundle, true, true);
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_K9:{
				if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_K9");
				setupBundleNotifications(extrasBundle, true, true);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				if(_debug) Log.v(_context, "NotificationActivity.onCreate() NOTIFICATION_TYPE_GENERIC");
				setupGenericBundleNotifications(extrasBundle);
				break;
		    }
	    	case Constants.NOTIFICATION_TYPE_PREVIEW_PHONE:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_PHONE");
		    	setupBundleNotifications(extrasBundle, true, true);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_PREVIEW_SMS:{
			    if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_SMS");
			    setupBundleNotifications(extrasBundle, true, true);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_PREVIEW_CALENDAR:{
		    	if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_CALENDAR");
			    setupBundleNotifications(extrasBundle, true, true);
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_PREVIEW_K9:{
				if(_debug) Log.v(_context, "NotificationActivity.onNewIntent() NOTIFICATION_TYPE_K9");
				setupBundleNotifications(extrasBundle, true, true);
				break;
		    }
	    }
	    Common.acquireKeyguardLock(_context, this);
	    setScreenTimeoutAlarm();
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Customized activity finish.
	 * This closes this activity screen.
	 */
	public void finishActivity(){
		if(_tts != null){
	    	_tts.shutdown();
	    }
		if(_preferences.getBoolean(Constants.CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY, false)){
			Common.clearAllNotifications(_context);
		}
	    cancelScreenTimeout();
	    Common.setInLinkedAppFlag(_context, false);
	    Common.clearKeyguardLock(this);
	    Common.clearWakeLock();
	    //Finish the activity.
	    finish();
	}
	
	/**
	 * Set up the ViewFlipper elements.
	 * 
	 * @param notificationType - Notification type.
	 */ 
	private void setupViews(int notificationType){
		_notificationViewFlipper = (NotificationViewFlipper) findViewById(R.id.notification_view_flipper);
	}
	
	/**
	 * Setup custom style elements of the ViewFlipper.
	 */
	private void setupViewFlipperStyles(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		String verticalLocation = _preferences.getString(Constants.POPUP_VERTICAL_LOCATION_KEY, Constants.POPUP_VERTICAL_LOCATION_DEFAULT);
		if(verticalLocation.equals(Constants.POPUP_VERTICAL_LOCATION_TOP)){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}else if(verticalLocation.equals(Constants.POPUP_VERTICAL_LOCATION_BOTTOM)){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}else{			
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		}
		_notificationViewFlipper.setLayoutParams(layoutParams);   	
	}
	
	/**
	 * Launches the preferences screen as new intent.
	 */
	private void launchPreferenceScreen(){
		Context context = getApplicationContext();
		Intent intent = new Intent(context, PreferencesActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Send a text message using any android messaging app.
	 * 
	 * @param phoneNumber - The phone number we want to send a text message to.
	 * 
	 * @return boolean - Returns true if the activity was started.
	 */
	private boolean sendSMSMessage(String phoneNumber){
		Notification notification = _notificationViewFlipper.getActiveNotification();
		return SMSCommon.startMessagingAppReplyActivity(_context, this, phoneNumber, notification.getMessageID(), notification.getThreadID(), Constants.SEND_SMS_ACTIVITY, notification.getNotificationType());
	}
	
	/**
	 * Place a phone call.
	 * 
	 * @param phoneNumber - The phone number we want to send a place a call to.
	 * 
	 * @return boolean - Returns true if the activity was started.
	 */
	private boolean makePhoneCall(String phoneNumber){
		return PhoneCommon.makePhoneCall(_context, this, phoneNumber, Constants.CALL_ACTIVITY);
	}	
	
	/**
	 * Get all the phone numbers associated with the incoming notification's contact.
	 * 
	 * @param notification - The incoming notification.
	 * 
	 * @return String[] - Array of phone numbers for this contact. Returns null if no numbers are found.
	 */
	private String[] getPhoneNumbers(Notification notification){
		return ContactsCommon.getContactPhoneNumbers(_context, notification);
	}

	/**
	 * Get unread SMS messages in the background.
	 * 
	 * @author Camille S�vigny
	 */
	private class getAllUnreadSMSMessagesAsyncTask extends AsyncTask<String, Void, Bundle>{
	    
		/**
	     * Do this work in the background.
	     * 
	     * @param params - The contact's id.
	     */
	    protected Bundle doInBackground(String... params){
			if(_debug) Log.v(_context, "NotificationActivity.getAllUnreadSMSMessagesAsyncTask.doInBackground()");
			try{
				return SMSCommon.getAllUnreadSMSMessages(_context);
			}catch(Exception ex){
				Log.e(_context, "NotificationActivity.getAllUnreadSMSMessagesAsyncTask.doInBackground() ERROR: " + ex.toString());
				return null;
			}
	    }
	    
	    /**
	     * Set the image to the notification View.
	     * 
	     * @param result - The image of the contact.
	     */
	    protected void onPostExecute(Bundle smsNotificationBundle){
			if(_debug) Log.v(_context, "NotificationActivity.getAllUnreadSMSMessagesAsyncTask.onPostExecute()");	
			if(smsNotificationBundle != null){
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_SMS);
				bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, smsNotificationBundle);
				setupBundleNotifications(bundle, false, false);
			}
	    }
	}

	/**
	 * Get unread MMS messages in the background.
	 * 
	 * @author Camille S�vigny
	 */
	private class getAllUnreadMMSMessagesAsyncTask extends AsyncTask<Void, Void, Bundle>{
	    
		/**
	     * Do this work in the background.
	     * 
	     * @param params - The contact's id.
	     */
	    protected Bundle doInBackground(Void...params){
	    	return SMSCommon.getAllUnreadMMSMessages(_context);
	    }
	    
	    /**
	     * Set the image to the notification View.
	     * 
	     * @param result - The image of the contact.
	     */
	    protected void onPostExecute(Bundle mmsNotificationBundle){
			if(mmsNotificationBundle != null){
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_SMS);
				bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, mmsNotificationBundle);
				setupBundleNotifications(bundle, false, false);
			}
	    }
	}
	
	/**
	 * Setup the bundle notification.
	 * 
	 * @param bundle - Activity bundle.
	 * @param postStatusBarnotification - Boolean telling us to post a new status bar notification.
	 * @param isNew - This notification is new and not a re-loaded/unread older notification.
	 * 
	 * @return boolean - Returns true if the method did not encounter an error.
	 */
	private boolean setupBundleNotifications(Bundle bundle, boolean postStatusBarnotification, boolean isNew){
		if(_debug) Log.v(_context, "NotificationActivity.setupBundleNotifications()");
		try{
			Bundle notificationBundle = bundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
			if(notificationBundle == null){
				if(_debug) Log.v(_context, "NotificationActivity.setupBundleNotifications() Bundle is null. Exiting..."); 
				return false;
			}
			//Loop through all the bundles that were sent through.
			int bundleCount = notificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, -1);
			if(bundleCount <= 0){
				if(_debug) Log.e(_context, "NotificationActivity.setupBundleNotifications() Bundle does not contain a notification! BundleCount = " + bundleCount);
				return false;
			}
			boolean displayPopup = !Common.restrictPopup(_context);
			for(int i=1;i<=bundleCount;i++){
				Bundle notificationBundleSingle = notificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				Notification notification = new Notification(_context, notificationBundleSingle);				
				//Only display the notification popup window if not in restrict mode.
				if(displayPopup){
					//Add the Notification to the ViewFlipper.
					_notificationViewFlipper.addNotification(notification, isNew);
				}
				//Display Status Bar Notification
			    if(postStatusBarnotification){
			    	int notificationType = notification.getNotificationType();
			    	notification.postStatusBarNotification(getNotificationTypeCount(notificationType, notification.getNotificationSubType()), Common.getStatusBarNotificationBundle(_context, notificationType));
			    }
			}
			return displayPopup;
		}catch(Exception ex){
			Log.e(_context, "NotificationActivity.setupBundleNotifications() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Setup the generic bundle notification.
	 * 
	 * @param bundle - Activity bundle.
	 * 
	 * @return boolean - Returns true if a popup window was displayed.
	 */
	private boolean setupGenericBundleNotifications(Bundle bundle){
		if(_debug) Log.v(_context, "NotificationActivity.setupGenericBundleNotifications()");
		try{
			if(bundle == null){
				if(_debug) Log.v(_context, "NotificationActivity.setupGenericBundleNotifications() Bundle is null. Exiting..."); 
				return false;
			}
			boolean displayPopup = !Common.restrictPopup(_context);				
			//Only display the notification popup window if not in restrict mode.
			if(displayPopup){
				Notification notification = new Notification(_context, bundle);
				//Add the Notification to the ViewFlipper.
				_notificationViewFlipper.addNotification(notification, true);
				//Display Status Bar Notification
				notification.postStatusBarNotification(getNotificationTypeCount(notification.getNotificationType(), notification.getNotificationSubType()), bundle);
			}
			return displayPopup;
		}catch(Exception ex){
			if(_debug) Log.v(_context, "NotificationActivity.setupGenericBundleNotifications() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Setup Activity's context menus.
	 * 
	 * @param contextMenu - The context menu item.
	 * @param notificationType - The notification type to customize what is shown.
	 */
	private void setupContextMenus(ContextMenu contextMenu, int notificationType){
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_SMS:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
				MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_MMS:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				MenuItem addContactMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
				addContactMenuItem.setVisible(false);
				MenuItem editContactMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
				editContactMenuItem.setVisible(false);
		    	MenuItem viewContactMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
		    	viewContactMenuItem.setVisible(false);
				MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_K9:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				MenuItem addContactMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
				addContactMenuItem.setVisible(false);
				MenuItem editContactMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
				editContactMenuItem.setVisible(false);
		    	MenuItem viewContactMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
		    	viewContactMenuItem.setVisible(false);
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
				MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				break;
		    }
			default:{
				break;
			}
		}
	}
	
	/**
	 * Cancel the screen timeout alarm.
	 */
	private void cancelScreenTimeout(){
		if(_screenTimeoutPendingIntent != null){
	    	AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
	    	alarmManager.cancel(_screenTimeoutPendingIntent);
	    	_screenTimeoutPendingIntent.cancel();
	    	_screenTimeoutPendingIntent = null;
		}
	}
	
	/**
	 * Set up the phone for TTS.
	 */
	private void setupTextToSpeech(){
		Intent intent = new Intent();
		intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(intent, Constants.TEXT_TO_SPEECH_ACTIVITY);
	}
	
	/**
	 * The Android text-to-speech library OnInitListener
	 */
	private final OnInitListener ttsOnInitListener = new OnInitListener(){
		public void onInit(int status){		
			if(status == TextToSpeech.SUCCESS){
				Notification notification = _notificationViewFlipper.getActiveNotification();
				notification.speak(_tts);
			}else{
				Toast.makeText(_context, R.string.app_tts_error, Toast.LENGTH_LONG).show();
			}
    	}
  	};
  	
  	/**
  	 * Get the number of notifications of a certain type.
  	 * 
  	 * @param notificationType - The notification type.
  	 * @param notificationSubType - The notification sub type.
  	 * 
  	 * @return int - The number of notifications of this type in the ViewFlipper.
  	 */
  	private int getNotificationTypeCount(int notificationType, int notificationSubType){
		//Adjust for Preview notifications.
		if(notificationType > 1999){
			notificationType -= 2000;
		}
		int notificationCount = 0;
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				notificationCount = _notificationViewFlipper.getMissedCallCount();
				break;
		    }
			case Constants.NOTIFICATION_TYPE_SMS:{
				notificationCount = _notificationViewFlipper.getSMSCount();
				break;
		    }
			case Constants.NOTIFICATION_TYPE_MMS:{
				notificationCount = _notificationViewFlipper.getMMSCount();
				break;
		    }
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				notificationCount = _notificationViewFlipper.getCalendarCount();
				break;
		    }
			case Constants.NOTIFICATION_TYPE_K9:{
				notificationCount = _notificationViewFlipper.getK9Count();
				break;
		    }
			default:{
				notificationCount = 1;
				break;
			}
		}
		if(notificationCount == 0) notificationCount = 1;
		return notificationCount;
  	}
  	
    /*
     * Dismiss all notifications asynchronous task.
     */
    private class DismissAllNotificationsTask extends AsyncTask<Void, Void, Boolean>{
    	
    	//ProgressDialog to display while the task is running.
    	private ProgressDialog dialog;
    	
        /**
         * Do this work before the background task starts.
         */  	
        @Override
        protected void onPreExecute(){
        	dialog = ProgressDialog.show(NotificationActivity.this, "", _context.getString(R.string.dismiss_all_loading_text), true);
        }
        
	    /**
	     * Do this work in the background.
	     */
        @Override
        protected Boolean doInBackground(Void... params){
  			_notificationViewFlipper.dismissAllNotifications();
        	return true;
        }

	    /**
	     * Do this work after the background has finished.
	     */
        @Override
        protected void onPostExecute(Boolean result){
        	dialog.dismiss();			
			finishActivity();
        }
        
    }
  	
    /*
     * Set the screen tineout asynchronous task.
     */
    private class SetScreenTimeoutTask extends AsyncTask<Void, Void, Boolean>{
        
	    /**
	     * Do this work in the background.
	     */
        @Override
        protected Boolean doInBackground(Void... params){
    		long scheduledAlarmTime = System.currentTimeMillis() + (Long.parseLong(_preferences.getString(Constants.SCREEN_TIMEOUT_KEY, "300")) * 1000);
    		AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        	Intent intent = new Intent(_context, ScreenManagementAlarmReceiver.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        	_screenTimeoutPendingIntent = PendingIntent.getBroadcast(_context, 0, intent, 0);
    		alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledAlarmTime, _screenTimeoutPendingIntent);
        	return true;
        }

	    /**
	     * Do this work after the background task has finished.
	     */
        @Override
        protected void onPostExecute(Boolean result){

        }
        
    }  
	
}