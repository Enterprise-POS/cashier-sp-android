package com.pos.cashiersp.presentation.expenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import com.pos.cashiersp.model.domain.Expense
import com.pos.cashiersp.presentation.expenses.components.ExpenseCategoryDialog
import com.pos.cashiersp.presentation.expenses.components.ExpenseForm
import com.pos.cashiersp.presentation.expenses.components.localDayToUtcMillis
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.ui.theme.Danger
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.dateFormatter
import com.pos.cashiersp.presentation.util.toRupiah
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    navController: NavController,
    viewModel: ExpenseViewModel = hiltViewModel(),
) {
    val form = viewModel.form.value
    val categories = viewModel.categories.value
    val storeName = viewModel.storeName.value
    val storeNames = viewModel.storeNames.value
    val onEvent = viewModel::onEvent
    val isExpanded = currentWindowAdaptiveInfo().windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val storeLabel: (Int?) -> String = { id -> if (id == null) "Whole business" else storeNames[id] ?: "Store #$id" }
    // The form's "store" option: this device's store, or the edited expense's own store
    val formStoreLabel = form.editing?.storeId?.let(storeLabel) ?: storeName

    // The app theme is still the template purple: use the brand colors for this screen, its sheet and dialogs
    MaterialTheme(colorScheme = expenseColorScheme(MaterialTheme.colorScheme)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Secondary100,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Column {
                            TextWithNoPadding("Expenses", fontSize = 18.sp, color = Secondary, fontWeight = FontWeight.W500)
                            Spacer(Modifier.height(6.dp))
                            TextWithNoPadding("Track your business spending", fontSize = 12.sp, color = Gray600)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to previous screen")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEvent(ExpenseEvent.OnSetCategoryManager(true)) }) {
                            Icon(Icons.Outlined.Category, contentDescription = "Manage expense categories")
                        }
                    },
                )
            },
            floatingActionButton = {
                // On tablets the form pane is already beside the list
                if (!(isExpanded && form.isOpen)) {
                    ExtendedFloatingActionButton(
                        onClick = { onEvent(ExpenseEvent.OnOpenAddForm) },
                        containerColor = Primary,
                        contentColor = White,
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        text = { Text("Add Expense") },
                    )
                }
            },
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                ExpenseListPane(viewModel, storeLabel, modifier = Modifier.weight(1f))

                if (isExpanded && form.isOpen) {
                    Surface(
                        color = White,
                        shape = CardDefaults.shape,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 16.dp, bottom = 16.dp),
                    ) {
                        ExpenseForm(form, categories, formStoreLabel, onEvent)
                    }
                }
            }
        }

        if (!isExpanded && form.isOpen) {
            ModalBottomSheet(
                onDismissRequest = { onEvent(ExpenseEvent.OnCloseForm) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = White,
            ) {
                ExpenseForm(form, categories, formStoreLabel, onEvent)
            }
        }

        if (viewModel.categoryManager.value.isOpen) {
            ExpenseCategoryDialog(viewModel.categoryManager.value, categories, onEvent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseListPane(viewModel: ExpenseViewModel, storeLabel: (Int?) -> String, modifier: Modifier) {
    val list = viewModel.list.value
    val categories = viewModel.categories.value
    val scope = viewModel.scope.value
    val categoryFilter = viewModel.categoryFilter.value
    val (rangeStart, rangeEnd) = viewModel.dateRange.value
    val onEvent = viewModel::onEvent
    var showRangePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp), // room for the FAB
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ExpenseScope.entries.forEachIndexed { index, entry ->
                    SegmentedButton(
                        selected = scope == entry,
                        onClick = { onEvent(ExpenseEvent.OnSetScope(entry)) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = ExpenseScope.entries.size),
                    ) { Text(entry.label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp) }
                }
            }
        }

        item {
            AssistChip(
                onClick = { showRangePicker = true },
                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                label = { Text("${dayLabel(rangeStart)} – ${dayLabel(rangeEnd)}") },
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val chips = listOf(0 to "All", -1 to "Uncategorized") + categories.map { it.id to it.name }
                items(chips, key = { it.first }) { (id, label) ->
                    FilterChip(
                        selected = categoryFilter == id,
                        onClick = { onEvent(ExpenseEvent.OnSetCategoryFilter(id)) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary, selectedLabelColor = White
                        ),
                    )
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = White), modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text("Total", color = Gray600, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(list.sumAmount.toRupiah(), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Secondary)
                        Text("${list.totalCount} expenses", fontSize = 12.sp, color = Gray600)
                    }
                }
            }
        }

        viewModel.setupError.value?.let { error ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(error, color = Danger, fontSize = 13.sp)
                        TextButton(onClick = { onEvent(ExpenseEvent.OnRetrySetup) }) { Text("Try again") }
                    }
                }
            }
        }

        list.error?.let { error ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(error, color = Danger, fontSize = 13.sp)
                        TextButton(onClick = { onEvent(ExpenseEvent.OnRefresh) }) { Text("Try again") }
                    }
                }
            }
        }

        items(list.expenses, key = { it.id }) { expense ->
            ExpenseRow(expense, storeLabel(expense.storeId)) { onEvent(ExpenseEvent.OnOpenEditForm(expense)) }
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    list.isLoading -> CircularProgressIndicator(color = Primary)
                    list.canLoadMore -> OutlinedButton(onClick = { onEvent(ExpenseEvent.OnLoadMore) }) { Text("Load more") }
                    list.expenses.isEmpty() && list.error == null ->
                        Text("No expenses in this period.", color = Gray600, modifier = Modifier.padding(24.dp))
                }
            }
        }
    }

    if (showRangePicker) {
        val state = rememberDateRangePickerState(
            initialSelectedStartDateMillis = localDayToUtcMillis(calendarOf(rangeStart)),
            initialSelectedEndDateMillis = localDayToUtcMillis(calendarOf(rangeEnd)),
        )
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                TextButton(
                    enabled = state.selectedStartDateMillis != null,
                    onClick = {
                        val start = state.selectedStartDateMillis!!
                        val end = state.selectedEndDateMillis ?: start // one tap = a single day
                        onEvent(ExpenseEvent.OnSetDateRange(utcDayToLocalStart(start), utcDayToLocalStart(end)))
                        showRangePicker = false
                    },
                ) { Text("Apply") }
            },
            dismissButton = { TextButton(onClick = { showRangePicker = false }) { Text("Cancel") } },
        ) {
            DateRangePicker(state = state, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense, storeLabel: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(14.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    expense.categoryName ?: "Uncategorized",
                    fontWeight = FontWeight.Medium,
                    color = if (expense.categoryName == null) Gray600 else Secondary,
                )
                val details = listOf(
                    expense.description.takeIf { it.isNotBlank() },
                    dateFormatter(expense.expenseDate, "dd MMM"),
                    expense.paymentType,
                    storeLabel,
                ).filterNotNull().joinToString(" · ")
                Text(details, fontSize = 12.sp, color = Gray600, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text(expense.amount.toRupiah(), fontWeight = FontWeight.SemiBold, color = Secondary)
        }
    }
}

private fun expenseColorScheme(base: ColorScheme) = base.copy(
    primary = Primary,
    onPrimary = White,
    primaryContainer = Primary.copy(alpha = 0.18f),
    onPrimaryContainer = Secondary,
    secondaryContainer = Primary.copy(alpha = 0.18f), // selected segmented button
    onSecondaryContainer = Secondary,
    surfaceContainer = White, // dropdown menus
    surfaceContainerHigh = White, // dialogs, date pickers
)

private fun calendarOf(millis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = millis }

private fun dayLabel(millis: Long): String = dateFormatter(calendarOf(millis), "dd MMM yyyy")

/** UTC-midnight millis from the date picker -> local start of that day. */
private fun utcDayToLocalStart(utcMillis: Long): Long {
    val day = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
    return Calendar.getInstance().apply {
        clear()
        set(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}
