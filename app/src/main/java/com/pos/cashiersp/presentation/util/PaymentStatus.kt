package com.pos.cashiersp.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.pos.cashiersp.presentation.ui.theme.Danger800
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Indigo500
import com.pos.cashiersp.presentation.ui.theme.Info
import com.pos.cashiersp.presentation.ui.theme.Orange500
import com.pos.cashiersp.presentation.ui.theme.Success

enum class PaymentStatus(val uiLabel: String) {
    SUCCESS("Success"),
    PENDING("Pending"),
    REFUNDED("Refunded"),
    FAILED("Failed"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled"),
    PARTIALY_REFUNDED("Partialy Refunded")
}

/**
 * Visual style (label, color, icon) for each transaction status.
 * Kept to a small, learnable palette:
 *  - green   = success
 *  - amber   = pending / in-progress
 *  - red     = failed
 *  - slate   = cancelled
 *  - gray    = expired
 *  - blue/indigo = refund variants
 */

fun PaymentStatus.toStyle(): StatusStyle = when (this) {
    PaymentStatus.SUCCESS -> StatusStyle(uiLabel, Success, Icons.Default.CheckCircle)
    PaymentStatus.PENDING -> StatusStyle(uiLabel, Orange500, Icons.Default.Schedule)
    PaymentStatus.PARTIALY_REFUNDED -> StatusStyle(uiLabel, Indigo500, Icons.Default.Replay)
    PaymentStatus.FAILED -> StatusStyle(uiLabel, Danger800, Icons.Default.Cancel)
    PaymentStatus.CANCELLED -> StatusStyle(uiLabel, Gray300, Icons.Default.Block)
    PaymentStatus.EXPIRED -> StatusStyle(uiLabel, Gray500, Icons.Default.History)
    PaymentStatus.REFUNDED -> StatusStyle(uiLabel, Info, Icons.Default.Replay)
}

data class StatusStyle(
    val label: String,
    val color: Color,
    val icon: ImageVector,
)
