package com.pos.cashiersp.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun parseDateString(input: String): Calendar {
    val normalized = input
        .replace(Regex("(\\.\\d{3})\\d+"), "$1")          // microseconds → milliseconds
        .replace(Regex("([+-]\\d{2}):(\\d{2})$"), "$1$2") // +09:00 → +0900
        .replace(Regex("Z$"), "+0000")                     // Z → +0000

    // normalized handles all cases:
    // "2026-03-31T08:48:47.179Z"       → "2026-03-31T08:48:47.179+0000"
    // "2026-03-31T17:48:47.179+09:00"  → "2026-03-31T17:48:47.179+0900"
    // "2026-03-31T08:48:47.179418Z"    → "2026-03-31T08:48:47.179+0000"

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
    val date = sdf.parse(normalized)!! // ✅ use normalized, not input

    return Calendar.getInstance(sdf.timeZone).apply {
        time = date
    }
}
