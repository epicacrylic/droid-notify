package apps.droidnotify;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;

/**
 * This is the Notification class that holds all the information about all notifications we will display to the user.
 * 
 * @author Camille S�vigny
 */
public class Notification {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private final String SMS_DISMISS_KEY = "sms_dismiss_button_action";
	private final String MMS_DISMISS_KEY = "mms_dismiss_button_action";
	private final String MISSED_CALL_DISMISS_KEY = "missed_call_dismiss_button_action";
	private final String SMS_DELETE_KEY = "sms_delete_button_action";
	private final String MMS_DELETE_KEY = "mms_delete_button_action";
	private final String SMS_DISMISS_ACTION_MARK_READ = "0";
	private final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String MMS_DISMISS_ACTION_MARK_READ = "0";
	private final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String MISSED_CALL_DISMISS_ACTION_MARK_READ = "0";
	private final String MISSED_CALL_DISMISS_ACTION_DELETE = "1";
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug;
	private Context _context;
	private String _phoneNumber;
	private String _addressBookPhoneNumber;
	private String _messageBody;
	private long _timeStamp;
	private long _threadID;
	private long _contactID;
	private String _contactLookupKey;
	private String _contactName;
	private long _photoID;
	private Bitmap _photoImg;
	private int _notificationType;
	private long _messageID;
	private boolean _fromEmailGateway;
	private String _serviceCenterAddress ;
	private MessageClass _messageClass;
	private boolean _contactExists;
	private boolean _contactPhotoExists;
	private String _title;
	private String _email;
	private long _calendarID;
	private long _calendarEventID;
	private long _calendarEventStartTime;
	private long _calendarEventEndTime;
	private boolean _allDay;
	
	//================================================================================
	// Constructors
	//================================================================================
  
