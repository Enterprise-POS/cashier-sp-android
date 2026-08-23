package com.pos.cashiersp.presentation.item_sales_log

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
import androidx.navigation.NavController
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
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
    DATE("date", "Date"),
    TOTAL_AMOUNT("total_amount", "Total Amount"),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSalesLog(
    navController: NavController,
    drawerState: DrawerState,
    // The item the user came from (e.g. tapped "Mojito Classic" in a product list).
    // Used when scope == SINGLE_ITEM.
    focusedItemName: String = "Mojito Classic"
) {
    // Default to null so we can force the user to pick a scope on first open.
    var selectedScope by rememberSaveable { mutableStateOf<SalesLogScope?>(null) }

    // --- Applied sort + date filter state (this is what gets sent to the backend later) ---
    var sortColumn by rememberSaveable { mutableStateOf(SortColumn.DATE) }
    var sortAscending by rememberSaveable { mutableStateOf(false) }
    var dateFilterStart by rememberSaveable { mutableStateOf<Long?>(null) }
    var dateFilterEnd by rememberSaveable { mutableStateOf<Long?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

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
                    onSelect = { selectedScope = it }
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
                    sortLabel = sortButtonLabel(sortColumn, sortAscending),
                    onSortClick = { showFilterSheet = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedTransactions, key = { it.orderId }) { transaction ->
                        TransactionRecordCard(transaction)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PaginationRow(currentPage = 1, totalPages = 5)
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            initialColumn = sortColumn,
            initialAscending = sortAscending,
            initialStartDate = dateFilterStart,
            initialEndDate = dateFilterEnd,
            onDismiss = { showFilterSheet = false },
            onApply = { column, ascending, start, end ->
                sortColumn = column
                sortAscending = ascending
                dateFilterStart = start
                dateFilterEnd = end
                showFilterSheet = false

                // This is the object you'd hand to your API layer:
                // SalesLogFilterRequest(
                //     filters = listOf(SortFilterUi(column.apiName, ascending)),
                //     dateFilter = if (start != null || end != null) DateFilterUi(start, end) else null
                // )
            }
        )
    }
}

private fun sortButtonLabel(column: SortColumn, ascending: Boolean): String {
    return when {
        column == SortColumn.DATE && !ascending -> "Latest"
        column == SortColumn.DATE && ascending -> "Oldest"
        else -> "${column.displayName} ${if (ascending) "↑" else "↓"}"
    }
}

