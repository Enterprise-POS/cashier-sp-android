package com.pos.cashiersp.presentation.invoice_detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.presentation.invoice_detail.InvoiceDetailEvent
import com.pos.cashiersp.presentation.invoice_detail.InvoiceDetailViewModel
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun CheckTransactionStatusButton(
    viewModel: InvoiceDetailViewModel = hiltViewModel(),
) {
    val paymentStatusState = viewModel.paymentStatusState.value
    val enableButton = !paymentStatusState.isLoading

    OutlinedButton(
        onClick = { viewModel.onEvent(InvoiceDetailEvent.OnClickCheckTransaction) },
        enabled = enableButton,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = Primary,
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enableButton) Primary else Primary.copy(alpha = 0.4f),
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (paymentStatusState.isLoading) "Checking status..." else "Check transaction",
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
        )
    }
}