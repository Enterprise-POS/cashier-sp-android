package com.pos.cashiersp.presentation.item_sales_log.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.presentation.item_sales_log.SalesLogScope
import com.pos.cashiersp.presentation.item_sales_log.SortColumn
import com.pos.cashiersp.presentation.item_sales_log.TransactionRecordUi
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Primary200
import com.pos.cashiersp.presentation.ui.theme.PrimaryHover
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Success
import com.pos.cashiersp.presentation.ui.theme.White

private val sampleTransactions = listOf(
    TransactionRecordUi(
        "#2896",
        "Mojito Classic",
        "Rp 88,000",
        1,
        "14 Jan 2026, 11:18",
        "Rp 88,000",
        88_000,
        1768389480
    ),
    TransactionRecordUi(
        "#2841",
        "Mojito Classic",
        "Rp 176,000",
        2,
        "13 Jan 2026, 09:45",
        "Rp 88,000",
        176_000,
        1768297500
    ),
    TransactionRecordUi(
        "#2810",
        "Mojito Classic",
        "Rp 264,000",
        3,
        "12 Jan 2026, 14:22",
        "Rp 90,000",
        264_000,
        1768227720
    ),
    TransactionRecordUi(
        "#2779",
        "Espresso Shot",
        "Rp 45,000",
        1,
        "11 Jan 2026, 18:05",
        "Rp 45,000",
        45_000,
        1768154700
    ),
    TransactionRecordUi(
        "#2751",
        "Espresso Shot",
        "Rp 180,000",
        4,
        "10 Jan 2026, 10:30",
        "Rp 45,000",
        180_000,
        1768041000
    ),
)

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun LogsRow(paddingValues: PaddingValues) {
    val focusedItemName = "Mojito Classic"

    // Default to null so we can force the user to pick a scope on first open.
    var selectedScope by rememberSaveable { mutableStateOf<SalesLogScope?>(null) }

    // --- Applied sort + date filter state (this is what gets sent to the backend later) ---
    var sortColumn by rememberSaveable { mutableStateOf(SortColumn.DATE) }
    var sortAscending by rememberSaveable { mutableStateOf(false) }
    var dateFilterStart by rememberSaveable { mutableStateOf<Long?>(null) }
    var dateFilterEnd by rememberSaveable { mutableStateOf<Long?>(null) }

    val scopedTransactions = remember(selectedScope) {
        when (selectedScope) {
            SalesLogScope.SINGLE_ITEM -> sampleTransactions.filter { it.itemName == focusedItemName }
            SalesLogScope.ALL_ITEMS -> sampleTransactions
            null -> emptyList()
        }
    }

    // Applies the current filters/sort locally so the UI reflects them —
    // the same "filters" + "date_filter" shape is what you'd send to the API instead.
    val displayedTransactions =
        remember(scopedTransactions, sortColumn, sortAscending, dateFilterStart, dateFilterEnd) {
            val dateFiltered = scopedTransactions.filter { t ->
                (dateFilterStart == null || t.epochSeconds >= dateFilterStart!!) &&
                        (dateFilterEnd == null || t.epochSeconds <= dateFilterEnd!!)
            }
            val comparator = when (sortColumn) {
                SortColumn.DATE -> compareBy<TransactionRecordUi> { it.epochSeconds }
                SortColumn.TOTAL_AMOUNT -> compareBy { it.revenue }
                SortColumn.QUANTITY -> compareBy { it.quantity }
            }
            if (sortAscending) dateFiltered.sortedWith(comparator) else dateFiltered.sortedWith(comparator.reversed())
        }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayedTransactions, key = { it.orderId }) { transaction ->
                TransactionRecordCard(transaction)
            }
        }
    }
}

@Composable
private fun TransactionRecordCard(transaction: TransactionRecordUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order ${transaction.orderId}",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Secondary)
                )
                Text(
                    text = transaction.price,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Success)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.itemName,
                    style = TextStyle(fontSize = 13.sp, color = Gray400)
                )
                QuantityBadge(quantity = transaction.quantity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.date,
                    style = TextStyle(fontSize = 12.sp, color = Gray400)
                )
                Text(
                    text = "Store price: ${transaction.storePrice}",
                    style = TextStyle(fontSize = 12.sp, color = Secondary)
                )
            }
        }
    }
}

@Composable
private fun QuantityBadge(quantity: Int) {
    val unitLabel = if (quantity == 1) "unit" else "units"
    Box(
        modifier = Modifier
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