/**
 * Sort + date range sheet. Builds the exact request shape:
 * { "filters": [{ "column": ..., "ascending": ... }], "date_filter": { "start_date": ..., "end_date": ... } }
 * UI-only — nothing here calls a network layer, it just reports the chosen values back up.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    initialColumn: SortColumn,
    initialAscending: Boolean,
    initialStartDate: Long?,
    initialEndDate: Long?,
    onDismiss: () -> Unit,
    onApply: (SortColumn, Boolean, Long?, Long?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Draft state — only committed to the parent on "Apply", so backing out of the
    // sheet (dismiss/cancel) doesn't change what's currently applied.
    var draftColumn by remember { mutableStateOf(initialColumn) }
    var draftAscending by remember { mutableStateOf(initialAscending) }
    var draftStartDate by remember { mutableStateOf(initialStartDate) }
    var draftEndDate by remember { mutableStateOf(initialEndDate) }
    // No way to know which shortcut produced the initial range (if any), so treat any
    // pre-existing range as "Custom" and leave nothing highlighted when there's no range.
    var draftQuickRange by remember {
        mutableStateOf(if (initialStartDate != null || initialEndDate != null) QuickRange.CUSTOM else null)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Sort & Filter",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Secondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sort by",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray600)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SortColumn.entries.forEach { column ->
                    FilterChip(
                        selected = draftColumn == column,
                        onClick = { draftColumn = column },
                        label = { Text(column.displayName, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = White,
                            labelColor = Secondary,
                            selectedContainerColor = Primary,
                            selectedLabelColor = White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = draftColumn == column,
                            borderColor = Gray100,
                            selectedBorderColor = Primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Order",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray600)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !draftAscending,
                    onClick = { draftAscending = false },
                    label = { Text("Descending", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = White,
                        labelColor = Secondary,
                        selectedContainerColor = Primary,
                        selectedLabelColor = White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = !draftAscending,
                        borderColor = Gray100,
                        selectedBorderColor = Primary
                    )
                )
                FilterChip(
                    selected = draftAscending,
                    onClick = { draftAscending = true },
                    label = { Text("Ascending", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = White,
                        labelColor = Secondary,
                        selectedContainerColor = Primary,
                        selectedLabelColor = White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = draftAscending,
                        borderColor = Gray100,
                        selectedBorderColor = Primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Gray100)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Date range",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray600)
                )
                if (draftStartDate != null || draftEndDate != null) {
                    Text(
                        text = "Clear",
                        style = TextStyle(fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium),
                        modifier = Modifier.clickable {
                            draftStartDate = null
                            draftEndDate = null
                            draftQuickRange = null
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Peach summary card, mirrors the "Date range" card used elsewhere in the app.
            DateRangeSummaryCard(startDate = draftStartDate, endDate = draftEndDate)

            Spacer(modifier = Modifier.height(10.dp))

            // Quick shortcuts — instantly fill both start and end. Scrollable so it doesn't
            // have to cram every option into the sheet width.
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QuickRange.entries) { option ->
                    QuickRangeChip(
                        label = option.label,
                        isSelected = draftQuickRange == option,
                        onClick = {
                            when (option) {
                                QuickRange.TODAY -> {
                                    draftStartDate = startOfDayEpoch(nowEpoch())
                                    draftEndDate = endOfDayEpoch(nowEpoch())
                                }

                                QuickRange.LAST_HOUR -> {
                                    draftEndDate = nowEpoch()
                                    draftStartDate = nowEpoch() - 60 * 60
                                }

                                QuickRange.LAST_6_HOURS -> {
                                    draftEndDate = nowEpoch()
                                    draftStartDate = nowEpoch() - 6 * 60 * 60
                                }

                                QuickRange.LAST_12_HOURS -> {
                                    draftEndDate = nowEpoch()
                                    draftStartDate = nowEpoch() - 12 * 60 * 60
                                }

                                QuickRange.CUSTOM -> {
                                    // Just switches into custom mode; leaves existing dates as-is
                                    // so the user can fine-tune them below.
                                }
                            }
                            draftQuickRange = option
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Custom range",
                style = TextStyle(fontSize = 12.sp, color = Gray400)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date and time are separate, deliberately — each opens its own picker
            // and only updates its own part of the epoch timestamp. Editing any of
            // these switches the active shortcut to "Custom" automatically.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DatePickerField(
                    modifier = Modifier.weight(1f),
                    label = "Start date",
                    epochSeconds = draftStartDate,
                    onDateSelected = { newDate ->
                        draftStartDate = setDatePart(draftStartDate, newDate)
                        draftQuickRange = QuickRange.CUSTOM
                    }
                )
                TimeField(
                    modifier = Modifier.weight(1f),
                    label = "Start time",
                    epochSeconds = draftStartDate,
                    onTimeSelected = { hour, minute ->
                        draftStartDate = setTimePart(draftStartDate, hour, minute)
                        draftQuickRange = QuickRange.CUSTOM
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DatePickerField(
                    modifier = Modifier.weight(1f),
                    label = "End date",
                    epochSeconds = draftEndDate,
                    onDateSelected = { newDate ->
                        draftEndDate = setDatePart(draftEndDate, newDate)
                        draftQuickRange = QuickRange.CUSTOM
                    }
                )
                TimeField(
                    modifier = Modifier.weight(1f),
                    label = "End time",
                    epochSeconds = draftEndDate,
                    onTimeSelected = { hour, minute ->
                        draftEndDate = setTimePart(draftEndDate, hour, minute)
                        draftQuickRange = QuickRange.CUSTOM
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                ) {
                    Text("Cancel", color = Gray600)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Primary)
                        .clickable {
                            onApply(draftColumn, draftAscending, draftStartDate, draftEndDate)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Apply",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = White)
                    )
                }
            }
        }
    }
}

/** One tappable date field; opens a DatePickerDialog and reports back epoch seconds (UTC). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    modifier: Modifier = Modifier,
    label: String,
    epochSeconds: Long?,
    onDateSelected: (Long?) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Gray100, RoundedCornerShape(10.dp))
            .clickable { showPicker = true }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text = label, style = TextStyle(fontSize = 11.sp, color = Gray400))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = epochSeconds?.let { formatDate(it) } ?: "Any",
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Secondary)
        )
    }

    if (showPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = epochSeconds?.times(1000)
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis?.div(1000))
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(epochSeconds: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(epochSeconds * 1000))
}

private fun formatTime(epochSeconds: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(epochSeconds * 1000))
}

private fun formatDateTime(epochSeconds: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
    return formatter.format(Date(epochSeconds * 1000))
}

private fun nowEpoch(): Long = System.currentTimeMillis() / 1000

private fun startOfDayEpoch(epochSeconds: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = epochSeconds * 1000
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis / 1000
}

private fun endOfDayEpoch(epochSeconds: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = epochSeconds * 1000
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis / 1000
}

/**
 * Replaces only the year/month/day of [current] with the date carried by
 * [newDateEpochSeconds] (which comes from a date-only picker, so it's always midnight UTC),
 * keeping whatever time-of-day was already set. Defaults to 00:00 if there's no existing value.
 */
