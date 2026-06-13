package com.pos.cashiersp.presentation.invoice_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.domain.PurchasedItem
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.util.toRupiah

// Single Item Row
@Composable
fun ItemRow(
    index: Int,
    item: PurchasedItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Index badge
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Gray100.copy(alpha = .6f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = index.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.W600,
                color = Gray600,
            )
        }

        // Name + meta
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.itemNameSnapshot,
                fontSize = 13.sp,
                fontWeight = FontWeight.W600,
                color = Secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            val meta = buildString {
                append("Qty ${item.quantity}")
                if (item.discountAmount > 0) append(" · -${item.discountAmount.toRupiah()}")
            }
            Text(
                text = meta,
                fontSize = 11.sp,
                color = Gray500,
                fontWeight = FontWeight.W400,
            )
        }

        // Price column
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.totalAmount.toRupiah(),
                fontSize = 14.sp,
                fontWeight = FontWeight.W700,
                color = Primary,
            )
            Text(
                text = "unit ${item.storePriceSnapshot.toRupiah()}",
                fontSize = 10.sp,
                color = Gray400,
            )
        }
    }
}