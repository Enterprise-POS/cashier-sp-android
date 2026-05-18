package com.pos.cashiersp.presentation.util

import android.util.Log
import java.time.OffsetDateTime
import java.util.Calendar

fun parseDateString(dateString: String): Calendar? {
    return try {
        val offsetDateTime = OffsetDateTime.parse(dateString)
        Calendar.getInstance().apply {
            timeInMillis = offsetDateTime.toInstant().toEpochMilli()
        }
    } catch (e: Exception) {
        Log.w("parseDateString", "Unable to parse date: $dateString")
        null
    }
}