package com.dil.notifier
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback

/**
 * This is a custom adapter to the RecyclerView for our notificationList. Call addNotification to add a notification to the list and removeNotification to remove one
 */
class NotificationListAdapter() :
    RecyclerView.Adapter<NotificationListAdapter.MyRecyclerViewDataHolder>() {
    inner class MyRecyclerViewDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // items that will be shown in the RecyclerView list
    private val items: SortedList<NotificationData>

    init {
        items = SortedList(
            NotificationData::class.java,
            object : SortedListAdapterCallback<NotificationData>(this) {
                override fun compare(a: NotificationData, b: NotificationData): Int {
                    // Sort by name in ascending order
                    return a.name.compareTo(b.name)
                }

                override fun areItemsTheSame(item1: NotificationData, item2: NotificationData): Boolean {
                    // Check if two items represent the same data (unique identifier)
                    // So if you add an item with the same name, but different content. It will remove the old content and add the new (basically update)
                    return item1.name == item2.name
                }

                override fun areContentsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean {
                    // Check if two items have the same content. See if it has been updated
                    return oldItem.equals(newItem)
                }
            }
        )
    }
    // Say the layout for each item in the RecyclerView is based on notification_list_item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewDataHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_list_item, parent, false)
        return MyRecyclerViewDataHolder(view)
    }

    // Update the layout for notification_list_item based on the position (index of the item in the items we passed in
    override fun onBindViewHolder(holder: MyRecyclerViewDataHolder, position: Int) {
        // Display text content of data in list
        val item: NotificationData = items[position]

        val appName: TextView = holder.itemView.findViewById(R.id.appName)
        // Make the app name bold and underlined
        val name: Spannable = SpannableString(item.name)
        name.setSpan(StyleSpan(Typeface.BOLD), 0, item.name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        name.setSpan(UnderlineSpan(), 0, item.name.length, 0)
        appName.text = name

        val vibrationText: TextView = holder.itemView.findViewById(R.id.vibrationText)
        vibrationText.text = item.vibration

        val edgeText: TextView = holder.itemView.findViewById(R.id.edgeText)
        if (item.edge) edgeText.text = "Edge Lighting"
        else edgeText.text = "No Edge Lighting"

        val deleteButton: ImageButton = holder.itemView.findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            // Show an "Are you sure?" popup before deleting
            val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
            builder
                .setMessage("Delete your custom notification for " + item.name + "?")
                .setTitle("Are you sure?")
                .setPositiveButton("Delete") { dialog, which ->
                    // Perform the delete function
                    item.delete() // This had to be passed in because some classes/functions couldn't be resolved here

                    // Remove it from the list
                    removeNotification(position)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    // Do nothing
                }
            // Actually show it
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    // For some reason we have to do this
    override fun getItemCount(): Int {
        return items.size()
    }

    /*** Functions used to add and remove items. Call these after creating your adapter ***/

    /**
     * adds an NotificationData item to the list for the adapter to show
     * @param item Notification data you want to add
     */
    fun addNotification(item: NotificationData) {
        items.add(item)

        // We don't have to do this now that we used a SortedList
        // Tell the recyclerView that the list has changed, so it will update the screen and remove the item
        // notifyItemInserted(position)
    }

    /**
     * removes an NotificationData item to the list that the adapter shows at the inputted index
     * @param index index in the list of the Notification data you want to remove
     */
    fun removeNotification(index: Int) {
        items.removeItemAt(index)

        // We don't have to do this now that we used a SortedList
        // Tell the recyclerView that the list has changed, so it will update the screen and remove the item
        // notifyItemRemoved(position)
    }
}