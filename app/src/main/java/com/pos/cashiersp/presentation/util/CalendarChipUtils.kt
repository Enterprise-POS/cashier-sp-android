package com.pos.cashiersp.presentation.util

import java.util.Calendar

class CalendarChipUtils {
    companion object {
        @JvmStatic
        fun nowEpoch(): Long = System.currentTimeMillis() / 1000

        @JvmStatic
        fun startOfDayEpoch(epochSeconds: Long): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = epochSeconds * 1000
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis / 1000
        }

        @JvmStatic
        fun endOfDayEpoch(epochSeconds: Long): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = epochSeconds * 1000
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis / 1000
        }
    }
}