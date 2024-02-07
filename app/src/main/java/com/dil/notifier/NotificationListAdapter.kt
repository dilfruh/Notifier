package com.dil.notifier
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationListAdapter(private val items: List<(NotificationData)>) :
    RecyclerView.Adapter<NotificationListAdapter.MyRecyclerViewDataHolder>() {
    inner class MyRecyclerViewDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewDataHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_list_item, parent, false)
        return MyRecyclerViewDataHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewDataHolder, position: Int) {
        val item: NotificationData = items[position]

        val appName: TextView = holder.itemView.findViewById(R.id.appName)
        appName.text = item.name

        val deleteButton: Button = holder.itemView.findViewById(R.id.deleteButton)
        appName.setOnClickListener { item.delete() }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}