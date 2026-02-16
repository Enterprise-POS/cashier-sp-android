package com.pos.cashiersp.presentation.transaction_history.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.model.domain.OrderItem
import com.pos.cashiersp.model.dto.SearchTransactionsDto
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.transaction_history.ItemsPerPage
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryEvent
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryViewModel
import com.pos.cashiersp.presentation.ui.theme.Dark
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SalesReportSection(
    modifier: Modifier = Modifier,
    viewModel: TransactionHistoryViewModel = hiltViewModel(),
) {
    val searchTransactionsDto: SearchTransactionsDto? = viewModel.searchTransactionsDto.value
    val itemsPerPage: ItemsPerPage = viewModel.itemsPerPage.value
    val isRequesting = viewModel.isRequesting.value
    var currentPage = 0
    var totalItems = 0
    val salesData = mutableListOf<OrderItem>()
    if (searchTransactionsDto != null) {
        searchTransactionsDto.orderItems.forEach { salesData.add(it.toDomain()) }
        currentPage = searchTransactionsDto.page
        totalItems = searchTransactionsDto.totalCount
    }

    if (isRequesting) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.height(6.dp))
                Text(
                    "Loading report...",
                    color = Gray300,
                )
            }
        }
    } else if (searchTransactionsDto != null && salesData.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        // Showing text
        TextWithNoPadding(
            text = "Showing ${((currentPage - 1) * itemsPerPage.value) + 1}-${
                minOf(
                    currentPage * itemsPerPage.value,
                    totalItems
                )
            } of $totalItems",
            color = Gray600,
            fontSize = 12.sp,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            salesData.forEach { item(key = it.id) { SaleCard(it, viewModel) } }
        }
        Spacer(Modifier.height(8.dp))
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No report...",
                color = Gray300,
            )
        }
    }
}

// Define Locale here must restart the app to see change
private val dateFormat = SimpleDateFormat(
    "dd MMM yyyy - HH:mm",
    Locale.getDefault()
)

@Composable
private fun SaleCard(sale: OrderItem, viewModel: TransactionHistoryViewModel) {
    var expanded by remember { mutableStateOf(false) }

    // Calculate date range based on selected period
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        println("Tap")
                    },
                    onLongClick = {
                        expanded = true
                    }
                ),
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

                    // Place here so the drop down will render at the right
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = Primary100
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Print,
                                        contentDescription = "Print shortcut",
                                        tint = Secondary,
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    TextWithNoPadding("Print", color = Secondary, fontSize = 14.sp)
                                }
                            },
                            onClick = {
                                expanded = false
                                viewModel.onEvent(TransactionHistoryEvent.OnLongPressedAndClickPrint(sale.id))
                            }
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun CompactDetail(
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
