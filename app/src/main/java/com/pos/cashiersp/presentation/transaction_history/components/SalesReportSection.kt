package com.pos.cashiersp.presentation.transaction_history.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryViewModel
import com.pos.cashiersp.presentation.ui.theme.Dark
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SalesReportSection(
    modifier: Modifier = Modifier,
    viewModel: TransactionHistoryViewModel = hiltViewModel(),
) {
    val searchTransactionsDto = viewModel.searchTransactionsDto.value

    val currentPage = 1
    val itemsPerPage = 10
    val totalItems = 100
    val salesData = searchTransactionsDto?.orderItems?.map { it.toDomain() }

    Spacer(Modifier.height(8.dp))
    // Showing text
    TextWithNoPadding(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        text = "Showing ${((currentPage - 1) * itemsPerPage) + 1}-${
            minOf(
                currentPage * itemsPerPage,
                totalItems
            )
        } of $totalItems",
        color = Gray600,
        fontSize = 12.sp,
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        salesData?.forEach { item(key = it.id) { SaleCard(it) } }
    }
}

@Composable
fun SaleCard(sale: OrderItem) {
    // Calculate date range based on selected period
    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy - HH:mm",
        Locale.getDefault()
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to details */ },
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Section
            Column(
                modifier = Modifier.fillMaxWidth(.72f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ID and Time
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${sale.id}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Dark,
                        fontSize = 16.sp,
                    )
                }

                // Details in compact row format
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompactDetail(
                        label = "Cash-in",
                        value = "¥${sale.purchasedPrice}",
                        modifier = Modifier.weight(1f)
                    )
                    CompactDetail(
                        label = "Change",
                        value = "¥${sale.purchasedPrice - sale.totalAmount}",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Date
                Text(
                    text = dateFormat.format(sale.createdAt.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400,
                    fontSize = 11.sp
                )
            }

            // Right Section - Total Amount
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.height(68.dp)
            ) {
                TextWithNoPadding(
                    text = "¥${sale.subtotal}",
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View details",
                    tint = Gray300,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CompactDetail(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = Gray500,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Secondary,
            fontSize = 11.sp
        )
    }
}
