package com.example.whisper.utils

import com.example.whisper.data.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentTime(): Long {
    return System.currentTimeMillis()
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
}

fun formatDateTime(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
}

fun List<Message>.sortByTimestamp(descending: Boolean = true): List<Message> {
    return if (descending) {
        sortedByDescending { it.timestamp }
    } else {
        sortedBy { it.timestamp }
    }
}