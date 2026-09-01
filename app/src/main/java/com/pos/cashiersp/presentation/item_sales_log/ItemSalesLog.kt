package com.pos.cashiersp.presentation.item_sales_log

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.domain.PurchasedItem
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.item_sales_log.components.FilterBottomSheet
import com.pos.cashiersp.presentation.item_sales_log.components.OrderSearchAndSortBar
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary200
import com.pos.cashiersp.presentation.ui.theme.PrimaryHover
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.Success
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.dateFormatter
import com.pos.cashiersp.presentation.util.toRupiah
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class TransactionRecordUi(
    val orderId: String,
    val itemName: String,
    val price: String,
    val quantity: Int,
    val date: String,
    val storePrice: String,
    val revenue: Long, // raw numeric value, used to compute stats/sorting correctly
    val epochSeconds: Long // placeholder demo timestamps — swap for real backend values
)

/** Which scope of logs the user wants to see. */
enum class SalesLogScope {
    ALL_ITEMS,
    SINGLE_ITEM
}

/**
 * Sortable columns, matching the "column" values the backend request expects
 * (see [SortFilterUi.column]).
 */
enum class SortColumn(val apiName: String, val displayName: String) {
    @SerializedName("created_at")
    DATE("created_at", "Date"),

    @SerializedName("total_amount")
    TOTAL_AMOUNT("total_amount", "Total Amount"),

    @SerializedName("quantity")
    QUANTITY("quantity", "Quantity")
}

/** One entry of the request's "filters" array: { "column": ..., "ascending": ... } */
data class SortFilterUi(
    val column: String,
    val ascending: Boolean
)

/** The request's "date_filter" object: { "start_date": ..., "end_date": ... } */
data class DateFilterUi(
    val startDate: Long?,
    val endDate: Long?
)

/**
 * Mirrors the exact request shape:
 * {
 *   "filters": [ { "column": ..., "ascending": ... } ],
 *   "date_filter": { "start_date": ..., "end_date": ... }
 * }
 * UI-only for now — build this and hand it to your API layer when it's ready.
 */
data class SalesLogFilterRequest(
    val filters: List<SortFilterUi>,
    val dateFilter: DateFilterUi?
)

/** Which quick-select shortcut (if any) is currently active in the date range picker. */
enum class QuickRange(val label: String) {
    TODAY("Today"),
    LAST_HOUR("Last hour"),
    LAST_6_HOURS("Last 6 hours"),
    LAST_12_HOURS("Last 12 hours"),
    CUSTOM("Custom")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSalesLog(
    navController: NavController,
    drawerState: DrawerState,
    // The item the user came from (e.g. tapped "Mojito Classic" in a product list).
    // Used when scope == SINGLE_ITEM.
    focusedItemName: String = "Mojito Classic",
    viewModel: ItemSalesLogViewModel = hiltViewModel()
) {
    // Default to null so we can force the user to pick a scope on first open.
    val selectedScope = viewModel.selectedScope.value

    // --- Applied sort + date filter state (this is what gets sent to the backend later) ---
    val sortColumn = viewModel.sortColumn.value
    val sortAscending = viewModel.sortAscending.value
    val dateFilterStart = viewModel.dateFilterStart.value
    val dateFilterEnd = viewModel.dateFilterEnd.value
    val showFilterSheet = viewModel.showFilterSheet.value

    // The logs
    val allItemLogs = viewModel.allItemLogs.value
    val searchItemLogs = viewModel.searchItemLogs.value

    // Pagination
    val currentAllItemPage = viewModel.currentAllItemPage.value
    val totalAllItemPages = viewModel.totalAllItemPages.value
    val currentSearchItemPage = viewModel.currentSearchItemPage.value
    val totalSearchItemPages = viewModel.totalSearchItemPages.value

    val (scopedTransactions, currentPage, totalPages) = when (selectedScope) {
        SalesLogScope.SINGLE_ITEM -> Triple(searchItemLogs, currentSearchItemPage, totalSearchItemPages)
        SalesLogScope.ALL_ITEMS -> Triple(allItemLogs, currentAllItemPage, totalAllItemPages)
        null -> Triple(emptyList(), 0, 0)
    }


    // Applies the current filters/sort locally so the UI reflects them —
    // the same "filters" + "date_filter" shape is what you'd send to the API instead.
    /*
    val displayedTransactions =
        remember(scopedTransactions, sortColumn, sortAscending, dateFilterStart, dateFilterEnd) {
            val dateFiltered: List<TransactionRecordUi> = scopedTransactions.filter { t ->
                (dateFilterStart == null || t.epochSeconds >= dateFilterStart) &&
                        (dateFilterEnd == null || t.epochSeconds <= dateFilterEnd)
            }
            val comparator: Comparator<TransactionRecordUi> = when (sortColumn) {
                SortColumn.DATE -> compareBy { it.epochSeconds }
                SortColumn.TOTAL_AMOUNT -> compareBy { it.revenue }
                SortColumn.QUANTITY -> compareBy { it.quantity }
            }
            val sorted = dateFiltered.sortedWith(comparator)
            if (sortAscending) sorted else sorted.asReversed()
        }
    */

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Secondary100,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Column {
                        TextWithNoPadding(
                            "Item Sales Log",
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        TextWithNoPadding(
                            "Review and filter past sales",
                            fontSize = 12.sp,
                            color = Gray600,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to previous screen"
                        )
                    }
                }
            )
        },
        // Docked to the real bottom of the screen (Scaffold's bottomBar slot) —
        // always one thumb-reach away, regardless of how long the list above gets.
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Secondary100)
                    .navigationBarsPadding() // clears the system nav bar (back/home/recents)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                ScopeSelector(
                    selectedScope = selectedScope,
                    focusedItemName = focusedItemName,
                    onSelect = { viewModel.onEvent(ItemSalesLogEvent.OnSetScopeSelector(it)) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (selectedScope == null) {
                // Nothing picked yet — don't show stats/list/pagination.
                EmptyScopePrompt()
            } else {
                /*
                StatsSummaryCard(
                    transactionsCount = displayedTransactions.size,
                    unitsSold = displayedTransactions.sumOf { it.quantity },
                    totalRevenue = formatRupiah(displayedTransactions.sumOf { it.revenue })
                )

                Spacer(modifier = Modifier.height(12.dp))
                 */

                OrderSearchAndSortBar(
                    onSortClick = { viewModel.onEvent(ItemSalesLogEvent.OnSetFilterSheetState(true)) },
                    sortColumn,
                    sortAscending,
                    selectedScope
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(scopedTransactions) { purchasedItem ->
                        TransactionRecordCard(purchasedItem)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PaginationRow(
                    currentPage,
                    totalPages,
                    onPageChange = { viewModel.onEvent(ItemSalesLogEvent.OnChangePage(it)) })
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            onDismiss = { viewModel.onEvent(ItemSalesLogEvent.OnDismissFilterBottomSheet) },
            viewModel = viewModel
        )
    }
}

/**
 * The two entry buttons: "All Items" vs "This Item".
 * Shown as a segmented control; before a choice is made neither side is highlighted.
 */
@Composable
private fun ScopeSelector(
    selectedScope: SalesLogScope?,
    focusedItemName: String,
    onSelect: (SalesLogScope) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .border(1.dp, Gray100, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ScopeButton(
            modifier = Modifier.weight(1f),
            label = "Search Item",
            isSelected = selectedScope == SalesLogScope.SINGLE_ITEM,
            onClick = { onSelect(SalesLogScope.SINGLE_ITEM) }
        )
        ScopeButton(
            modifier = Modifier.weight(1f),
            label = "All Items",
            isSelected = selectedScope == SalesLogScope.ALL_ITEMS,
            onClick = { onSelect(SalesLogScope.ALL_ITEMS) }
        )
    }
}

@Composable
private fun ScopeButton(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(9.dp))
            .background(if (isSelected) Primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) White else Secondary
            )
        )
    }
}

