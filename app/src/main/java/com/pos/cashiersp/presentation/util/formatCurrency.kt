package com.pos.cashiersp.presentation.util

import java.text.NumberFormat
import java.util.Locale

fun Number.toRupiah(): String {
    val localeID = Locale("id", "ID")
    val doubleValue = this.toDouble()

    // Create the currency formatter instance
    val currencyFormat = NumberFormat.getCurrencyInstance(localeID)

    // Hide trailing decimal zeros common in IDR (.00)
    currencyFormat.maximumFractionDigits = 0

    //    return when {
    //        amount >= 1_000_000 -> "Rp %.2fM".format(amount / 1_000_000.0)
    //        amount >= 1_000 -> "Rp %.0fK".format(amount / 1_000.0)
    //        else -> "Rp $amount"
    //    }

    return currencyFormat.format(doubleValue)
}