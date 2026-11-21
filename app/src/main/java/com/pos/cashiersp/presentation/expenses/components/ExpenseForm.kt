package com.pos.cashiersp.presentation.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pos.cashiersp.model.domain.ExpenseCategory
import com.pos.cashiersp.model.domain.ExpensePaymentTypes
import com.pos.cashiersp.presentation.expenses.ExpenseEvent
import com.pos.cashiersp.presentation.expenses.ExpenseFormState
import com.pos.cashiersp.presentation.ui.theme.Danger
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.dateFormatter
import com.pos.cashiersp.presentation.util.toRupiah
import java.util.Calendar
import java.util.TimeZone

/** Add / edit expense. Used inside the phone bottom sheet and the tablet side pane. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseForm(
    form: ExpenseFormState,
    categories: List<ExpenseCategory>,
    storeLabel: String,
    onEvent: (ExpenseEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var categoryMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            if (form.editing == null) "Add Expense" else "Edit Expense",
            fontSize = 18.sp, color = Secondary,
        )

        OutlinedTextField(
            value = form.amount,
            onValueChange = { onEvent(ExpenseEvent.OnChangeAmount(it)) },
            label = { Text("Amount *") },
            prefix = { Text("Rp ") },
            supportingText = { form.amount.toLongOrNull()?.let { Text(it.toRupiah()) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Category, last menu entry opens the category manager (add / rename / delete)
        ExposedDropdownMenuBox(expanded = categoryMenuOpen, onExpandedChange = { categoryMenuOpen = it }) {
            OutlinedTextField(
                value = categories.find { it.id == form.categoryId }?.name ?: "Uncategorized",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuOpen) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = categoryMenuOpen, onDismissRequest = { categoryMenuOpen = false }) {
                DropdownMenuItem(text = { Text("Uncategorized", color = Gray600) }, onClick = {
                    onEvent(ExpenseEvent.OnChangeCategory(null)); categoryMenuOpen = false
                })
                categories.forEach { category ->
                    DropdownMenuItem(text = { Text(category.name) }, onClick = {
                        onEvent(ExpenseEvent.OnChangeCategory(category.id)); categoryMenuOpen = false
                    })
                }
                HorizontalDivider()
                DropdownMenuItem(text = { Text("Manage categories…", color = Primary) }, onClick = {
                    categoryMenuOpen = false
                    onEvent(ExpenseEvent.OnSetCategoryManager(true))
                })
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("For", fontSize = 12.sp, color = Gray600)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = form.forStore,
                    onClick = { onEvent(ExpenseEvent.OnChangeForStore(true)) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                ) { Text(storeLabel, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                SegmentedButton(
                    selected = !form.forStore,
                    onClick = { onEvent(ExpenseEvent.OnChangeForStore(false)) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                ) { Text("Whole business", maxLines = 1) }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Payment", fontSize = 12.sp, color = Gray600)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpensePaymentTypes.forEach { type ->
                    FilterChip(
                        selected = form.paymentType == type,
                        onClick = { onEvent(ExpenseEvent.OnChangePaymentType(type)) },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary, selectedLabelColor = White
                        ),
                    )
                }
            }
        }

        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
            Text(
                "  " + dateFormatter(form.date, "dd MMM yyyy"),
                modifier = Modifier.weight(1f),
            )
        }

        OutlinedTextField(
            value = form.description,
            onValueChange = { onEvent(ExpenseEvent.OnChangeDescription(it)) },
            label = { Text("Note") },
            supportingText = { Text("${form.description.length} / 500") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )

        form.editing?.let {
            Text(
                "Recorded by ${it.recordedBy}" +
                        (it.updatedAt?.let { u -> " · edited ${dateFormatter(u, "dd MMM yyyy HH:mm")}" } ?: ""),
                fontSize = 12.sp, color = Gray600,
            )
        }
        form.error?.let { Text(it, color = Danger, fontSize = 13.sp) }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            if (form.editing != null) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    enabled = !form.isSaving,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
                ) { Text("Delete") }
            }
            OutlinedButton(onClick = { onEvent(ExpenseEvent.OnCloseForm) }, enabled = !form.isSaving) { Text("Cancel") }
            Button(
                onClick = { onEvent(ExpenseEvent.OnSaveForm) },
                enabled = !form.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.weight(1f),
            ) { Text(if (form.isSaving) "Saving…" else "Save") }
        }
    }

    if (showDatePicker) {
        // DatePicker works in UTC midnight millis: convert from / to the local calendar, keep the time of day
        val state = rememberDatePickerState(initialSelectedDateMillis = localDayToUtcMillis(form.date))
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onEvent(ExpenseEvent.OnChangeDate(withUtcDay(form.date, it))) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) { DatePicker(state = state) }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this expense?") },
            text = { Text("${form.amount.toLongOrNull()?.toRupiah() ?: ""} · ${dateFormatter(form.date, "dd MMM yyyy")}") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onEvent(ExpenseEvent.OnDeleteEditing)
                }) { Text("Delete", color = Danger) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } },
        )
    }
}

/** Local calendar day -> the UTC-midnight millis Material DatePicker uses. */
fun localDayToUtcMillis(local: Calendar): Long = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
    clear()
    set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
}.timeInMillis

/** Copy of [base] moved to the day of [utcMillis] (from DatePicker), time of day kept. */
fun withUtcDay(base: Calendar, utcMillis: Long): Calendar {
    val day = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
    return (base.clone() as Calendar).apply {
        set(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH))
    }
}
