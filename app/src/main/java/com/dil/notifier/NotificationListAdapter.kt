package com.dil.notifier
import android.app.NotificationChannel
import android.app.NotificationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class NotificationListAdapter(private val items: MutableList<(NotificationData)>) :
    RecyclerView.Adapter<NotificationListAdapter.MyRecyclerViewDataHolder>() {
    inner class MyRecyclerViewDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Say the layout for each item in the RecyclerView is based on notification_list_item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewDataHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_list_item, parent, false)
        return MyRecyclerViewDataHolder(view)
    }

    // Update the layout for notification_list_item based on the position (index of the item in the items we passed in
    override fun onBindViewHolder(holder: MyRecyclerViewDataHolder, position: Int) {
        val item: NotificationData = items[position]

        val appName: TextView = holder.itemView.findViewById(R.id.appName)
        appName.text = item.name

        val deleteButton: Button = holder.itemView.findViewById(R.id.deleteButton)
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
                    items.removeAt(position)
                    // Tell the recyclerView that the list has changed, so it will update the screen and remove the item
                    notifyItemRemoved(position)
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
        return items.size
    }
}