	/**
	 * Class Constructor
	 * This constructor should be called for SMS & MMS Messages.
	 */
	public Notification(Context context, Bundle bundle, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, Bundle bundle, int notificationType)");
		setContext(context);
		setContactExists(false);
		SmsMessage[] msgs = null;
        String messageBody = "";            
        if (bundle != null){
        	setNotificationType(notificationType);
        	if(notificationType == NOTIFICATION_TYPE_PHONE){
        		//Do Nothing. This should not be called if a missed call is received.
    	    }
        	if(notificationType == NOTIFICATION_TYPE_SMS){
        		// Retrieve SMS message from bundle.
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length]; 
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	            }
	            SmsMessage sms = msgs[0];
	            long timeStamp = sms.getTimestampMillis();
	            //Adjust the timestamp to the localized time of the users phone.
	            //I don't know why the line below is "-=" and not "+=" but for some reason it works.
	            timeStamp -= TimeZone.getDefault().getOffset(timeStamp);
	            setTimeStamp(timeStamp);
	            String phoneNumber = sms.getDisplayOriginatingAddress();
	    		setPhoneNumber(phoneNumber);
	    		setFromEmailGateway(sms.isEmail());
	    		setServiceCenterAddress(sms.getServiceCenterAddress());
	    		setMessageClass(sms.getMessageClass());
	    		setTitle("SMS Message");
	            //Get the entire message body from the new message.
	            for (int i=0; i<msgs.length; i++){                
	                messageBody += msgs[i].getMessageBody().toString();
	            }
	            if(messageBody.startsWith(phoneNumber)){
	            	messageBody = messageBody.substring(phoneNumber.length());
	            }	            
	            messageBody = messageBody.trim();
	            setMessageBody(messageBody);
	    		loadThreadID(context, phoneNumber);
	    		long threadID = getThreadID();
	    		if(sms.getServiceCenterAddress() == null){
	    			loadServiceCenterAddress(context, threadID);
	    		}
	    		loadMessageID(context, threadID, messageBody, timeStamp);
	    		loadContactsInfoByPhoneNumber(context, phoneNumber);
	    		//Search by email if we can't find the contact info.
	    		if(!getContactExists()){
	    			loadContactsInfoByEmail(context, phoneNumber);
	    		}
        	}
    	    if(notificationType == NOTIFICATION_TYPE_MMS){
    	    	setTitle("MMS Message");
    	    	//TODO - MMS
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
    	    	//Do Nothing. This should not be called if a calendar event is received.
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
    	    	//Do Nothing. This should not be called if an email is received.
    	    }
        }
	}

	/**
	 * Class Constructor
	 * This constructor should be called for SMS & MMS Messages.
	 */
	public Notification(Context context, long messageID, long threadID, String messageBody, String phoneNumber, long timeStamp, long contactID, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, long messageID, long threadID, String messageBody, String phoneNumber, long timeStamp, long contactID, int notificationType)");
		setTitle("SMS Message");
		setContext(context);
		setMessageID(messageID);
		setThreadID(threadID);
		setMessageBody(messageBody);
		setPhoneNumber(phoneNumber);
        setTimeStamp(timeStamp);
        setContactID(contactID);
		setFromEmailGateway(false);
		setMessageClass(MessageClass.CLASS_0);
		setContactExists(false);
		loadContactsInfoByPhoneNumber(context, phoneNumber);   
		setNotificationType(notificationType);
	}
	
	/**
	 * Class Constructor
	 * This constructor should be called for TEST SMS & MMS Messages.
	 */
	public Notification(Context context, String phoneNumber, String messageBody, long timeStamp, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String phoneNumber, String messageBody, long timeStamp, int notificationType)");
		setTitle("SMS Message");
		setContext(context);
		setNotificationType(notificationType);
		setContactExists(false);
        setTimeStamp(timeStamp);
		setPhoneNumber(phoneNumber);
		setFromEmailGateway(false);
		setMessageClass(MessageClass.CLASS_0);
        setMessageBody(messageBody);
	}
	
	/**
	 * Class Constructor
	 * This constructor should be called for Missed Calls.
	 */
	public Notification(Context context, String phoneNumber, long timeStamp, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String phoneNumber, long timeStamp, int notificationType)");
		setContext(context);
		setContactExists(false);
		setContactPhotoExists(false);
		setNotificationType(notificationType);
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		setPhoneNumber(phoneNumber);
    		setTimeStamp(timeStamp);
      		setTitle("Missed Call");
      		//Don't load contact info if this is a test message (Phone Number: 555-555-5555).
    		if(!phoneNumber.equals("5555555555")){
    			loadContactsInfoByPhoneNumber(context, phoneNumber);
    		}
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
    		//Do Nothing. This should not be called if a SMS or MMS is received.
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//Do Nothing. This should not be called if a calendar event is received.
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//Do Nothing. This should not be called if an email is received.
	    }
	}

	/**
	 * Class Constructor
	 * This constructor should be called for Calendar Events.
	 */
	public Notification(Context context, String title, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, long calendarID, long calendarEventID, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String title, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, long calendarID, long calendarEventID, int notificationType)");
		setContext(context);
		setContactExists(false);
		setContactPhotoExists(false);
		setNotificationType(notificationType);
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		//Do Nothing. This should not be called if a missed call is received.
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS){
    		if (_debug) Log.v("Notification.Notification() NOTIFICATION_TYPE_SMS");
    		//Do Nothing. This should not be called if a SMS is received.
    	}   		
    	if(notificationType == NOTIFICATION_TYPE_MMS){
    		if (_debug) Log.v("Notification.Notification() NOTIFICATION_TYPE_MMS");
    		//Do Nothing. This should not be called if an MMS is received.
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	setTimeStamp(eventStartTime);
	    	setTitle(title);
	    	setAllDay(allDay);
	    	setMessageBody(formatCalendarEventMessage(messageBody, eventStartTime, eventEndTime, allDay));
	    	setCalendarID(calendarID);
	    	setCalendarEventID(calendarEventID);
	    	setCalendarEventStartTime(eventStartTime);
	    	setCalendarEventEndTime(eventEndTime);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//Do Nothing. This should not be called if an email is received.
	    }
	}
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 * 
	 * @param context - Applications Context.
	 */
	public void setContext(Context context) {
		if (_debug) Log.v("Notification.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return context - Applications Context.
	 */
	public Context getContext() {
		if (_debug) Log.v("Notification.getContext()");
	    return _context;
	}

	/**
	 * Set the addressBookPhoneNumber property.
	 * 
	 * @param addressBookPhoneNumber - Phone's contact phone number.
	 */
	public void setAddressBookPhoneNumber(String addressBookPhoneNumber) {
		if (_debug) Log.v("Notification.setAddressBookPhoneNumber() PhoneNumber: " + addressBookPhoneNumber);
		_addressBookPhoneNumber = addressBookPhoneNumber;
	}
	
	/**
	 * Get the addressBookPhoneNumber property.
	 * 
	 * @return addressBookPhoneNumber - Phone's contact phone number or stored phone number if not available.
	 */
	public String getAddressBookPhoneNumber() {
		if (_debug) Log.v("Notification.getAddressBookPhoneNumber()");
		if(_addressBookPhoneNumber == null){
			loadContactsInfoByPhoneNumber(getContext(),getPhoneNumber());
		}
		if(_addressBookPhoneNumber == null){
			return _phoneNumber;
		}
		return _addressBookPhoneNumber;
	}

	/**
	 * Set the phoneNumber property.
	 * 
	 * @param phoneNumber - Contact's phone number.
	 */
	public void setPhoneNumber(String phoneNumber) {
		if (_debug) Log.v("Notification.setPhoneNumber() PhoneNumber: " + phoneNumber);
		_phoneNumber = phoneNumber;
	}
	
	/**
	 * Get the phoneNumber property.
	 * 
	 * @return phoneNumber - Contact's phone number.
	 */
	public String getPhoneNumber() {
		if (_debug) Log.v("Notification.getPhoneNumber()");
		return _phoneNumber;
	}
	
	/**
	 * Set the messageBody property.
	 * 
	 * @param messageBody - Notification's message.
	 */
	public void setMessageBody(String messageBody) {
		if (_debug) Log.v("Notification.setMessageBody() MessageBody: " + messageBody);
		_messageBody = messageBody;
	}
	
	/**
	 * Get the messageBody property.
	 * 
	 * @return messageBody - Notification's message.
	 */
	public String getMessageBody() {
		if (_debug) Log.v("Notification.getMessageBody()");
		if (_messageBody == null) {
			_messageBody = "";
	    }
	    return _messageBody;
	}

	/**
	 * Set the timeStamp property.
	 * 
	 * @param timeStamp - TimeStamp of notification.
	 */
	public void setTimeStamp(long timeStamp) {
		if (_debug) Log.v("Notification.setTimeStamp() TimeStamp: " + timeStamp);
	    _timeStamp = timeStamp;
	}
	
	/**
	 * Get the timeStamp property.
	 * 
	 * @return timeStamp - TimeStamp of notification.
	 */
	public long getTimeStamp() {
		if (_debug) Log.v("Notification.getTimeStamp()");
	    return _timeStamp;
	}
	
	/**
	 * Set the threadID property.
	 * 
	 * @param threadID - SMS/MMS Message thread id.
	 */
	public void setThreadID(long threadID) {
		if (_debug) Log.v("Notification.setThreadID() ThreadID: " + threadID);
	    _threadID = threadID;
	}
	
	/**
	 * Get the threadID property.
	 * 
	 * @return threadID - SMS/MMS Message thread id.
	 */
	public long getThreadID() {
		if(_threadID == 0){
			loadThreadID(getContext(), getPhoneNumber());
		}
		if (_debug) Log.v("Notification.getThreadID() ThreadID: " + _threadID);
	    return _threadID;
	}	
	
	/**
	 * Set the contactID property.
	 * 
	 * @param contactID - Contact's ID.
	 */
	public void setContactID(long contactID) {
		if (_debug) Log.v("Notification.setContactID() ContactID: " + contactID);
	    _contactID = contactID;
	}
	
	/**
	 * Get the contactID property.
	 * 
	 * @return contactID - Contact's ID.
	 */
	public long getContactID() {
		if (_debug) Log.v("Notification.getContactID()");
		if(_contactID == 0){
			loadContactsInfoByPhoneNumber(getContext(),getPhoneNumber());
		}
	    return _contactID;
	}
	
	/**
	 * Set the contactLookupKey property.
	 * 
	 * @param contactLookupKey - Contact's lookup key.
	 */
	public void setContactLookupKey(String contactLookupKey) {
		if (_debug) Log.v("Notification.setContactLookupKey() ContactLookupKey: " + contactLookupKey);
		_contactLookupKey = contactLookupKey;
	}
	
	/**
	 * Get the contactLookupKey property.
	 * 
	 * @return contactLookupKey - Contact's lookup key.
	 */
	public String getContactLookupKey() {
		if (_debug) Log.v("Notification.getContactLookupKey()");
		if(_contactLookupKey == null){
			loadContactsInfoByPhoneNumber(getContext(),getPhoneNumber());
		}
	    return _contactLookupKey;
	}	

	/**
	 * Set the contactName property.
	 * 
	 * @param contactName - Contact's display name.
	 */
	public void setContactName(String contactName) {
		if (_debug) Log.v("Notification.setContactName() ContactName: " + contactName);
		_contactName = contactName;
	}
	
	/**
	 * Get the contactName property.
	 * 
	 * @return contactName - Contact's display name.
	 */
	public String getContactName() {
		if (_debug) Log.v("Notification.getContactName()");
		if (_contactName == null) {
			_contactName = _context.getString(android.R.string.unknownName);
	    }
		return _contactName;
	}

	/**
	 * Set the photoID property.
	 * 
	 * @param photoID - Contact's photo id.
	 */
	public void setPhotoID(long photoID) {
		if (_debug) Log.v("Notification.setPhotoID() PhotoID: " + photoID);
		_photoID = photoID;
	}
	
	/**
	 * Get the photoID property.
	 * 
	 * @return photoID - Contact's photo id.
	 */
	public long getPhotoID() {
		if (_debug) Log.v("Notification.getPhotoID()");
		return _photoID;
	}

	/**
	 * Set the photoImg property.
	 * 
	 * @param photoImg - Bitmap of contact's photo.
	 */
	public void setPhotoImg(Bitmap photoImg) {
		if (_debug) Log.v("Notification.setPhotoID() PhotoIImg: " + photoImg);
		_photoImg = photoImg;
	}
	
	/**
	 * Get the photoIImg property.
	 * 
	 * @return photoImg - Bitmap of contact's photo.
	 */
	public Bitmap getPhotoImg() {
		if (_debug) Log.v("Notification.getPhotoIImg()");
		return _photoImg;
	}
	
	/**
	 * Set the notificationType property.
	 * 
	 * @param notificationType - The type of notification this is.
	 */
	public void setNotificationType(int notificationType) {
		if (_debug) Log.v("Notification.setNotificationType() NotificationType: " + notificationType);
		_notificationType = notificationType;
	}
	
	/**
	 * Get the notificationType property.
	 * 
	 * @return notificationType - The type of notification this is.
	 */
	public int getNotificationType() {
		if (_debug) Log.v("Notification.getNotificationType()");
		return _notificationType;
	}

	/**
	 * Set the messageID property.
	 * 
	 * @param messageID - The message id of the SMS/MMS message.
	 */
	public void setMessageID(long messageID) {
		if (_debug) Log.v("Notification.setMessageID() MessageID: " + messageID);
  		_messageID = messageID;
	}
	
	/**
	 * Get the messageID property.
	 * 
	 * @return messageID - The message id of the SMS/MMS message.
	 */
	public long getMessageID() {
		if (_debug) Log.v("Notification.getMessageID()");
		if(_messageID == 0){
			loadMessageID(getContext(), getThreadID(), getMessageBody(), getTimeStamp());
		}
  		return _messageID;
	}

	/**
	 * Set the fromEmailGateway property.
	 * 
	 * @param fromEmailGateway - Boolean which is true if the message came from an email gateway.
	 */
	public void setFromEmailGateway(boolean fromEmailGateway) {
		if (_debug) Log.v("Notification.setFromEmailGateway() FromEmailGateway: " + fromEmailGateway);
  		_fromEmailGateway = fromEmailGateway;
	}
	
	/**
	 * Get the fromEmailGateway property.
	 * 
	 * @return fromEmailGateway - Boolean which returns true if the message came from an email gateway.
	 */
	public boolean setFromEmailGateway() {
		if (_debug) Log.v("Notification.getFromEmailGateway()");
  		return _fromEmailGateway;
	}	

	/**
	 * Set the serviceCenterAddress property.
	 * 
	 * @param serviceCenterAddress - The SMS/MMS service center address.
	 */
	public void setServiceCenterAddress(String serviceCenterAddress) {
		if (_debug) Log.v("Notification.setServiceCenterAddress() ServiceCenterAddress: " + serviceCenterAddress);
		_serviceCenterAddress = serviceCenterAddress;
	}
	
	/**
	 * Get the serviceCenterAddress property.
	 * 
	 * @return serviceCenterAddress - The SMS/MMS service center address.
	 */
	public String getServiceCenterAddress() {
		if (_debug) Log.v("Notification.getServiceCenterAddress() ServiceCenterAddress: " + _serviceCenterAddress);
		if(_serviceCenterAddress == null){
			loadServiceCenterAddress(getContext(), getThreadID());
		}
  		return _serviceCenterAddress;
	}
	
	/**
	 * Set the messageClass property.
	 * 
	 * @param messageClass - The message class of the SMS/MMS message.
	 */
	public void setMessageClass(MessageClass messageClass) {
		if (_debug) Log.v("Notification.setMessageClass()");
		_messageClass = messageClass;
	}
	
	/**
	 * Get the messageClass property.
	 * 
	 * @return messageClass - The message class of the SMS/MMS message.
	 */
	public MessageClass getMessageClass() {
		if (_debug) Log.v("Notification.getMessageClass()");
  		return _messageClass;
	}

	/**
	 * Set the contactExists property.
	 * 
	 * @param contactExists - Boolean which is true if there is a contact in the phone linked to this notification.
	 */
	public void setContactExists(boolean contactExists) {
		if (_debug) Log.v("Notification.setContactExists()");
		_contactExists = contactExists;
	}
	
	/**
	 * Get the contactExists property.
	 * 
	 * @return  contactExists - Boolean returns true if there is a contact in the phone linked to this notification.
	 */
	public boolean getContactExists() {
		if (_debug) Log.v("Notification.getContactExists()");
  		return _contactExists;
	}

	/**
	 * Set the contactPhotoExists property.
	 * 
	 * @param contactPhotoExists - Boolean which is true if there is a contact photo in the phone linked to this notification.
	 */
	public void setContactPhotoExists(boolean contactPhotoExists) {
		if (_debug) Log.v("Notification.setContactPhotoExists()");
		_contactPhotoExists = contactPhotoExists;
	}
	
	/**
	 * Get the contactPhotoExists property.
	 * 
	 * @return contactPhotoExists - Boolean which is true if there is a contact photo in the phone linked to this notification.
	 */
	public boolean getContactPhotoExists() {
		if (_debug) Log.v("Notification.getContactPhotoExists()");
  		return _contactPhotoExists;
	}	

	/**
	 * Set the title property.
	 * 
	 * @param title - Notification title.
	 */
	public void setTitle(String title) {
		if (_debug) Log.v("Notification.setTitle() Title: " + title);
		_title = title;
	}
	
	/**
	 * Get the title property.
	 * 
	 * @return title - Notification title.
	 */
	public String getTitle() {
		if (_debug) Log.v("Notification.getTitle() Title: " + _title);
  		return _title;
	}

	/**
	 * Set the email property.
	 * 
	 * @param setEmail - Contact's email address.
	 */
	public void setEmail(String email) {
		if (_debug) Log.v("Notification.setEmail() Email: " + email);
		_email = email;
	}
	
	/**
	 * Get the email property.
	 * 
	 * @return setEmail - Contact's email address.
	 */
	public String getEmail() {
		if (_debug) Log.v("Notification.getEmail() Email: " + _email);
  		return _email;
	}

	/**
	 * Set the calendarID property.
	 * 
	 * @param calendarID - Calendar Event's id.
	 */
	public void setCalendarID(long calendarID) {
		if (_debug) Log.v("Notification.setCalendarID() CalendarID: " + calendarID);
		_calendarID = calendarID;
	}
	
	/**
	 * Get the calendarID property.
	 * 
	 * @return calendarID - Calendar Event's id.
	 */
	public long getCalendarID() {
		if (_debug) Log.v("Notification.getCalendarID() CalendarID: " + _calendarID);
  		return _calendarID;
	}

	/**
	 * Set the calendarEventID property.
	 * 
	 * @return calendarEventStartTime - Start time of the Calendar Event.
	 */
	public void setCalendarEventID(long calendarEventID) {
		if (_debug) Log.v("Notification.setCalendarEventID() CalendarEventID: " + calendarEventID);
		_calendarEventID = calendarEventID;
	}
	
	/**
	 * Get the calendarEventID property.
	 * 
	 * @param 
	 */
	public long getCalendarEventID() {
		if (_debug) Log.v("Notification.getCalendarEventID() CalendarEventID: " + _calendarEventID);
  		return _calendarEventID;
	}
	
	/**
	 * Set the calendarEventStartTime property.
	 * 
	 * @param calendarEventStartTime - Start time of the Calendar Event.
	 */
	public void setCalendarEventStartTime(long calendarEventStartTime) {
		if (_debug) Log.v("Notification.setCalendarEventStartTime() CalendarEventStartTime: " + calendarEventStartTime);
		_calendarEventStartTime = calendarEventStartTime;
	}
	
	/**
	 * Get the calendarEventStartTime property.
	 * 
	 * @return calendarEventStartTime - Start time of the Calendar Event.
	 */
	public long getCalendarEventStartTime() {
		if (_debug) Log.v("Notification.getCalendarEventStartTime() CalendarEventStartTime: " + _calendarEventStartTime);
  		return _calendarEventStartTime;
	}

	/**
	 * Set the calendarEventEndTime property.
	 * 
	 * @param calendarEventEndTime - End time of the Calendar Event.
	 */
	public void setCalendarEventEndTime(long calendarEventEndTime) {
		if (_debug) Log.v("Notification.setCalendarEventEndTime() CalendarEventEndTime: " + calendarEventEndTime);
		_calendarEventEndTime = calendarEventEndTime;
	}
	
	/**
	 * Get the calendarEventEndTime property.
	 * 
	 * @return calendarEventEndTime - End time of the Calendar Event.
	 */
	public long getCalendarEventEndTime() {
		if (_debug) Log.v("Notification.getCalendarEventEndTime() CalendarEventEndTime: " + _calendarEventEndTime);
  		return _calendarEventEndTime;
	}

	/**
	 * Set the allDay property.
	 * 
	 * @param allDay - Boolean which is true if the Calendar Event is all day.
	 */
	public void setAllDay(boolean allDay) {
		if (_debug) Log.v("Notification.setCalendarEventEndTime() AllDay: " + allDay);
		_allDay = allDay;
	}
	
	/**
	 * Get the allDay property.
	 * 
	 * @return allDay - Boolean returns true if the Calendar Event is all day.
	 */
	public boolean getAllDay() {
		if (_debug) Log.v("Notification.getCalendarEventEndTime() AllDay: " + _allDay);
  		return _allDay;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	/**
	 * Set this notification as being viewed on the users phone.
	 * 
	 * @param isViewed - Boolean value to set or unset the item as being viewed.
	 */
	public void setViewed(boolean isViewed){
		if (_debug) Log.v("Notification.setViewed()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int notificationType = getNotificationType();
		if (_debug) Log.v("Notification.setViewed() Preference Value: " + preferences.getString(MISSED_CALL_DISMISS_KEY, "0"));
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		//Action is determined by the users preferences. 
    		//Either mark the call log as viewed, delete the call log entry, or do nothing to the call log entry.
    		if(preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_MARK_READ)){
    			setCallViewed(isViewed);
    		}else if(preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_DELETE)){
    			deleteFromCallLog();
    		}
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(preferences.getString(SMS_DISMISS_KEY, "0").equals(SMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
    	if(getNotificationType() == NOTIFICATION_TYPE_MMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(preferences.getString(MMS_DISMISS_KEY, "0").equals(MMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//Do nothing. There is no log to update for Calendar Events.
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	setEmailRead(isViewed);
	    }
	}
	
	/**
	 * Delete the message or thread from the users phone.
	 */
	public void deleteMessage(){
		if (_debug) Log.v("Notification.deleteMessage()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int notificationType = getNotificationType();
		//Decide what to do here based on the users preferences.
		//Delete the single message, delete the entire thread, or do nothing.
		boolean deleteThread = false;
		boolean deleteMessage = false;
		if(notificationType == NOTIFICATION_TYPE_SMS){
			if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}else if(notificationType == NOTIFICATION_TYPE_MMS){
			if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}
		Context context = getContext();
		long threadID = getThreadID();
		if(threadID == 0){
			if (_debug) Log.v("Notification.deleteMessage() Thread ID == 0. Load Thread ID");
			loadThreadID(context, getPhoneNumber());
			threadID = getThreadID();
		}
		long messageID = getMessageID();
		if(messageID == 0){
			if (_debug) Log.v("Notification.deleteMessage() Message ID == 0. Load Message ID");
			loadMessageID(context, getThreadID(), getMessageBody(), getTimeStamp());
			messageID = getMessageID();
		}
		if(deleteMessage || deleteThread){
			if(deleteThread){
			try{
				//Delete entire SMS thread.
				context.getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + threadID), 
						null, 
						null);
				}catch(Exception ex){
					if (_debug) Log.e("Notification.deleteMessage() Delete Thread ERROR: " + ex.toString());
				}
			}else{
				try{
					//Delete single message.
					context.getContentResolver().delete(
							Uri.parse("content://sms/" + messageID),
							null, 
							null);
				}catch(Exception ex){
					if (_debug) Log.e("Notification.deleteMessageg() Delete Message ERROR: " + ex.toString());
				}
			}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Load the SMS/MMS thread id for this notification.
	 * 
	 * @param context - Application Context.
	 * @param phoneNumber - Notifications's phone number.
	 */
	private void loadThreadID(Context context, String phoneNumber){
		if (_debug) Log.v("Notification.getThreadIdByAddress()");
		if (phoneNumber == null){
			if (_debug) Log.v("Notification.loadThreadID() Phone number provided is NULL: Exiting loadThreadID()");
			return;
		}
		try{
			final String[] projection = new String[] { "_id", "thread_id" };
			final String selection = "address = " + DatabaseUtils.sqlEscapeString(phoneNumber);
			final String[] selectionArgs = null;
			final String sortOrder = null;
		    long threadID = 0;
		    Cursor cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    if (cursor != null) {
		    	try {
		    		if (cursor.moveToFirst()) {
		    			threadID = cursor.getLong(cursor.getColumnIndex("thread_id"));
		    			if (_debug) Log.v("Notification.loadThreadID() Thread ID Found: " + threadID);
		    		}
		    	}catch(Exception e){
			    		if (_debug) Log.e("Notification.loadThreadID() EXCEPTION: " + e.toString());
		    	} finally {
		    		cursor.close();
		    	}
		    }
		    setThreadID(threadID);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadThreadID() ERROR: " + ex.toString());
		}
	}

	/**
	 * Load the SMS/MMS message id for this notification.
	 * 
	 * @param context - Application Context.
	 * @param threadId - Notifications's threadID.
	 * @param timestamp - Notifications's timeStamp.
	 */
	public void loadMessageID(Context context, long threadID, String messageBody, long timeStamp) {
		if (_debug) Log.v("Notification.loadMessageID()");
		if (threadID == 0){
			if (_debug) Log.v("Notification.loadMessageID() Thread ID provided is NULL: Exiting loadMessageId()");
			return;
		}    
		if (messageBody == null){
			if (_debug) Log.v("Notification.loadMessageID() Message body provided is NULL: Exiting loadMessageId()");
			return;
		} 
		try{
			final String[] projection = new String[] { "_id, body"};
			final String selection = "thread_id = " + threadID ;
			final String[] selectionArgs = null;
			final String sortOrder = null;
			long messageID = 0;
		    Cursor cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    try{
			    while (cursor.moveToNext()) { 
		    		if(cursor.getString(cursor.getColumnIndex("body")).trim().equals(messageBody)){
		    			messageID = cursor.getLong(cursor.getColumnIndex("_id"));
		    			if (_debug) Log.v("Notification.loadMessageID() Message ID Found: " + messageID);
		    			break;
		    		}
			    }
		    }catch(Exception ex){
				if (_debug) Log.e("Notification.loadMessageID() ERROR: " + ex.toString());
			}finally{
		    	cursor.close();
		    }
		    setMessageID(messageID);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadMessageID() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Get the service center to use for a reply.
	 * 
	 * @param context
	 * @param threadID
	 * 
	 * @return String - The service center address of the message.
	 */
	private String loadServiceCenterAddress(Context context, long threadID) {
		if (_debug) Log.v("Notification.loadServiceCenterAddress()");
		if (threadID == 0){
			if (_debug) Log.v("Notification.loadServiceCenterAddress() Thread ID provided is NULL: Exiting loadServiceCenterAddress()");
			return null;
		} 
		try{
			final String[] projection = new String[] {"reply_path_present", "service_center"};
			//final String[] projection = null;
			final String selection = "thread_id = " + threadID ;
			final String[] selectionArgs = null;
			final String sortOrder = "date DESC";
			String serviceCenterAddress = null;
		    Cursor cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    try{
		    	
//		    	for(int i=0; i<cursor.getColumnCount(); i++){
//		    		if (_debug) Log.v("Notification.loadServiceCenterAddress() Cursor Column: " + cursor.getColumnName(i) + " Column Value: " + cursor.getString(i));
//		    	}
		    	
//		    	if ((cursor == null) || !cursor.moveToFirst()) {
//			        return null;
//			    }
		    	while (cursor.moveToNext()) { 
			    	serviceCenterAddress = cursor.getString(cursor.getColumnIndex("service_center"));
			    	if (_debug) Log.v("Notification.loadServiceCenterAddress() Reply Path Present: " + cursor.getString(cursor.getColumnIndex("reply_path_present")));
	    			if (_debug) Log.v("Notification.loadServiceCenterAddress() Service Center Address: " + serviceCenterAddress);
	    			if(serviceCenterAddress != null){
	    				return serviceCenterAddress;
	    			}
		    	}
		    }catch(Exception ex){
				if (_debug) Log.e("Notification.loadServiceCenterAddress() ERROR: " + ex.toString());
			}finally{
		    	cursor.close();
		    }
		    setServiceCenterAddress(serviceCenterAddress);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadServiceCenterAddress() ERROR: " + ex.toString());
		}	    
		return null;
	}	

	/**
	 * Load the various contact info for this notification from a phoneNumber.
	 * 
	 * @param context - Application Context.
	 * @param phoneNumber - Notifications's phone number.
	 */ 
	private void loadContactsInfoByPhoneNumber(Context context, String phoneNumber){
		if (_debug) Log.v("Notification.loadContactsInfo()");
		if (phoneNumber == null) {
			if (_debug) Log.v("Notification.loadContactsInfo() Phone number provided is NULL: Exiting...");
			return;
		}
		//Exit if the phone number is an email address.
		if (phoneNumber.contains("@")) {
			if (_debug) Log.v("Notification.loadContactsInfo() Phone number provided appears to be an email address: Exiting...");
			return;
		}
		try{
			PhoneNumber incomingNumber = new PhoneNumber(phoneNumber);
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (_debug) Log.v("Notification.loadContactsInfo() Searching contacts");
			while (cursor.moveToNext()) { 
				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactLookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				final String[] phoneProjection = null;
				final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
				final String[] phoneSelectionArgs = null;
				final String phoneSortOrder = null;
				Cursor phoneCursor = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						phoneProjection, 
						phoneSelection, 
						phoneSelectionArgs, 
						phoneSortOrder); 
				while (phoneCursor.moveToNext()) { 
					String addressBookPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					PhoneNumber contactNumber = new PhoneNumber(addressBookPhoneNumber);
					if(incomingNumber.getPhoneNumber().equals(contactNumber.getPhoneNumber())){
						setContactID(Long.parseLong(contactID));
						if(addressBookPhoneNumber != null){
		    			  	setAddressBookPhoneNumber(addressBookPhoneNumber);
		    		  	}
		    		  	if(contactLookupKey != null){
		    			  	setContactLookupKey(contactLookupKey);
		    		  	}
		    		  	if(contactName != null){
		    			  	setContactName(contactName);
		    		  	}
		    		  	if(photoID != null){
		    			  	setPhotoID(Long.parseLong(photoID));
		    		  	}
		  	          	Uri uri = ContentUris.withAppendedId(
		  	        		  ContactsContract.Contacts.CONTENT_URI,
		  	        		  Long.parseLong(contactID));
		  		      	InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
		  		      	Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
		  		      	if(contactPhotoBitmap!= null){
		  		    	  	setPhotoImg(contactPhotoBitmap);
		  		    	  	setContactPhotoExists(true);
		  		      	}
		  		      	setContactExists(true);
		  		      	break;
					}
				}
				phoneCursor.close(); 
				if(getContactExists()) break;
		   	}
			cursor.close();
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadContactsInfo() ERROR: " + ex.toString());
		}
	}

	/**
	 * Load the various contact info for this notification from an email.
	 * 
	 * @param context - Application Context.
	 * @param incomingEmail - Notifications's email address.
	 */ 
	private void loadContactsInfoByEmail(Context context, String incomingEmail){
		if (_debug) Log.v("Notification.loadContactsInfoByEmail()");
		if (incomingEmail == null) {
			if (_debug) Log.v("Notification.loadContactsInfoByEmail() Email provided is NULL: Exiting...");
			return;
		}
		try{
			final String[] projection = null;
			final String selection = null;
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (_debug) Log.v("Notification.loadContactsInfoByEmail() Searching contacts");
			while (cursor.moveToNext()) { 
				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactLookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				final String[] emailProjection = null;
				final String emailSelection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactID;
				final String[] emailSelectionArgs = null;
				final String emailSortOrder = null;
                Cursor emailCursor = context.getContentResolver().query(
                		ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
                		emailProjection,
                		emailSelection, 
                        emailSelectionArgs, 
                        emailSortOrder);
                while (emailCursor.moveToNext()) {
                	String contactEmail = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    //String emailType = emailCursor.getString(emailCursor.getColumnIndex(Phone.TYPE));
                    //if (_debug) Log.v("Notification.loadContactsInfoByEmail() Email Address: " + emailIdOfContact + " Email Type: " + emailType);
					if(incomingEmail.toLowerCase().equals(contactEmail.toLowerCase())){
						setContactID(Long.parseLong(contactID));
		    		  	if(contactLookupKey != null){
		    			  	setContactLookupKey(contactLookupKey);
		    		  	}
		    		  	if(contactName != null){
		    			  	setContactName(contactName);
		    		  	}
		    		  	if(photoID != null){
		    			  	setPhotoID(Long.parseLong(photoID));
		    		  	}
		  	          	Uri uri = ContentUris.withAppendedId(
		  	        		  ContactsContract.Contacts.CONTENT_URI,
		  	        		  Long.parseLong(contactID));
		  		      	InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
		  		      	Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
		  		      	if(contactPhotoBitmap!= null){
		  		    	  	setPhotoImg(contactPhotoBitmap);
		  		    	  	setContactPhotoExists(true);
		  		      	}
		  		      	setContactExists(true);
		  		      	break;
					}
                    
                }
                emailCursor.close();
                if(getContactExists()) break;
		   	}
			cursor.close();
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadContactsInfo() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Set the call log as viewed (not new) or new depending on the input.
	 * 
	 * @param isViewed - Boolean, if true sets the call log call as being viewed.
	 */
	private void setCallViewed(boolean isViewed){
		if (_debug) Log.v("Notification.setCallViewed()");
		Context context = getContext();
		String phoneNumber = getPhoneNumber();
		long timeStamp = getTimeStamp();
		ContentValues contentValues = new ContentValues();
		if(isViewed){
			contentValues.put(android.provider.CallLog.Calls.NEW, 0);
		}else{
			contentValues.put(android.provider.CallLog.Calls.NEW, 1);
		}
		String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
		String[] selectionArgs = new String[] {DatabaseUtils.sqlEscapeString(phoneNumber), Long.toString(timeStamp)};
		try{
			context.getContentResolver().update(
					Uri.parse("content://call_log/calls"),
					contentValues,
					selection, 
					selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.setCallViewed() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Delete the call log entry.
	 */
	private void deleteFromCallLog(){
		if (_debug) Log.v("Notification.deleteFromCallLog()");
		Context context = getContext();
		String phoneNumber = getPhoneNumber();
		long timeStamp = getTimeStamp();
		String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
		String[] selectionArgs = new String[] {DatabaseUtils.sqlEscapeString(phoneNumber), Long.toString(timeStamp)};
		try{
			context.getContentResolver().delete(
					Uri.parse("content://call_log/calls"),
					selection, 
					selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.deleteFromCallLog() ERROR: " + ex.toString());
		}
	}

	/**
	 * Set the SMS/MMS message as read or unread depending on the input.
	 * 
	 * @param isViewed - Boolean, if true sets the message as viewed.
	 */
	private void setMessageRead(boolean isViewed){
		if (_debug) Log.v("Notification.setMessageRead()");
		Context context = getContext();
		long messageID = getMessageID();
		long threadID = getThreadID();
		String messageBody = getMessageBody();
		long timeStamp = getTimeStamp();
		if(messageID == 0){
			if (_debug) Log.v("Notification.setMessageRead() Message ID == 0. Loading Message ID");
			loadMessageID(context, threadID, messageBody, timeStamp);
			messageID = getMessageID();
		}
		ContentValues contentValues = new ContentValues();
		if(isViewed){
			contentValues.put("READ", 1);
		}else{
			contentValues.put("READ", 0);
		}
		String selection = null;
		String[] selectionArgs = null;
		try{
			context.getContentResolver().update(
					Uri.parse("content://sms/" + messageID), 
		    		contentValues, 
		    		selection, 
		    		selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.setMessageRead() ERROR: " + ex.toString());
		}
	}

	/**
	 * Set the Email message as read or unread depending on the input.
	 * 
	 * @param isViewed - Boolean, if true sets the message as viewed.
	 */
	private void setEmailRead(boolean isViewed){
		if (_debug) Log.v("Notification.setEmailRead()");
		try{
			//TODO - Write This Function setEmailRead(boolean isViewed)
//			Context context = getContext();
//			long messageID = getMessageID();
//			long threadID = getThreadID();
//			String messageBody = getMessageBody();
//			long timeStamp = getTimeStamp();
//			if(messageID == 0){
//				if (_debug) Log.v("Notification.setMessageRead() Message ID == 0. Load Message ID");
//				loadMessageID(context, threadID, messageBody, timeStamp);
//				messageID = getMessageID();
//			}
//			ContentValues contentValues = new ContentValues();
//			if(isViewed){
//				contentValues.put("READ", 1);
//			}else{
//				contentValues.put("READ", 0);
//			}
//			String selection = null;
//			String[] selectionArgs = null;
//			context.getContentResolver().update(
//					Uri.parse("content://sms/" + messageID), 
//		    		contentValues, 
//		    		selection, 
//		    		selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.setEmailRead() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Format/create the Calendar Event message.
	 * 
	 * @param eventStartTime - Calendar Event's start time.
	 * @param eventEndTime - Calendar Event's end time.
	 * @param allDay - Boolean, true if the Calendar Event is all day.
	 * 
	 * @return String - Returns the formatted Calendar Event message.
	 */
	private String formatCalendarEventMessage(String messageBody, long eventStartTime, long eventEndTime, boolean allDay){
		if (_debug) Log.v("Notification.formatCalendarEventMessage()");
		String formattedMessage = "";
		SimpleDateFormat eventDateFormatted = new SimpleDateFormat();
		eventDateFormatted.setTimeZone(TimeZone.getDefault());
		Date eventEndDate = new Date(eventEndTime);
		Date eventStartDate = new Date(eventStartTime);
		String[] startTimeInfo = eventDateFormatted.format(eventStartDate).split(" ");
		String[] endTimeInfo = eventDateFormatted.format(eventEndDate).split(" ");
    	if(messageBody.equals("")){
    		try{
	    		if(allDay){
	    			formattedMessage = startTimeInfo[0] + " - All Day";
	    		}else{
	    			//Check if the event spans a single day or not.
	    			if(startTimeInfo[0].equals(endTimeInfo[0]) && startTimeInfo.length == 3){
	    				formattedMessage = startTimeInfo[0] + " " + startTimeInfo[1] + " " + startTimeInfo[2] +  " - " +  endTimeInfo[1] + " " + startTimeInfo[2];
	    			}else{
	    				formattedMessage = eventDateFormatted.format(eventStartDate) + " - " +  eventDateFormatted.format(eventEndDate);
	    			}
	    		}
    		}catch(Exception ex){
    			if (_debug) Log.e("Notification.formatCalendarEventMessage() ERROR: " + ex.toString());
    			formattedMessage = eventDateFormatted.format(eventStartDate) + " - " +  eventDateFormatted.format(eventEndDate);
    		}
    	}else{
    		formattedMessage = messageBody;
    	}
		return formattedMessage;
	}
}