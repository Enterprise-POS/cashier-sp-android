package com.pos.cashiersp.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun dateFormatter(calendar: Calendar, pattern: String): String {
    val dateFormat = SimpleDateFormat(
        pattern,
        Locale.getDefault()
    )
    var date = calendar.time

    return dateFormat.format(date)
}

fun dateFormatter(date: Date, pattern: String): String {
    val dateFormat = SimpleDateFormat(
        pattern,
        Locale.getDefault()
    )

    return dateFormat.format(date)
}