private fun setDatePart(current: Long?, newDateEpochSeconds: Long?): Long? {
    if (newDateEpochSeconds == null) return current

    val newDateCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    newDateCal.timeInMillis = newDateEpochSeconds * 1000

    val resultCal = Calendar.getInstance()
    resultCal.timeInMillis = (current ?: newDateEpochSeconds) * 1000
    resultCal.set(Calendar.YEAR, newDateCal.get(Calendar.YEAR))
    resultCal.set(Calendar.MONTH, newDateCal.get(Calendar.MONTH))
    resultCal.set(Calendar.DAY_OF_MONTH, newDateCal.get(Calendar.DAY_OF_MONTH))
    if (current == null) {
        resultCal.set(Calendar.HOUR_OF_DAY, 0)
        resultCal.set(Calendar.MINUTE, 0)
    }
    resultCal.set(Calendar.SECOND, 0)
    resultCal.set(Calendar.MILLISECOND, 0)
    return resultCal.timeInMillis / 1000
}

/**
 * Replaces only the hour/minute of [current], keeping the existing date.
 * Defaults to today if there's no existing value.
 */
private fun setTimePart(current: Long?, hour: Int, minute: Int): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = (current ?: nowEpoch()) * 1000
    cal.set(Calendar.HOUR_OF_DAY, hour)
    cal.set(Calendar.MINUTE, minute)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis / 1000
}

/** Peach summary card showing the combined range, e.g. "15 Aug 2026 - 00:00  to  15 Aug 2026 - 23:59". */
@Composable
private fun DateRangeSummaryCard(startDate: Long?, endDate: Long?) {
    val summaryText = when {
        startDate != null && endDate != null -> "${formatDateTime(startDate)}  to  ${formatDateTime(endDate)}"
        startDate != null -> "From ${formatDateTime(startDate)}"
        endDate != null -> "Until ${formatDateTime(endDate)}"
        else -> "No date range selected"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Primary200)
            .border(1.dp, Primary, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Date range", style = TextStyle(fontSize = 11.sp, color = PrimaryHover))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = summaryText,
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Secondary)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/** One tappable quick-range shortcut chip, e.g. "Today", "Last hour". Highlights when active. */
@Composable
private fun QuickRangeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Primary else White)
            .border(1.dp, if (isSelected) Primary else Gray100, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) White else Secondary
            ),
            maxLines = 1
        )
    }
}

/** One tappable time field, separate from the date field; opens its own time picker. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    modifier: Modifier = Modifier,
    label: String,
    epochSeconds: Long?,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Gray100, RoundedCornerShape(10.dp))
            .clickable { showPicker = true }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text = label, style = TextStyle(fontSize = 11.sp, color = Gray400))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = epochSeconds?.let { formatTime(it) } ?: "--:--",
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Secondary)
        )
    }

    if (showPicker) {
        val calendar = Calendar.getInstance().apply {
            if (epochSeconds != null) timeInMillis = epochSeconds * 1000
        }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )
        AppTimePickerDialog(
            onDismiss = { showPicker = false },
            onConfirm = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                showPicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

/** Material3 doesn't ship a TimePickerDialog wrapper — this is a small one around TimePicker. */
@Composable
private fun AppTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = White, tonalElevation = 6.dp) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Gray600) }
                    TextButton(onClick = onConfirm) { Text("OK", color = Primary) }
                }
            }
        }
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

private fun formatRupiah(amount: Long): String {
    return when {
        amount >= 1_000_000 -> "Rp %.2fM".format(amount / 1_000_000.0)
        amount >= 1_000 -> "Rp %.0fK".format(amount / 1_000.0)
        else -> "Rp $amount"
    }
}

@Composable
private fun StatsSummaryCard(
    transactionsCount: Int,
    unitsSold: Int,
    totalRevenue: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = "$transactionsCount", label = "Transactions", valueColor = Primary)
            StatDivider()
            StatItem(value = "$unitsSold", label = "Units Sold", valueColor = Success)
            StatDivider()
            StatItem(value = totalRevenue, label = "Total Revenue", valueColor = Secondary)
        }
    }
}


@Composable
private fun RowScope.StatItem(value: String, label: String, valueColor: Color) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = TextStyle(fontSize = 12.sp, color = Gray400))
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Gray100)
    )
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

@Composable
private fun PaginationRow(currentPage: Int, totalPages: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PageArrowButton(label = "<")
        Spacer(modifier = Modifier.width(6.dp))

        PageNumberButton(number = 1, isActive = currentPage == 1)
        Spacer(modifier = Modifier.width(6.dp))
        PageNumberButton(number = 2, isActive = currentPage == 2)
        Spacer(modifier = Modifier.width(6.dp))
        PageNumberButton(number = 3, isActive = currentPage == 3)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "...", modifier = Modifier.padding(horizontal = 4.dp), color = Gray400)
        Spacer(modifier = Modifier.width(6.dp))
        PageNumberButton(number = totalPages, isActive = currentPage == totalPages)

        Spacer(modifier = Modifier.width(6.dp))
        PageArrowButton(label = ">")
    }
}

@Composable
private fun PageNumberButton(number: Int, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Primary else White)
            .border(1.dp, if (isActive) Primary else Gray100, RoundedCornerShape(8.dp)),
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
private fun PageArrowButton(label: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(White)
            .border(1.dp, Gray100, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, style = TextStyle(fontSize = 13.sp, color = Secondary))
    }
}