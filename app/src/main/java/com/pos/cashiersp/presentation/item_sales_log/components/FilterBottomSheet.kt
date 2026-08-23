package com.pos.cashiersp.presentation.item_sales_log.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.pos.cashiersp.presentation.item_sales_log.QuickRange
import com.pos.cashiersp.presentation.item_sales_log.SortColumn
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary200
import com.pos.cashiersp.presentation.ui.theme.PrimaryHover
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Sort + date range sheet. Builds the exact request shape:
 * { "filters": [{ "column": ..., "ascending": ... }], "date_filter": { "start_date": ..., "end_date": ... } }
 * UI-only — nothing here calls a network layer, it just reports the chosen values back up.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
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

private fun nowEpoch(): Long = System.currentTimeMillis() / 1000

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