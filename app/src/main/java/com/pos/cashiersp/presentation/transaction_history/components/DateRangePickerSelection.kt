package com.pos.cashiersp.presentation.transaction_history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.transaction_history.ColumnName
import com.pos.cashiersp.presentation.transaction_history.PeriodFilter
import com.pos.cashiersp.presentation.transaction_history.Pickers
import com.pos.cashiersp.presentation.transaction_history.SortDirection
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryEvent
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryViewModel
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray500
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSection(
    viewModel: TransactionHistoryViewModel = hiltViewModel(),
    onDateRangeSelected: (startDate: String, endDate: String) -> Unit = { _, _ -> }
) {
    val selectedPeriod = viewModel.selectedPeriod.value
    val showSortMenu = viewModel.showSortMenu.value
    val showColumnMenu = viewModel.showColumnMenu.value
    val selectedSort = viewModel.selectedSort.value
    val selectedColumn = viewModel.selectedColumn.value
    val dateRangePicker = viewModel.dateRangePicker.value
    val startTimePicker = viewModel.startTimePicker.value
    val endTimePicker = viewModel.endTimePicker.value

    val startCalendar = viewModel.startCalendar.value
    val endCalendar = viewModel.endCalendar.value

    var formattedStartDate by remember { mutableStateOf<String?>(null) }
    var formatterEndDate by remember { mutableStateOf<String?>(null) }

    // Calculate date range based on selected period
    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy - HH:mm",
        Locale.getDefault()
    )

    val (startDate, endDate) =
        // If custom period is selected and dates are available, use them
        if (selectedPeriod == PeriodFilter.CUSTOM && formattedStartDate != null && formatterEndDate != null) {
            formattedStartDate!! to formatterEndDate!!
        } else {
            val now = Calendar.getInstance()
            val start = now.clone() as Calendar
            val end = now.clone() as Calendar

            when (selectedPeriod) {
                PeriodFilter.TODAY -> {
                    start.set(Calendar.HOUR_OF_DAY, 0)
                    start.set(Calendar.MINUTE, 0)
                    start.set(Calendar.SECOND, 0)
                    start.set(Calendar.MILLISECOND, 0)

                    end.set(Calendar.HOUR_OF_DAY, 23)
                    end.set(Calendar.MINUTE, 59)
                    end.set(Calendar.SECOND, 0)
                    end.set(Calendar.MILLISECOND, 0)
                }

                PeriodFilter.LAST_HOUR -> {
                    start.add(Calendar.HOUR_OF_DAY, -1)
                }

                PeriodFilter.LAST_6_HOUR -> {
                    start.add(Calendar.HOUR_OF_DAY, -6)
                }

                PeriodFilter.LAST_12_HOUR -> {
                    start.add(Calendar.HOUR_OF_DAY, -12)
                }

                PeriodFilter.LAST_7_DAYS -> {
                    start.add(Calendar.DAY_OF_YEAR, -7)
                }

                PeriodFilter.THIS_MONTH -> {
                    start.set(Calendar.DAY_OF_MONTH, 1)
                    start.set(Calendar.HOUR_OF_DAY, 0)
                    start.set(Calendar.MINUTE, 0)
                    start.set(Calendar.SECOND, 0)
                    start.set(Calendar.MILLISECOND, 0)
                }

                PeriodFilter.CUSTOM -> {}
            }

            dateFormat.format(start.time) to dateFormat.format(end.time)
        }


    fun handleDateRangeSelected(startMillis: Long?, endMillis: Long?) {
        if (startMillis != null && endMillis != null) {
            viewModel.onEvent(TransactionHistoryEvent.OnChangePeriodFilter(PeriodFilter.CUSTOM))

            val start = Calendar.getInstance().apply {
                timeInMillis = startMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val end = Calendar.getInstance().apply {
                timeInMillis = endMillis
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            viewModel.onEvent(TransactionHistoryEvent.OnChangeStartCalendar(start))
            viewModel.onEvent(TransactionHistoryEvent.OnChangeEndCalendar(end))
            formattedStartDate = dateFormat.format(start.time)
            formatterEndDate = dateFormat.format(end.time)
        }
    }

    Card(
        border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f)),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier.padding(horizontal = 14.dp),
    ) {
        // Date Range Picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable { viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.DATE_RANGE_PICKER, true)) }
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Primary100,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 0.4.dp,
                        color = Primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                TextWithNoPadding(
                    text = "Date range",
                    fontSize = 12.sp,
                    color = Gray500,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$startDate - $endDate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Secondary
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Calendar",
                        tint = Gray500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Sort and Status Dropdowns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Sort Dropdown
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                OutlinedButton(
                    onClick = { viewModel.onEvent(TransactionHistoryEvent.OnClickSortersDropDown(true)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Gray100.copy(alpha = .2f),
                        contentColor = Secondary,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 10.dp
                    ),
                    border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            TextWithNoPadding(
                                text = "Sort",
                                fontSize = 12.sp,
                                color = Gray400
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = selectedSort.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Secondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Dropdown's Icon",
                            tint = Gray400
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = {
                        viewModel.onEvent(TransactionHistoryEvent.OnClickSortersDropDown(false))
                    },
                    modifier = Modifier.background(White),
                ) {
                    SortDirection.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                viewModel.onEvent(TransactionHistoryEvent.OnPickSortersDropDown(option))
                                viewModel.onEvent(TransactionHistoryEvent.OnClickSortersDropDown(false))
                            }
                        )
                    }
                }
            }

            // Status Dropdown
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.onEvent(TransactionHistoryEvent.OnClickColumnsDropDown(true))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Gray100.copy(alpha = .2f),
                        contentColor = Secondary,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 10.dp
                    ),
                    border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            TextWithNoPadding(
                                text = "Order by",
                                fontSize = 12.sp,
                                color = Gray400
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = selectedColumn.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Secondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.FilterAlt,
                            contentDescription = "Dropdown",
                            tint = Gray400
                        )
                    }
                }

                DropdownMenu(
                    expanded = showColumnMenu,
                    onDismissRequest = { viewModel.onEvent(TransactionHistoryEvent.OnClickColumnsDropDown(false)) },
                    modifier = Modifier.background(White)
                ) {
                    ColumnName.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                viewModel.onEvent(TransactionHistoryEvent.OnPickColumnsDropDown(option))
                                viewModel.onEvent(TransactionHistoryEvent.OnClickColumnsDropDown(false))
                            }
                        )
                    }
                }
            }
        }

        // Period Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodFilter.entries.forEach { period ->
                item {
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = {
                            viewModel.onEvent(TransactionHistoryEvent.OnChangePeriodFilter(period))
                            if (period == PeriodFilter.CUSTOM) {
                                viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.DATE_RANGE_PICKER, true))
                            }
                        },
                        label = {
                            Text(
                                text = period.label,
                                fontSize = 13.sp,
                                fontWeight = if (selectedPeriod == period) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = White,
                            containerColor = White,
                            labelColor = Gray300
                        ),
                        border = BorderStroke(width = .8.dp, color = Gray100.copy(alpha = .4f)),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        PaginationBar(viewModel)

        ButtonRowComponent(viewModel)
    }

    if (dateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = {
                viewModel.onEvent(
                    TransactionHistoryEvent.PickersInput(
                        Pickers.DATE_RANGE_PICKER,
                        false
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        handleDateRangeSelected(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.DATE_RANGE_PICKER, false))
                        viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.START_TIME_PICKER, true))
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.DATE_RANGE_PICKER, false))
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Select date range",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    )
                },
                showModeToggle = false,
            )
        }
    }

    if (startTimePicker) {
        val currentTime = Calendar.getInstance()

        val timePickerState = rememberTimePickerState(
            initialHour = 0,
            initialMinute = 0,
            is24Hour = true,
        )
        TimePickerDialog(
            title = {
                Text("Select start time")
            },
            onDismiss = {
                viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.START_TIME_PICKER, false))
            },
            onConfirm = {
                if (startCalendar != null && endCalendar != null) {
                    startCalendar.apply {
                        this.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        this.set(Calendar.MINUTE, timePickerState.minute)
                    }
                    formattedStartDate = dateFormat.format(startCalendar!!.time)
                }
                viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.START_TIME_PICKER, false))
                viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.END_TIME_PICKER, true))
            }) {
            TimePicker(
                state = timePickerState,
            )
        }
    }

    if (endTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = 23,
            initialMinute = 59,
            is24Hour = true,
        )
        TimePickerDialog(
            title = {
                Text("Select end time")
            },
            onDismiss = {
                viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.END_TIME_PICKER, false))
            },
            onConfirm = {
                if (startCalendar != null && endCalendar != null) {
                    endCalendar.apply {
                        this!!.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        this.set(Calendar.MINUTE, timePickerState.minute)
                    }
                    formatterEndDate = dateFormat.format(endCalendar!!.time)
                }
                viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.END_TIME_PICKER, false))
            }) {
            TimePicker(
                state = timePickerState,
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        title = title,
        text = { content() }
    )
}

@Composable
fun ButtonRowComponent(
    viewModel: TransactionHistoryViewModel,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Export Button
            Button(
                onClick = { /* Handle export */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(
                    text = "Reset",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            // View daily summary Button
            Button(
                onClick = { viewModel.onEvent(TransactionHistoryEvent.OnClickShowReportBtn) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(
                    text = "Show report",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}