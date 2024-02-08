package com.dil.notifier

data class NotificationData(
    val name: String,
    val vibration: String,
    val edge: Boolean,
    val delete: () -> Unit
)
// TODO See about adding equals function here
