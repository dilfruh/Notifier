package com.dil.notifier

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * This is where most of the action happens
 */
class MainActivity : AppCompatActivity() {
    // Set up buttons, textViews, editTexts, spinners, checkboxes, etc.
    var accessText: TextView? = null
    var vibrationText: TextView? = null
    var listHeader: TextView? = null
    var infoTooltip: TextView? = null
    var instructionsButton: Button? = null
    var accessButton: Button? = null
    var createButton: Button? = null
    var editAppName: EditText? = null
    var vibrateSpinner: Spinner? = null
    var edgeCheck: CheckBox? = null
    var infoIcon: ImageButton? = null
    var notificationList: RecyclerView? = null
    val myAdapter = NotificationListAdapter()

    // Vibration patterns
    val vibrations = arrayOf(
        vibrationData("none", longArrayOf(0, 0)),
        vibrationData("1 long", longArrayOf(0, 1500)), // 0 sec off, 1.5 sec on, sec off, etc.
        vibrationData("1 short", longArrayOf(0, 400)),
        vibrationData("2 long", longArrayOf(0, 1500, 300, 1500)),
        vibrationData("2 short", longArrayOf(0, 300, 200, 300)),
        vibrationData("3 long", longArrayOf(0, 1500, 300, 1500, 300, 1500)),
        vibrationData("3 short", longArrayOf(0, 200, 200, 200, 200, 200)),
        vibrationData("1 short, 1 long", longArrayOf(0, 200, 200, 1500)),
        vibrationData("2 short, 1 long", longArrayOf(0, 200, 200, 200, 200, 1500))
    )

