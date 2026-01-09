package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
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
    Surface(
        color = if (active) Color.Transparent else White,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(50),
        border = BorderStroke(
            .4.dp,
            if (active) Primary else Gray100
        )
    ) {
        Box(
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(34.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
            ) {
                icon()
                Spacer(Modifier.width(8.dp))
                Text(
                    paymentMethodName,
                    color = if (active) Primary else Secondary,
                    fontSize = 12.sp,
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
    }
}