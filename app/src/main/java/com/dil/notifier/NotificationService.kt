package com.dil.notifier

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.PowerManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * This overrides the default NotificationListenerService so we can intercept notifications coming in from other apps and create our own with our custom vibration patterns and lighting
 */
class NotificationService : NotificationListenerService() {
    //Create Tag and contexts which aren't that important to know about
    private val TAG = this.javaClass.simpleName
    var context: Context? = null

    //Create list of active notifications
    var list = activeNotifications
    
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        //Log info, doesn't really affect anything I believe
        Log.i(TAG, "********** onNotificationPosted")
        Log.i(
            TAG,
            "ID :" + sbn.id + " \t " + sbn.notification.tickerText + " \t " + sbn.packageName
        )

        //Get notification app name
        val pm = applicationContext.packageManager
        val ai: ApplicationInfo?
        ai = try { pm.getApplicationInfo(sbn.packageName, 0) }
        catch (e: PackageManager.NameNotFoundException) { null }
        val applicationName = (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String
        //Basically it's val pm = applicationContext.packageManager;
        //               val applicationName = pm.getApplicationLabel(pm.getApplicationInfo(sbn.packageName, 0)) as String

        //Get notification title and content
        val title: String? = sbn.notification.extras.getString("android.title") + ": " + sbn.notification.extras.getString("android.text")
        //Get group behavior of notification. 0 means alert everything, 1 means alert only the group summary notification, 2 means alert only the children of the summary
        val alert: Int? = sbn.notification.groupAlertBehavior

        //Get rid of duplicate notifications from group summaries or children of the summaries
        //To check for a certain flag, bitwise & that specific flag with the flags int of the notification. If it isn't 0, then the flag is active (weird huh)
        var send = true
        if ((alert == 2) && (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0)){
            //If it's a summary and you aren't supposed to alert the summary, don't send
            send = false
        }
        if ((alert == 1) && (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY == 0)){
            //If it isn't a summary and you are only supposed to alert the summary, don't send
            send = false
        }

        var t2: String? = sbn.notification.extras.getString("android.title") + sbn.notification.extras.getString("android.text") + sbn.toString()
        //Check to see if literally repeating the same notification from list of previous notifications
        for(i in list.indices){
            val t1: String? = list[i].notification.extras.getString("android.title") + list[i].notification.extras.getString("android.text") + list[i].toString()
            if(t1 == t2){
                send = false
            }
        }

        //Get rid of Snapchat multiple notifications
        if ((title!!.contains("Running...")) || (title!!.contains("Updating messages..."))){
            send = false
        }

        //Have myListener perform sendNotif function using that notification app name and title+content and long as it's not a duplicate
        if (send) {
            sendNotif(applicationName, title)
        }

        //Update list of notifications to check for repeats next time
        list = activeNotifications
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i(TAG, "********** onNotificationRemoved")
        Log.i(
            TAG,
            "ID :" + sbn.id + " \t " + sbn.notification.tickerText + " \t " + sbn.packageName
        )
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        //requestRebind();
    }

    /**
     * Send a notification for notifAppName with notifTitle
     * @param notifAppName the name of the app
     * @param notifTitle the text displayed in the notification
     */
    private fun sendNotif(notifAppName: String?, notifTitle: String?) {
        //Get list of notification channels
        var list1: List<NotificationChannel> = ArrayList()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        list1 = notificationManager.notificationChannels

        //Check if app name has channel to be notified
        var exists = false
        var wakeUp = false
        var onChannel = ""
        var offChannel = ""
        for (notif in list1) { //For every item in list1, we will name it notif
            val notifName = notif.id
            val notifOn = notifAppName + "ScreenOn"
            val notifOff = notifAppName + "ScreenOff"
            val notifWakeUp = notifAppName + "ScreenOffWakeUp"

            //Say the channel exists if there's a match and save Screen On channel id
            if (notifName.startsWith(notifOn)) {
                exists = true
                onChannel = notifName
            }

            //Find out if supposed to wake up and save Screen Off channel id
            if (notifName.startsWith(notifWakeUp)) {
                exists = true
                wakeUp = true
                offChannel = notifName
            }
            else if (notifName.startsWith(notifOff)) {
                exists = true
                offChannel = notifName
            }
        }

        //Send notification if you are supposed to
        if (exists) {
            //Check if screen is on
            val pm = this.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isScreenOn = pm.isInteractive
            Log.e("screen on.................................", "" + isScreenOn)

            var CHANNEL_ID = ""

            //if screen on, use onChannel, else offChannel
            if (isScreenOn) {
                CHANNEL_ID = onChannel
            }
            else {
                CHANNEL_ID = offChannel
            }

            //Create notification
            var builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(notifAppName)
                .setContentText(notifTitle)

            //Send notification
            with(NotificationManagerCompat.from(this)) {
                val notificationId = 1
                notify(notificationId, builder.build())
            }

            //Wake screen if supposed to
            if (!isScreenOn && wakeUp) {
                val wl = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                    "myApp:notificationLock"
                )
                wl.acquire(10000)
                val wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myApp:MyCpuLock")
                wl_cpu.acquire(10000)
            }

            //Wait 5.1 sec
            Thread.sleep(5_100)

            //Dismiss notification
            with(NotificationManagerCompat.from(this)) {
                val notificationId = 1
                cancel(notificationId)
            }
        }
    }

}