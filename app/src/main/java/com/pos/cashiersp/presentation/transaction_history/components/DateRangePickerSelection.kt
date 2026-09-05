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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialog
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
) {
    val selectedPeriod = viewModel.selectedPeriod.value
    val showSortMenu = viewModel.showSortMenu.value
    val showColumnMenu = viewModel.showColumnMenu.value
    val selectedSort = viewModel.selectedSort.value
    val selectedColumn = viewModel.selectedColumn.value
    val dateRangePicker: Boolean = viewModel.dateRangePicker.value
    val startTimePicker: Boolean = viewModel.startTimePicker.value
    val endTimePicker: Boolean = viewModel.endTimePicker.value

    val startCalendar: Calendar? = viewModel.startCalendar.value
    val endCalendar: Calendar? = viewModel.endCalendar.value

    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy - HH:mm",
        Locale.getDefault()
    )
    val (readableStartDate, readableEndDate) = if (startCalendar != null && endCalendar != null) {
        dateFormat.format(startCalendar.time) to dateFormat.format(endCalendar.time)
    } else {
        "Start Calendar" to "End Calendar"
    }

    fun handleDateRangeSelected(startMillis: Long?, endMillis: Long?) {
        if (startMillis != null && endMillis != null) {
            val start = Calendar.getInstance().apply {
                this.timeInMillis = startMillis
            }

            val end = Calendar.getInstance().apply {
                timeInMillis = endMillis
            }

            viewModel.onEvent(TransactionHistoryEvent.OnChangePeriodFilter(PeriodFilter.CUSTOM))
            viewModel.onEvent(TransactionHistoryEvent.OnChangeStartCalendar(start))
            viewModel.onEvent(TransactionHistoryEvent.OnChangeEndCalendar(end))
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
                        text = "$readableStartDate - $readableEndDate",
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
                                text = when (selectedColumn) {
                                    ColumnName.CREATED_AT -> selectedSort.label
                                    ColumnName.AMOUNT -> selectedSort.valueLabel
                                },
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
                    containerColor = White
                ) {
                    SortDirection.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (selectedColumn) {
                                        ColumnName.CREATED_AT -> option.label
                                        ColumnName.AMOUNT -> option.valueLabel
                                    }
                                )
                            },
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
                    viewModel.onEvent(
                        TransactionHistoryEvent.SetStartCalendar(
                            listOf(
                                Calendar.HOUR_OF_DAY to timePickerState.hour,
                                Calendar.MINUTE to timePickerState.minute
                            )
                        )
                    )
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
                    viewModel.onEvent(
                        TransactionHistoryEvent.SetEndCalendar(
                            listOf(
                                Calendar.HOUR_OF_DAY to timePickerState.hour,
                                Calendar.MINUTE to timePickerState.minute
                            )
                        )
                    )
                }
                viewModel.onEvent(TransactionHistoryEvent.PickersInput(Pickers.END_TIME_PICKER, false))
            }) {
            TimePicker(
                state = timePickerState,
            )
        }
    }

    val generalAlertDialogStatus = viewModel.generalAlertDialogStatus.value
    if (generalAlertDialogStatus.showDialog) {
        GeneralAlertDialog(
            generalAlertDialogStatus,
            onConfirmation = { viewModel.onEvent(TransactionHistoryEvent.OnCloseGeneralDialog) },
            onDismissRequest = { viewModel.onEvent(TransactionHistoryEvent.OnCloseGeneralDialog) },
        )
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
    val isRequesting = viewModel.isRequesting.value

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
                enabled = !isRequesting,
                onClick = { viewModel.onEvent(TransactionHistoryEvent.OnClickResetBtn) },
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
                enabled = !isRequesting,
                onClick = { viewModel.onEvent(TransactionHistoryEvent.OnClickShowReportBtn) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = if (isRequesting) "Please wait..." else "Show report",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}