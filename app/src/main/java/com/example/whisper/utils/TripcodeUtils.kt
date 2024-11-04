package com.example.whisper.utils

import java.security.MessageDigest
import android.util.Base64

object TripcodeUtils {
    fun generateTripcode(input: String): String {
        return try {
            // Get the SHA-256 hash of the input
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray())

            // Convert to Base64 and take first 12 chars
            val tripcode = Base64.encodeToString(hash, Base64.NO_WRAP)
                .replace(Regex("[^a-zA-Z0-9]"), "") // Remove special characters
                .take(12)

            tripcode
        } catch (e: Exception) {
            "ERROR"
        }
    }
}