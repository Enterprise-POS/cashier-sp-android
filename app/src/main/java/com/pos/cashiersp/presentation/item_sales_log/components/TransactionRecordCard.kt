package com.pos.cashiersp.presentation.item_sales_log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.domain.PurchasedItem
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary200
import com.pos.cashiersp.presentation.ui.theme.PrimaryHover
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Success
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.dateFormatter
import com.pos.cashiersp.presentation.util.toRupiah

@Composable
fun TransactionRecordCard(
    purchasedItem: PurchasedItem,
    modifier: Modifier = Modifier,
    onClickSeeDetail: (orderItemId: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    expanded = true
                }
            ),
    ) {
        Column(modifier = modifier.padding(14.dp)) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order ${purchasedItem.orderItemId}",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Secondary)
                )
                Text(
                    text = purchasedItem.totalAmount.toRupiah(),
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Success)
                )
            }

            Spacer(modifier = modifier.height(4.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = purchasedItem.itemNameSnapshot,
                    style = TextStyle(fontSize = 13.sp, color = Gray400)
                )
                QuantityBadge(quantity = purchasedItem.quantity, modifier)
            }

            Spacer(modifier = modifier.height(8.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = purchasedItem.createdAt?.let {
                        dateFormatter(
                            purchasedItem.createdAt,
                            "dd MMM yyyy - HH:mm"
                        )
                    } ?: "Time Error",
                    style = TextStyle(fontSize = 12.sp, color = Gray400)
                )
                Text(
                    text = "Store price: ${purchasedItem.storePriceSnapshot.toRupiah()}",
                    style = TextStyle(fontSize = 12.sp, color = Secondary)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Primary100,
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = "See detail",
                                tint = Secondary,
                            )
                            Spacer(Modifier.width(4.dp))
                            TextWithNoPadding("See order", color = Secondary, fontSize = 14.sp)
                        }
                    },
                    onClick = {
                        expanded = false
                        onClickSeeDetail(purchasedItem.orderItemId)
                    }
                )
            }
        }
    }
}

@Composable
private fun QuantityBadge(quantity: Int, modifier: Modifier) {
    val unitLabel = if (quantity == 1) "unit" else "units"
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Primary200)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "x $quantity $unitLabel",
            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = PrimaryHover)
        )
    }
}
