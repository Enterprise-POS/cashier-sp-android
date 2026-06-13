package com.pos.cashiersp.presentation.invoice_detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.pos.cashiersp.R
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.invoice_detail.InvoiceDetailViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Gray800
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.toRupiah

@Composable
fun PaymentSummarySection(viewModel: InvoiceDetailViewModel = hiltViewModel()) {
    val orderItem = viewModel.orderItem.value!!

    Card(
        border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f)),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextWithNoPadding(
                    "Payment summary",
                    fontSize = 16.sp,
                    color = Secondary,
                    fontWeight = FontWeight.W500,
                )
                Text(text = "Cash", fontSize = 13.sp, color = Gray400)
            }

            HorizontalDivider(color = Gray100.copy(alpha = .6f), thickness = .8.dp)

            PaymentRow(label = "Subtotal", value = orderItem.subtotal.toRupiah())
            PaymentRow(label = "Discount", value = orderItem.discountAmount.toRupiah())
            PaymentRow(label = "Cash-in", value = orderItem.purchasedPrice.toRupiah())

            HorizontalDivider(color = Gray100.copy(alpha = .6f), thickness = .8.dp)

            // Total paid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total paid",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    color = Secondary
                )
                Text(
                    text = orderItem.totalAmount.toRupiah(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    color = Primary
                )
            }

            // Payment method box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Primary100, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(R.raw.cash_payment_method_icon)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Cash payment",
                            colorFilter = ColorFilter.tint(Primary),
                            modifier = Modifier
                                .size(18.dp)
                        )
                        Column {
                            Text(text = "Method", fontSize = 10.sp, color = Gray400)
                            Text(
                                text = "Cash payment",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.W600,
                                color = Secondary
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Change", fontSize = 10.sp, color = Gray400)
                        Text(
                            text = (orderItem.purchasedPrice - orderItem.totalAmount).toRupiah(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700,
                            color = Secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Gray500, fontWeight = FontWeight.W400)
        Text(text = value, fontSize = 13.sp, color = Gray800, fontWeight = FontWeight.W500)
    }
}

