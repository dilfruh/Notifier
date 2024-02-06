package com.dil.notifier

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/**
 * This is where most of the action happens
 */
class MainActivity : AppCompatActivity() {
    //Set up buttons, textViews, editTexts, spinners, checkboxes
    var vibrationText: TextView? = null
    var notificationList: TextView? = null
    var instructionsButton: Button? = null
    var accessButton: Button? = null
    var createButton: Button? = null
    var editAppName: EditText? = null
    var vibrateSpinner: Spinner? = null
    var edgeCheck: CheckBox? = null
    var infoIcon: ImageButton? = null
    var infoTooltip: TextView? = null

    /**
     * Get and display list of notification channels. Call this every time app created, create button pressed, or delete button pressed
     */
    private fun getList() {
        //Get list of notification channels
        var list1: List<NotificationChannel>
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        list1 = notificationManager.notificationChannels
        //Sort list alphabetically by name
        list1.sortBy { it.name?.toString() }

        //Add name of each channel to TextView3
        notificationList?.text = "\nCurrent Notifications:"
        for (notif in list1) { //For every item in list1, we will name it notif
            //Get vibration pattern
            var vibrationText = ""
            val notifPattern = notif.vibrationPattern
            val none = longArrayOf(0, 0)
            val oneLong = longArrayOf(0, 1500)
            val oneShort = longArrayOf(0, 400)
            val twoLong = longArrayOf(0, 1500, 300, 1500)
            val twoShort = longArrayOf(0, 300, 200, 300)
            val threeLong = longArrayOf(0, 1500, 300, 1500, 300, 1500)
            val threeShort = longArrayOf(0, 200, 200, 200, 200, 200)
            val oneShortOneLong = longArrayOf(0, 200, 200, 1500)
            val twoShortOneLong = longArrayOf(0, 200, 200, 200, 200, 1500)
            if (notifPattern.contentEquals(none)) {
                vibrationText = " no vibration"
            }
            else if (notifPattern.contentEquals(oneLong)) {
                vibrationText = " 1 long"
            }
            else if (notifPattern.contentEquals(oneShort)) {
                vibrationText = " 1 short"
            }
            else if (notifPattern.contentEquals(twoLong)) {
                vibrationText = " 2 long"
            }
            else if (notifPattern.contentEquals(twoShort)) {
                vibrationText = " 2 short"
            }
            else if (notifPattern.contentEquals(threeLong)) {
                vibrationText = " 3 long"
            }
            else if (notifPattern.contentEquals(threeShort)) {
                vibrationText = " 3 short"
            }
            else if (notifPattern.contentEquals(oneShortOneLong)) {
                vibrationText = " 1 short, 1 long"
            }
            else if (notifPattern.contentEquals(twoShortOneLong)) {
                vibrationText = " 2 short, 1 long"
            }

            val notifName = notif.name.toString()
            //Say if there is supposed to be edge lighting and add vibration pattern
            if (notifName.contains(" Screen Off (With Edge Lighting)")){
                //Isolate just app name
                val appName = notifName.replace(" Screen Off (With Edge Lighting)", "")

                //Make the app name bold and underlined
                val sb: Spannable = SpannableString(appName)
                sb.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, appName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                sb.setSpan(UnderlineSpan(), 0, appName.length, 0)

                //Say the app name, edge lighting, and vibration text
                notificationList?.append("\n\n")
                notificationList?.append(sb)
                notificationList?.append(": Yes Edge Lighting,")
                notificationList?.append(vibrationText)
            }
            else if (notifName.contains(" Screen Off (No Edge Lighting)")){
                //Isolate just app name
                val appName = notifName.replace(" Screen Off (No Edge Lighting)", "")

                //Make the app name bold and underlined
                val sb: Spannable = SpannableString(appName)
                sb.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, appName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                sb.setSpan(UnderlineSpan(), 0, appName.length, 0)

                //Say the app name, edge lighting, and vibration text
                notificationList?.append("\n\n")
                notificationList?.append(sb)
                notificationList?.append(": No Edge Lighting,")
                notificationList?.append(vibrationText)
            }
            //Don't want to do for ScreenOn because then we'd repeat app names
        }
    }

