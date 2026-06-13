package com.pos.cashiersp.presentation.invoice_detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.invoice_detail.InvoiceDetailViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary200
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.dateFormatter
import com.pos.cashiersp.presentation.util.toRupiah

@Composable
fun InvoiceHeaderCard(viewModel: InvoiceDetailViewModel = hiltViewModel()) {
    var orderItem = viewModel.orderItem.value!!
    val date = dateFormatter(orderItem.createdAt, "dd MMM yyyy")
    val time = dateFormatter(orderItem.createdAt, "HH:mm")

    Card(
        border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f)),
        colors = CardDefaults.cardColors(containerColor = Primary100),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Invoice number + paid badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    TextWithNoPadding(
                        text = "INV-ID-${orderItem.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700,
                        color = Secondary
                    )
                    Spacer(Modifier.height(2.dp))
                    TextWithNoPadding(
                        text = "${orderItem.storeName} · Store #${orderItem.storeId}",
                        fontSize = 12.sp,
                        color = Gray400
                    )
                }

                // Paid badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Paid",
                            fontSize = 11.sp,
                            color = White,
                            fontWeight = FontWeight.W500
                        )
                        Text(
                            text = orderItem.totalAmount.toRupiah(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W700,
                            color = White
                        )
                    }
                }
            }

            // Date / Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoBox(label = "Date", value = date, modifier = Modifier.weight(1f))
                InfoBox(label = "Time", value = time, modifier = Modifier.weight(1f))
            }

            // Items / Tendered
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoBox(
                    label = "Total quantity",
                    value = "${orderItem.totalQuantity} item${if (orderItem.totalQuantity > 1) "s" else ""}",
                    modifier = Modifier.weight(1f)
                )
                InfoBox(label = "Cash-in", value = orderItem.purchasedPrice.toRupiah(), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(.8.dp, Primary200, RoundedCornerShape(8.dp))
            .background(White)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = label, fontSize = 11.sp, color = Gray400, fontWeight = FontWeight.W400)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.W600, color = Secondary)
        }
    }
}