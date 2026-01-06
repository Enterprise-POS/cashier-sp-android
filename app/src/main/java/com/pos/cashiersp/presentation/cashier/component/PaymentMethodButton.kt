package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.PaymentMethod

@Composable
fun PaymentMethodButton(
    paymentMethodName: String,
    active: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    // Column will make text in the bottom middle
    Column {
        IconButton(
            enabled = true,
            modifier = Modifier
                .fillMaxSize() // This center the icon
                .border(
                    width = 1.dp,
                    color = if (active) Primary else White,
                    shape = RoundedCornerShape(8.dp)
                ),
            onClick = onClick
        ) {
            icon()
            /*
            Icon(
                Icons.Filled.AddCircle,
                tint = Primary,
                contentDescription = "Add ... payment method",
                modifier = Modifier.size(24.dp)
            )
             */
        }

        Spacer(Modifier.height(8.dp))
        Text(
            paymentMethodName,
            color = if (active) Primary else White,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTextStyle.current.merge(
                TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    )
                ),
            ),
        )
    }
}