    /**
     * Get and display list of notification channels. Call this every time app created, create button pressed, or delete button pressed
     */
    private fun getList() {
        // Get list of notification channels
        var list1: List<NotificationChannel>
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        list1 = notificationManager.notificationChannels

        // Get data for each channel
        for (notif in list1) { // For every item in list1, we will name it notif
            // Get vibration pattern
            val notifPattern = notif.vibrationPattern
            val vibration = vibrations.find { vib -> notifPattern.contentEquals(vib.pattern) }
            var vibrationText = vibration?.name
            if (vibrationText == null || vibrationText === "none") vibrationText = "no vibration"

            val notifName = notif.name.toString()
            // Say if there is supposed to be edge lighting and add vibration pattern
            if (notifName.contains(" Screen Off (")){
                // Isolate just app name
                val appName = notifName.replace(" Screen Off (With Edge Lighting)", "").replace(" Screen Off (No Edge Lighting)", "")

                // Add to notification list
                val data = NotificationData(appName, vibrationText, notifName.contains(" Screen Off (With Edge Lighting)")) { deleteNotification(appName) } // This is how you pass in a function for a param
                myAdapter.addNotification(data)
            }
            // Don't want to do for ScreenOn because then we'd repeat app names
        }

        // Add data to list
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        notificationList?.adapter = myAdapter
        notificationList?.layoutManager = layoutManager

        // Add dividers between items
        notificationList?.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                layoutManager.orientation
            )
        )

        // We don't have to do this now that we use a SortedList
        // Update list
        //myAdapter.notifyDataSetChanged()
    }

    /**
     * checks to see if you have given permission to read notifications, and shows that you need to if not
     */
    private fun getNotificationAccess() {
        val pkgName: String = this.getPackageName()
        val listeners: MutableSet<String> = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (listeners?.contains(pkgName)) {
            accessButton?.visibility = View.GONE
            accessText?.visibility = View.GONE
        }
        else {
            accessButton?.visibility = View.VISIBLE
            accessText?.visibility = View.VISIBLE
        }
        // This doesn't seem to work
        /*
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)
        if (permission == PackageManager.PERMISSION_GRANTED) accessButton?.visibility = View.GONE
        else accessButton?.visibility = View.VISIBLE
        */
    }

    /**
     * When the delete button is clicked, delete the notification settinsg for the app name that is currently typed
     */
    fun deleteNotification(name: String) {
        // Get list of notification channels
        var list1: List<NotificationChannel> = ArrayList()
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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

        // Tell user it's been deleted
        Toast.makeText(this, name + " notification deleted", Toast.LENGTH_SHORT).show()
    }

    // When app is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create buttons, textViews, editTexts, checkboxes, etc.
        accessText = findViewById<TextView>(R.id.accessText)
        vibrationText = findViewById<TextView>(R.id.vibrationText)
        listHeader = findViewById<TextView>(R.id.listHeader)
        infoTooltip = findViewById<TextView>(R.id.infoTooltip)
        instructionsButton = findViewById<Button>(R.id.instructionsButton)
        accessButton = findViewById<Button>(R.id.accessButton)
        createButton = findViewById<Button>(R.id.createButton)
        editAppName = findViewById<EditText>(R.id.editAppName)
        edgeCheck = findViewById<CheckBox>(R.id.edgeCheck)
        infoIcon = findViewById<ImageButton>(R.id.infoIcon)
        notificationList = findViewById<RecyclerView>(R.id.notificationList)

        // Create spinner (drop down menu)
        vibrateSpinner = findViewById<Spinner>(R.id.vibrateSpinner)
        // Create an ArrayAdapter using the string array defined in strings.xml and spinner layout you created
        val vibrationNames = vibrations.map { vib -> vib.name }
        vibrateSpinner!!.adapter = ArrayAdapter(this, R.layout.spinner, vibrationNames)

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

        // Show list of notifications
        getList()

        // Check to see if they have granted permission to notification access
        getNotificationAccess()
    }

    override fun onResume() {
        super.onResume()
        // We have to do this here also so if they gave access and came back, the UI updates
        getNotificationAccess()
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
        // Vibration option selected
        val choice = vibrateSpinner?.selectedItem.toString()
        // Set up vibrator
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Vibrate device based on choice
        val vibration = vibrations.find { vib -> vib.name == choice }
        if (vibration != null && vibration.name != "none") {
            // If you try to vibrate with the "none" pattern, it will break
            // TODO test an actual notification with the none pattern
            v.vibrate(
                VibrationEffect.createWaveform(vibration.pattern, -1) // -1 means don't repeat
            )
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
     * When the create button is clicked, create a vibration and edge lighting for the info typed/selected
     */
    fun createClicked(view: View) {
        // Get name from editText field and convert to String
        val name: String = editAppName?.getText().toString()

        // Create as long as they inputted a name
        if (name != "") {
            // Delete notification channels with that name in case they are editing existing one
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

            // Create channel ids
            var CHANNEL_ID1 = name + "ScreenOn"
            var name1 = name + " Screen On"

            // Change screen off channel name according to if wake screen is on or off
            var CHANNEL_ID2 = ""
            var name2 = ""
            // TODO !! vs ?
            if (edgeCheck!!.isChecked) { //!! is just safety for non null
                CHANNEL_ID2 = name + "ScreenOffWakeUp"
                name2 = name + " Screen Off (With Edge Lighting)"
            } else {
                CHANNEL_ID2 = name + "ScreenOff"
                name2 = name + " Screen Off (No Edge Lighting)"
            }

            // Add vibration pattern chosen to channel ids
            val vibrateChoice = vibrateSpinner?.getSelectedItem().toString()
            CHANNEL_ID1 += vibrateChoice
            CHANNEL_ID2 += vibrateChoice

            // Create notification channel for screen on
            val importance1 = NotificationManager.IMPORTANCE_DEFAULT //This is no popup
            val mChannel1 = NotificationChannel(CHANNEL_ID1, name1, importance1)
            mChannel1.description = "When the screen is on so no popup"

            // Create notification channel for screen off
            val importance2 = NotificationManager.IMPORTANCE_HIGH //This is a popup
            val mChannel2 = NotificationChannel(CHANNEL_ID2, name2, importance2)
            mChannel2.description = "When the screen is off so yes popup"

            // Set vibration patterns for both channels
            val vibration = vibrations.find { vib -> vib.name == vibrateChoice }
            if (vibration != null) {
                mChannel1.vibrationPattern = vibration.pattern
                mChannel2.vibrationPattern = vibration.pattern

                if (vibration.name === "none") {
                    // Silent because they probably also don't wanna change the sound then
                    mChannel1.setSound(null, null)
                    mChannel2.setSound(null, null)
                }
            }

            // Actually create both notification channels
            notificationManager.createNotificationChannel(mChannel1)
            notificationManager.createNotificationChannel(mChannel2)

            // Tell user it's been created
            Toast.makeText(this, name + " notification created", Toast.LENGTH_SHORT).show()

            // Add to the notification list
            var vibrate = vibrateChoice
            if (vibrate == "none") vibrate = "no vibration"
            val data = NotificationData(name, vibrate, edgeCheck!!.isChecked) { deleteNotification(name) } // This is how you pass in a function for a param
            myAdapter.addNotification(data)
        }
    }
}