@Composable
private fun EmptyScopePrompt() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Choose \"This Item\" or \"All Items\" below to view sales logs",
            style = TextStyle(fontSize = 13.sp, color = Gray400)
        )
    }
}

@Composable
private fun TransactionRecordCard(purchasedItem: PurchasedItem) {
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
                    text = "Order ${purchasedItem.itemId}",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Secondary)
                )
                Text(
                    text = purchasedItem.totalAmount.toRupiah(),
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
                    text = purchasedItem.itemNameSnapshot,
                    style = TextStyle(fontSize = 13.sp, color = Gray400)
                )
                QuantityBadge(quantity = purchasedItem.quantity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatter(purchasedItem.createdAt, "dd MMM yyyy - HH:mm"),
                    style = TextStyle(fontSize = 12.sp, color = Gray400)
                )
                Text(
                    text = "Store price: ${purchasedItem.storePriceSnapshot.toRupiah()}",
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

@Composable
private fun PaginationRow(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    if (totalPages <= 0) return // nothing to paginate

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageArrowButton(
            label = "<",
            enabled = currentPage > 1,
            onClick = { onPageChange(currentPage - 1) }
        )
        Spacer(modifier = Modifier.width(6.dp))

        for (page in visiblePages(currentPage, totalPages)) {
            if (page == ELLIPSIS) {
                Text(
                    text = "...",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = Gray400
                )
            } else {
                PageNumberButton(
                    number = page,
                    isActive = currentPage == page,
                    onClick = { onPageChange(page) }
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        PageArrowButton(
            label = ">",
            enabled = currentPage < totalPages,
            onClick = { onPageChange(currentPage + 1) }
        )
    }
}

private const val ELLIPSIS = -1

/**
 * Builds the list of page numbers/ellipsis markers to show, always keeping
 * first page, last page, and a window around currentPage — e.g. for
 * currentPage=5, totalPages=10 -> [1, ..., 4, 5, 6, ..., 10]
 */
private fun visiblePages(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 5) {
        return (1..totalPages).toList()
    }

    val pages = mutableListOf(1)

    val windowStart = (currentPage - 1).coerceAtLeast(2)
    val windowEnd = (currentPage + 1).coerceAtMost(totalPages - 1)

    if (windowStart > 2) pages.add(ELLIPSIS)
    pages.addAll(windowStart..windowEnd)
    if (windowEnd < totalPages - 1) pages.add(ELLIPSIS)

    pages.add(totalPages)
    return pages
}

@Composable
private fun PageNumberButton(number: Int, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Primary else White)
            .border(1.dp, if (isActive) Primary else Gray100, RoundedCornerShape(8.dp))
            .clickable(enabled = !isActive, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) White else Secondary
            )
        )
    }
}

@Composable
private fun PageArrowButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(White)
            .border(1.dp, Gray100, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.sp,
                color = if (enabled) Secondary else Gray400
            )
        )
    }
}