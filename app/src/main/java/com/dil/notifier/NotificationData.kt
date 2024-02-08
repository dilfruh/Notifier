package com.dil.notifier

data class NotificationData(
    val name: String,
    val vibration: String,
    val edge: Boolean,
    val delete: () -> Unit
) {
    //! DON'T FORGET TO CHANGE THIS IF YOU CHANGE THE DATA CLASS
    // This we need to use in NotificationListAdapter
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationData

        if (name != other.name) return false
        if (vibration != other.vibration) return false
        if (edge != other.edge) return false
        return delete != other.delete
    }

    // This was generated automatically. IDK what it does
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + vibration.hashCode()
        result = 31 * result + edge.hashCode()
        result = 31 * result + delete.hashCode()
        return result
    }
}
