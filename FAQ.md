# Notify - Frequently Asked Questions #

  * [What is the difference between Notify and Notify+?](FAQ#What_is_the_difference_between_Notify_and_Notify+?.md)
  * [What is the difference between Notify Pro and Notify+?](FAQ#What_is_the_difference_between_Notify_Pro_and_Notify+?.md)

# **Notify** #

  * [Android KitKat (4.4+) and Notify](FAQ#Android_KitKat_(4.4+)_and_Notify.md)
  * [Can I change the UI of the notification?](FAQ#Can_I_change_the_UI_of_the_notification?.md)
  * [The notification icon is not removed when I dismiss or acknowledge the new message, why?](FAQ#The_notification_icon_is_not_removed_when_I_dismiss_or_acknowled.md)
  * [The contact photo in the notification doesn't display even though it shows in the system contacts, why?](FAQ#The_contact_photo_in_the_notification_doesn't_display_even.md)
  * [Can Application X be integrated with Notify?](FAQ#Can_Application_X_be_integrated_with_Notify?.md)
  * [Can Notify be moved to the SD card?](FAQ#Can_Notify_be_moved_to_the_SD_card?.md)
  * [After I delete an SMS it is still shown in my messaging app! Why?](FAQ#After_I_delete_an_SMS_it_is_still_shown_in_my_messaging_app!_Why.md)
  * [Known Issues with Go SMS & Notify](FAQ#Known_Issues_with_Go_SMS_&_Notify.md)
  * [Known Issues with Pansi SMS (aka Easy SMS) & Notify](FAQ#Known_Issues_with_Pansi_SMS_(aka_Easy_SMS)_&_Notify.md)
  * [Known Issues with Lango & Notify](FAQ#Known_Issues_with_Lango_&_Notify.md)
  * [Known Issues with ATT Messaging App & Notify](FAQ#Known_Issues_with_ATT_Messaging_App_&_Notify.md)
  * [Known Issues with Textra SMS & Notify](FAQ#Known_Issues_with_Textra_SMS_&_Notify.md)
  * [Known Issues with Chomp SMS & Notify](FAQ#Known_Issues_with_Chomp_SMS_&_Notify.md)
  * [Known Issues with Handcent SMS & Notify](FAQ#Known_Issues_with_Handcent_SMS_&_Notify.md)
  * [Will Notify display calendar events by non Android calendars?](FAQ#Will_Notify_display_calendar_events_by_non_Android_calendars?.md)
  * [I get status bar notifications but no popup, why?](FAQ#I_get_status_bar_notifications_but_no_popup,_why?.md)
  * [Can I help translate this application?](FAQ#Can_I_help_translate_this_application?.md)
  * [The SMS timestamp is not correct. What can I do?](FAQ#The_SMS_timestamp_is_not_correct._What_can_I_do?.md)
  * [Why is there a number indicating unread messages on top of my app icon?](FAQ#Why_is_there_a_number_indicating_unread_messages_on_top_of_my_ap.md)
  * [K-9/Kaiten Emails are not being shown. Why?](FAQ#K-9/Kaiten_Emails_are_not_being_shown._Why?.md)
  * [Aqua Mail Emails are not being shown. Why?](FAQ#Aqua_Mail_Emails_are_not_being_shown._Why?.md)
  * [The LED light is not working. Why?](FAQ#The_LED_light_is_not_working._Why?.md)
  * [The missed call status bar notification is not being dismissed. Why?](FAQ#The_missed_call_status_bar_notification_is_not_being_dismissed..md)
  * [Can I Suppress Notifications When I Am Using Application X?](FAQ#Can_I_Suppress_Notifications_When_I_Am_Using_Application_X?.md)
  * [Can Notifications Be Shown Over The Lockscreen?](FAQ#Can_Notifications_Be_Shown_Over_The_Lockscreen?.md)
  * [The Show When Locked/Unlocked options are not working. Why?](FAQ#The_Show_When_Locked/Unlocked_options_are_not_working._Why?.md)
  * [Email notifications information](FAQ#Email_notifications_information.md)
  * [Quick Reply information](FAQ#Quick_Reply_information.md)
  * [Notification sounds and vibrations are not working. Why?](FAQ#Notification_sounds_and_vibrations_are_not_working._Why?.md)
  * [Known Issues](FAQ#Known_Issues.md)

# **Notify+** #

  * [After Notify+ updates, the app stops working. Why?](FAQ#After_Notify+_updates,_the_app_stops_working._Why?.md)
  * [When I enable the Notify+ accessibility service my device begins to speak to me, why?](FAQ#When_I_enable_the_Notify+_accessibility_service_my_device_begins.md)
  * [When I try to enable the Notify+ accessibility service a conflict message is displayed and another service is disabled, why?](FAQ#When_I_try_to_enable_the_Notify+_accessibility_service_a_conflic.md)
  * [I get multiple notifications for a single status bar notification, why?](FAQ#I_get_multiple_notifications_for_a_single_status_bar_notificatio.md)



---


## What is the difference between Notify and Notify+? ##

**Notify**

Notify (Lite/Pro) is a full featured notification app for very specific notification types. Some specific features of each notification type are listed below.

1) SMS/MMS - Mark as read, delete, reply.

2) Missed Calls - Mark as read, delete, call.

3) Calendar - Dismiss, view.

4) K-9/Kaiten Mail Clients - Delete, reply.


**Notify+**

Adds very basic notifications for almost any app installed on the phone (as long as they post status bar notifications). Notify+ reads a posted status bar notification and displays it as a popup window. Only the information provided in the status bar notification can be used in the popup window.

## What is the difference between Notify Pro and Notify+? ##

**Notify Pro**

Includes all the features of the Notify Lite application and adds a few more features and notification types as described below:

1) Adds Facebook Notifications

2) Adds Twitter Notifications

3) Adds Gmail Notifications

4) Adds AquaMail Notifications

5) Adds A Quiet Time Feature

6) Adds A Blacklist Feature

7) Adds A Custom Contact Feature

8) Any new open API programs might be added as features to this app.

**Notify+**

Adds very basic notifications for almost any app installed on the phone (as long as they post status bar notifications). Notify+ reads a posted status bar notification and displays it as a popup window. Only the information provided in the status bar notification can be used in the popup window.

## Android KitKat (4.4+) and Notify ##

All the Notify apps (Notify Lite/Pro, Notify+ etc.) work with Android KitKat and above (Android 4.4+),  however, as of Android KitKat (4.4) there were changes to how it deals with SMS/MMS data. Notify can no longer delete or mark messages as read if they are running on Android 4.4+ devices. All other features work as expected.

The reason for this is due to changes that Google made to the Android OS. In Android 4.4+, only the Messaging app that is selected as the "Main" messaging app can modify the SMS/MMS data. All other apps only have read-only access. All "notification" apps were affected the same way. Notification popups that are included in the main messaging app will be able to delete or mark the messages as read from the popup.

## Can I change the UI of the notification? ##

Yes. There are built in themes that you can choose from. If you have any themes (any phone styles like a 3rd party rom, etc.) that you would like to see the notifications in, please contact me and I will do my best to create a theme in that style.

## The notification icon is not removed when I dismiss or acknowledge the new message, why? ##

This is likely because you have the messaging app notifications still enabled (or another apps notifications enabled for the type of notification: Calendar app, etc.) Unfortunately, there is no way for an app to remove another applications notifications.

Notify supports its own Status Bar Notifications. If you enable this feature, make sure that you disable the notifications from the Messaging App, Calendar App and any other apps that Notify supports.

Please note that you may not be able to remove the system Missed Call notification.

## The contact photo in the notification doesn't display even though it shows in the system contacts, why? ##

There are many 3rd party apps that hook into the phones contacts (e.g. Facebook, Twitter, Linked In, etc.). Although these apps are displaying user photos in the phones address book, these photos are not available by outside apps. The only photos that are available to all 3rd party apps are those that are part of the devices Google Contacts. You can download a few free apps that will sync your Facebook/Twitter/Linked In user data (including their photo) to the Google Contacts.

## Can Application X be integrated with Notify? ##

Unfortunately, the answer is probably no or not at this time.

Most applications are private apps and do not have an open API so currently there is no way to tap into their application data that they uses. I will continue to monitor all apps and look for any API's that will allow Notify to communicate with them or their data.

Apps that users have asked for integration (with comments):

1) WhatsApp - Currently there is no API for this application. I have contacted the company to request this feature. Until they let 3rd party developers access to the data there is nothing I can do.

2) Skype - Looking into the possibility.

4) Google+ - Looking into the possibility.

5) LinkedIn - Looking into the possibility.

6) gTalk - Looking into the possibility.

7) Yahoo Mail - Looking into the possibility. Why not install K-9 Mail or Kaiten Mail? It works great with Yahoo Mail!

8) Hotmail - Looking into the possibility. Why not install K-9 Mail or Kaiten Mail? It works great with Hotmail!

While you wait for more notification types, you can use an add-on called **Notify+** which is an accessibility service which offers a simple notification popup for all of the phones other apps. Try it out today!

## Can Notify be moved to the SD card? ##

Unfortunately, the answer is no.

If the app is moved to the SD Card / External Storage, the functionality of the application will be impacted in a negative manor. This application uses alarms and phone state intent filters to function normally. When apps are moved to the SD Card, the phone state intent filters are not received and if the SD card is ever removed or unmounted, all alarms are cleared from the memory. These issues would be catastrophic in terms of the apps operation so the application must remain on the phones internal memory.

## After I delete an SMS it is still shown in my messaging app! Why? ##

Basically, this is what is going on. SMS messaging apps have a cache. They cache your messages so that app starts up faster and appears more responsive. Unfortunately, when an outside app updates the message databsae the messaging app does not know that this update occured. This means that when the messaging app starts it still has older, possibly read, possibly deleted messages displayed. Those messages show up in teh messaging app but are not in the messaging databse any more becuase they were deleted. Depending on the messaging apps user preferences, you should see if there is an optoin to turn off message caching. Either way, you must wait for the messaging app to refresh it's cache before the deleted message dissapears or the message is marked as read. There is nothign that an outside app like Notify can do about this. Messaging app developers need to consider that the SMS database can be updated outside of their app.

## Known Issues with Go SMS & Notify ##

If you want to use GO SMS, you must follow the steps below in order for Notify to work with SMS/MMS notifications.

1) Uncheck/deselect the "Disable other message notifications" option in the GO SMS Pro settings/preferences (App Settings->Advanced Tab->Other Settings->Disable other message notifications). If this option is checked/selected then Notify may not work (i.e. SMS/MMS notifications may not be displayed).

## Known Issues with Pansi SMS (aka Easy SMS) & Notify ##

If you want to use Pansi SMS, you must follow the steps below in order for Notify to work with SMS/MMS notifications.

1) Uncheck/deselect the "Disable other message notifications" option in the Pansi SMS settings/preferences (App Settings->Setup Wizard (the last screen has the setting "Disable other message notifications")). If this option is checked/selected then Notify may not work (i.e. SMS/MMS notifications may not be displayed).

## Known Issues with Lango & Notify ##

If you want to use Lango, you must follow the steps below in order for Notify to work with SMS/MMS notifications.

1) Uncheck/deselect the "Set as default messaging client" option in the Lango settings/preferences (App Settings->Set as default messaging client). If this option is checked/selected then Notify may not work (i.e. SMS/MMS notifications may not be displayed).

## Known Issues with ATT Messaging App & Notify ##

Users have reported that a bundled app called "ATT Messaging" can stop SMS & MMS notifications from appearing. Uninstalling this application can solve this problem.

## Known Issues with Textra SMS & Notify ##

The Textra SMS app can have compatibility issues with earlier version of Notify. Updating the Notify app to the latest version should solve these issues.

**Note** Chomp SMS & Textra SMS are made by the same developers, so they have the same possible compatibility problems as well.

## Known Issues with Chomp SMS & Notify ##

The Chomp SMS app app can have compatibility issues with earlier version of Notify. Updating the Notify app to the latest version should solve these issues.

**Note** Chomp SMS & Textra SMS are made by the same developers, so they have the same possible compatibility problems as well.

## Known Issues with Handcent SMS & Notify ##

The Handcent SMS app has compatibility issues with almost all other SMS apps. They appear to be aborting the SMS broadcast which disables most other SMS apps. At this time there is no solution.

## Will Notify display calendar events by non Android calendars? ##

The answer is...it depends.

Developer apps have the ability to read the Google Calendar that is build into the Android OS. If the calendar app syncs all the information to the Google Calendar, then Notify will read the Google Calendar and notify you of the events. On the other hand if the calendar app keeps it's own calendar, then developer apps can't access that data and so Notify won't notify you of the calendar events.

## I get status bar notifications but no popup, why? ##

This issue is most likely due to the popup being blocked. Being in a phone call blocks the popup, but there is also an option to block the popup if you using certain apps. You can change this setting here (App Settings->Basic->Blocking App Settings).

## Can I help translate this application? ##

Yes! I would encourage anyone who speaks English and any other language to help translate this application. I use a collaborative translation tool powered by [CrowdIn.Net](http://www.crowdin.net/). Email me if you would like to help translate this application (droidnotify@gmail.com).

## The SMS timestamp is not correct. What can I do? ##

This is a problem because depending on the cell carrier, SMS messages will be delivered with a timestamp in either UTC time or in local time. By default, the app expects the incoming timestamp to be in local time. This can be fixed by tell the app that the incoming SMS messages are in UTC time and not local time. Visit the SMS user preferences located here:

_**App Settings->Notifications->SMS->Time Is In UTC**_

Check the box "Time Is In UTC" to tell the app that incoming SMS timestamps are in UTC time.

**Note:** Version 4.0 of the app has some code that tries to automagically set this for you, but it might not work 100% of the time.

## Why is there a number indicating unread messages on top of my app icon? ##

Many phones have built on top of the stock Android OS a custom UI (example: HTC Sense, Samsung TouchWiz, Etc...) These phones might have a Message Unread Count number that displays over the app icon. Unfortunately, most of these custom apps do not have a way for 3rd party app to refresh the counter. This means that when Notify updates the SMS data or dismisses an email (using Notify+), the counter may not aware of it. This means that even though the Email or SMS has been viewed and is marked as viewed on the phone, the app still says that there are unread messages. Until these apps provide a way for 3rd party apps to refresh this icon, this will remain an issue on those devices.

## K-9/Kaiten Emails are not being shown. Why? ##

There is most likely one of two reasons that this might happen.

1) The most likely reason is that you installed K-9/Kaiten AFTER you installed Notify. The reason has to do with permissions. Basically, K-9/Kaiten define permissions that Notify uses. If you install Notify first, the permissions are not associated with the app and it will break. All you have to do is uninstall Notify and re-install it again.

2) The less likely reason is that there is a bug in the code. Try step 1 first and if that doesn't solve the problem please send me an email with a debug log.

## Aqua Mail Emails are not being shown. Why? ##

There is most likely one of two reasons that this might happen.

1) The most likely reason is that you installed Aqua Mail AFTER you installed Notify. The reason has to do with permissions. Basically, Aqua Mail define permissions that Notify uses. If you install Notify first, the permissions are not associated with the app and it will break. All you have to do is uninstall Notify and re-install it again.

2) The less likely reason is that there is a bug in the code. Try step 1 first and if that doesn't solve the problem please send me an email with a debug log.

## The LED light is not working. Why? ##

The Android OS allows you to set the LED color to any color that you want, after which the hardware will **do its best** to show that color. If the color you chose doesn't show properly or doesn't show at all then it is likely a hardware limitation. Some newer Android phones may not even have an LED or may not have an LED that supports multiple colors (or the color that you selected in particular).

In my experience, many phones only show the LED when the screen is off. If the option to unlock the phone and turn the screen on is selected, then the LED will never be displayed on these phones. Another problem is that some phones only display a few colors (red, green, orange). If you set the LED color to blue or a bluish color, for example, then the LED will not be shown at all.

## The missed call status bar notification is not being dismissed. Why? ##

On most phones, the stock missed call status bar notification will not be removed when a missed call popup id dismissed. This status bar notification is a system notification and there is no API to remove it. If you are running Android 2.2, this should not be a problem, but any version of 2.3 or greater, this continues to be an issue. Until Android provides a way to remove system notifications, this issue will remain. Some phones may have a way to stop notifications for the "phone" app. This does depend on the manufacturer (aka. Samsung, HTC, etc.) Playing around in the device system settings, you may find an option that will stop the system notifications.

If your phone has been rooted, you can install and use a 3rd party add-on called "NotiGo" which can be found **[here](http://forum.xda-developers.com/showthread.php?t=1187025)** to help remove the system missed call notification.

## Can I Suppress Notifications When I Am Using Application X? ##

Yes, you can prevent the popup notification from appearing when you are using almost any application. You can select the applications that you wish to block notifications by updating the application settings here:

_**App Settings->Basic->Blocking Apps Settings**_

Simply enable app blocking and select the apps on your phone that you want to block notifications.

## Can Notifications Be Shown Over The Lockscreen? ##

The answer is, it depends.

1) Android OS: If you are using the Android OS (default, stock) lockscreen, then no, you can't. Android does not currently have a way to display anything over/on top of a  lockscreen. Developers can either lock or unlock the device and that's it.

2) Lockscreen Apps: If you are using a 3rd party lockscreen app (e.g. WidgetLocker, Go Locker, etc.), you may be able to display a notification over a locked screen. This all depends on that particular app. A lockscreen app is just another app on your device. Just like any other app, a lockscreen app can be pushed to the background when another app (say, Notify) is pushed to the foreground (aka, on top of the lockscreen app). Here are some common lockscreen apps and their behavior:

WidgetLocker - Notifications sometimes breaks this app and freezes the screen (locking the device and unlocking it fixes the issue). Using the "Lockscreen App Support" option in Notify allows a perfect experience with this app but this option does not allow you to display notifications over this lockscreen.

GoLocker - Notifications appear over the lockscreen as expected. This app does not have an API so Notify cannot unlock the phone when this app is in use.

Active Lockscreen - Notifications appear over the lockscreen as expected. This app does not have an API so Notify cannot unlock the phone when this app is in use.

Most lockscreen apps will display the notifications over them.

## The Show When Locked/Unlocked options are not working. Why? ##

These options are not compatible with 3rd party lockscreens. If these options are not working properly on your device it's because you are using a 3rd party lockscreen. These lockscreen apps "fake" a device lockscreen and always have the device in an unlocked state. THis is why those options might not be working.

## Email notifications information ##

At this time the Email content on Android phones is secure and 3rd party apps cannot read the phones emails. There are a few email clients that have open APIs that 3rd party developers can access.

1) K-9 and Kaiten email clients are supported but you will need to have a K-9/Kaiten app (K-9 Mail, Kaiten Mail, etc.) installed on your device for notifications to work.

2) AquaMail email client is supported but you will need to have the AquaMail app installed on your device for notifications to work.

3) Gmail is supported but you will need to have the Gmail app (with  version 2.3.6 or newer for Froyo/Gingerbread and version 4.0.5 and newer for Honeycomb and Ice Cream Sandwich) for those notifications to work.

_Please note:_ Currently the API provided by the Gmail app only gives access to the unread count of the email accounts. No other information is available (e.g. Sender, Email Subject, Email Body, Etc.) You can read more about the API that Gmail has provided here https://developers.google.com/gmail/android/

## Quick Reply information ##

The quick reply signature can be disable or updated here: _**App Settings->Basic->Quick Reply Settings**_

When replying to an SMS message via the Quick Reply feature some devices send two identical messages out instead of a single message. This is caused by a bug in the Android OS and is specific to certain devices and manufacturers. More information on this bug can be found here: https://code.google.com/p/android/issues/detail?id=27024

When you use the Quick Reply in the popup notification, the on-screen keyboard aka "soft keyboard" can hide parts of the notification including the reply box which makes it very difficult to type a reply. There are user options to help with this. They are located here: _**App Settings->Basic->Quick Reply Settings**_

On this user settings screen you can hide some of the notification items if the soft keyboard is covering them. These include:

**Hide Header Panel** - Hide the row of navigation buttons and notification count text at the very top of the popup window.

**Hide Contact Panel** - Hide the row under the navigation buttons that has the contact photo, name and number of the notification.

**Hide Button Panel** - Hide the row of buttons under the quick reply text box.

## Notification sounds and vibrations are not working. Why? ##

If you are running Android 4.1 (Jelly Bean) or above, the notifications may not be working by default. You must make sure that the app has the ability to show notifications. This option is checked by default. You can check this setting here:

Device Settings->Application Manager->(Select the Notify Lite/Pro app). Ensure that the "Show notifications" checkbox is checked.

## Known Issues ##

Here are confirmed, known issues that have not been resolved yet:

  * There is a known bug in Android Jelly Bean which may cause your screen to stay on. You can read about it here: http://code.google.com/p/android/issues/detail?id=39625. If you experience this issue, many users find that performing a "Factory Reset" after the update fixed this issue.

  * There is a known bug in the Samsung devices running Android Jelly Bean which caused the device to begin speaking once an accessibility service is enabled (any accessibility service). It appears to happen mostly, if not exclusively on Samsung Devices running Android Jelly Bean(4.1, 4.2) and Samsung's custom TouchWiz UI. A possible solution is listed here:

> Go to Device Settings->Application Manager. Go to the section/tab titled 'All'. Select the app titled 'Google Text-to-speech Engine' and click 'Disable. Then select the app titled 'Samsung TTS' and click 'Disable'.

> Go to to Device Settings->Accessibility and enable Notify+. The app should function normally without the device speaking to you. Here is the Google Documented Issue: http://code.google.com/p/android/issues/detail?id=23105. Here is the Samsung Documented Issue: http://developer.samsung.com/forum/board/thread/view.do?boardName=GeneralB&messageId=204387.

  * Facebook messages may not work if it's a new message. They are however working when an unread message exists on Facebook. This is a bug with the Facebook API. It is documented here: https://developers.facebook.com/bugs/227066064081265/. I am monitoring this and will continue to search for fixes to this known issue.

## After Notify+ updates, the app stops working. Why? ##

There is a bug in all versions of Android which stops the accessibility services from running after an update. You must disable and then re-enable the accessibility service in the phones settings restart/re-enable the service.

## When I enable the Notify+ accessibility service my device begins to speak to me, why? ##

This is a known bug in the Android OS on the device. It appears to happen mostly, if not exclusively on Samsung Devices running Android Jelly Bean(4.1, 4.2) and Samsung's custom TouchWiz UI. A possible solution is listed here:

  * Go to Device Settings->Application Manager. Go to the section/tab titled 'All'. Select the app titled 'Google Text-to-speech Engine' and click 'Disable. Then select the app titled 'Samsung TTS' and click 'Disable'.

  * Go to to Device Settings->Accessibility and enable Notify+. The app should function normally without the device speaking to you. Here is the Google Documented Issue: http://code.google.com/p/android/issues/detail?id=23105. Here is the Samsung Documented Issue: http://developer.samsung.com/forum/board/thread/view.do?boardName=GeneralB&messageId=204387.

## When I try to enable the Notify+ accessibility service a conflict message is displayed and another service is disabled, why? ##

This is a known bug in the Android OS on the device. It appears to happen mostly, if not exclusively on Samsung Devices running Android Jelly Bean(4.1, 4.2) and Samsung's custom TouchWiz UI. Unfortunately, there is no known remedy for this issue at this time.

Here is the Google Issue: http://code.google.com/p/android/issues/detail?id=23105

Here is the Samsung Issue: http://developer.samsung.com/forum/board/thread/view.do?boardName=GeneralB&messageId=204387

## I get multiple notifications for a single status bar notification, why? ##

Notify+ simply reads the status bar notification.
Allot of apps send multiple status bar notification "updates" to the status bar notification. Each time an app sends information to the status bar, the Notify+ app will respond and display that information to you. Sometimes this information will be empty and sometimes it will have new, updated information. This behavior is driven by the outside app that is posting the status bar notification and Notify+ cannot control this unfortunately.