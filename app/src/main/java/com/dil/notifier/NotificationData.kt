package com.dil.notifier

data class NotificationData(
    val name: String,
    val text: String,
    val delete: () -> Unit
)