    //When app is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Create buttons, textViews, editTexts, checkboxes
        vibrationText = findViewById<TextView>(R.id.vibrationText)
        notificationList = findViewById<TextView>(R.id.notificationList)
        instructionsButton = findViewById<Button>(R.id.instructionsButton)
        accessButton = findViewById<Button>(R.id.accessButton)
        createButton = findViewById<Button>(R.id.createButton)
        editAppName = findViewById<EditText>(R.id.editAppName)
        edgeCheck = findViewById<CheckBox>(R.id.edgeCheck)
        infoIcon = findViewById<ImageButton>(R.id.infoIcon)
        infoTooltip = findViewById<TextView>(R.id.infoTooltip)

        //Create spinner (drop down menu)
        vibrateSpinner = findViewById<Spinner>(R.id.vibrateSpinner)
        // Create an ArrayAdapter using the string array defined in strings.xml and spinner layout you created
        ArrayAdapter.createFromResource(
            this,
            R.array.vibrateArray, R.layout.spinner
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner)
            // Apply the adapter to the spinner (!! is just as long as it's not null)
            vibrateSpinner!!.adapter = adapter
        }

        /* Old way in which we can't change text size
        vibrateSpinner = findViewById(R.id.vibrateSpinner) as Spinner
        // Create an ArrayAdapter using the string array defined in strings.xml and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.vibrateArray,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner (!! is just as long as it's not null)
            vibrateSpinner!!.adapter = adapter
        }*/

        //Update list of notification channels using function defined earlier
        getList()
    }

    /**
     * When app instructions button is clicked, start InstructionsActivity to show the instructions
     */
    fun instructionsClicked(view: View) {
        val intent = Intent(this, InstructionsActivity::class.java).apply {
        }
        startActivity(intent)
    }

    /**
     * When notification access button is clicked, go to the setting to grant app access to notifications
     */
    fun accessClicked(view: View) {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        startActivity(intent)
    }

    /**
     * When the test vibration button is clicked, vibrate the phone with the selected pattern so users can see what it will feel like
     */
    fun vibrateClicked(view: View) {
        //Vibration option selected
        val choice = vibrateSpinner?.selectedItem.toString()
        //Set up vibrator
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator

        //Vibrate device based on choice
        when (choice) {
            "1 long" -> v.vibrate(
                VibrationEffect.createOneShot(
                    1500,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
            "1 short" -> v.vibrate(
                VibrationEffect.createOneShot(
                    400,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
            "2 long" -> {
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(1800) //really wait 300, but have to add in 1500 from previous vibration
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            "2 short" -> {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(500)
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            "3 long" -> {
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(1800)
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(1800)
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            "3 short" -> {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(400)
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(400)
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            "1 short, 1 long" -> {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(400)
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            "2 short, 1 long" -> {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(400)
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                Thread.sleep(400)
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    /**
     * When the info icon next to the edge lighting checkbox is clicked, show what it means
     */
    fun infoClicked(view: View) {
        // Hide or show the tooltip
        if (infoTooltip?.visibility == View.GONE) infoTooltip?.visibility = View.VISIBLE
        else infoTooltip?.visibility = View.GONE
    }

    /**
     * When the delete button is clicked, delete the notification settinsg for the app name that is currently typed
     */
    fun deleteClicked(view: View) {
        //Get name from editText field and convert to String
        val name: String = editAppName?.text.toString()

        //Delete as long as user inputted a name
        if (name != "") {
            //Get list of notification channels
            var list1: List<NotificationChannel> = ArrayList()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            list1 = notificationManager.notificationChannels

            for (notif in list1) {
                val notifName = notif.id
                if (notifName.startsWith(name + "ScreenOn")) {
                    notificationManager.deleteNotificationChannel(notifName)
                }
                if (notifName.startsWith(name + "ScreenOff")) {
                    notificationManager.deleteNotificationChannel(notifName)
                }
            }

            //Tell user it's been deleted
            Toast.makeText(applicationContext, name + " notification deleted", Toast.LENGTH_SHORT).show()

            //Update list of notification channels using function defined earlier
            getList()
        }
    }

    /**
     * When the create button is clicked, create a vibration and edge lighting for the info typed/selected
     */
    fun createClicked(view: View) {
        //Get name from editText field and convert to String
        val name: String = editAppName?.getText().toString()

        //Create as long as they inputted a name
        if (name != "") {

            //Delete notification channels with that name in case they are editing existing one
            var list1: List<NotificationChannel> = ArrayList()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            list1 = notificationManager.notificationChannels
            for (notif in list1) {
                val notifName = notif.id
                if (notifName.startsWith(name + "ScreenOn")) {
                    notificationManager.deleteNotificationChannel(notifName)
                }
                if (notifName.startsWith(name + "ScreenOff")) {
                    notificationManager.deleteNotificationChannel(notifName)
                }
            }

            //Create channel ids
            var CHANNEL_ID1 = name + "ScreenOn"
            var name1 = name + " Screen On"

            //Change screen off channel name according to if wake screen is on or off
            var CHANNEL_ID2 = ""
            var name2 = ""
            if (edgeCheck!!.isChecked) { //!! is just safety for non null
                CHANNEL_ID2 = name + "ScreenOffWakeUp"
                name2 = name + " Screen Off (With Edge Lighting)"
            } else {
                CHANNEL_ID2 = name + "ScreenOff"
                name2 = name + " Screen Off (No Edge Lighting)"
            }

            //Add vibration pattern chosen to channel ids
            val vibrateChoice = vibrateSpinner?.getSelectedItem().toString()
            CHANNEL_ID1 += vibrateChoice
            CHANNEL_ID2 += vibrateChoice

            //Create notification channel for screen on
            val importance1 = NotificationManager.IMPORTANCE_DEFAULT //This is no popup
            val mChannel1 = NotificationChannel(CHANNEL_ID1, name1, importance1)
            mChannel1.description = "When the screen is on so no popup"

            //Create notification channel for screen off
            val importance2 = NotificationManager.IMPORTANCE_HIGH //This is a popup
            val mChannel2 = NotificationChannel(CHANNEL_ID2, name2, importance2)
            mChannel2.description = "When the screen is off so yes popup"

            //Set vibration patterns for both channels
            when (vibrateChoice) {
                "none" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 0)
                    mChannel2.vibrationPattern = longArrayOf(0, 0)
                    mChannel1.setSound(
                        null,
                        null
                    ) //Silent because they probably also don't wanna change the sound then
                    mChannel2.setSound(null, null)
                }
                "1 long" -> {
                    mChannel1.vibrationPattern =
                        longArrayOf(0, 1500) //0 sec off, 1.5 sec on, sec off, etc.
                    mChannel2.vibrationPattern = longArrayOf(0, 1500)
                }
                "1 short" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 400)
                    mChannel2.vibrationPattern = longArrayOf(0, 400)
                }
                "2 long" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 1500, 300, 1500)
                    mChannel2.vibrationPattern = longArrayOf(0, 1500, 300, 1500)
                }
                "2 short" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 300, 200, 300)
                    mChannel2.vibrationPattern = longArrayOf(0, 300, 200, 300)
                }
                "3 long" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 1500, 300, 1500, 300, 1500)
                    mChannel2.vibrationPattern = longArrayOf(0, 1500, 300, 1500, 300, 1500)
                }
                "3 short" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 200, 200, 200, 200, 200)
                    mChannel2.vibrationPattern = longArrayOf(0, 200, 200, 200, 200, 200)
                }
                "1 short, 1 long" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 200, 200, 1500)
                    mChannel2.vibrationPattern = longArrayOf(0, 200, 200, 1500)
                }
                "2 short, 1 long" -> {
                    mChannel1.vibrationPattern = longArrayOf(0, 200, 200, 200, 200, 1500)
                    mChannel2.vibrationPattern = longArrayOf(0, 200, 200, 200, 200, 1500)
                }
            }

            //Actually create both notification channels
            notificationManager.createNotificationChannel(mChannel1)
            notificationManager.createNotificationChannel(mChannel2)

            //Tell user it's been created
            Toast.makeText(applicationContext, name + " notification created", Toast.LENGTH_SHORT).show()

            //Update list of notification channels using function defined earlier
            getList()
        }
    }
}