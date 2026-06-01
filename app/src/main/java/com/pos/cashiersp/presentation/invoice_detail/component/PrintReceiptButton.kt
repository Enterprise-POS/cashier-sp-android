package com.pos.cashiersp.presentation.invoice_detail.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
fun PrintReceiptButton(viewModel: InvoiceDetailViewModel = hiltViewModel()) {
    Button(
        onClick = { viewModel.onEvent(InvoiceDetailEvent.OnClickPrintReceiptBtn) },
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Print,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Print receipt",
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
            color = White
        )
    }
}