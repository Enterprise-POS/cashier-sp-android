package com.pos.cashiersp.presentation.item_sales_log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialog
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.item_sales_log.components.FilterBottomSheet
import com.pos.cashiersp.presentation.item_sales_log.components.OrderSearchAndSortBar
import com.pos.cashiersp.presentation.item_sales_log.components.PaginationRow
import com.pos.cashiersp.presentation.item_sales_log.components.ScopeSelector
import com.pos.cashiersp.presentation.item_sales_log.components.TransactionRecordCard
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import kotlinx.coroutines.flow.collectLatest

/** Which scope of logs the user wants to see. */
enum class SalesLogScope {
    ALL_ITEMS,
    SINGLE_ITEM
}

/** One entry of the request's "filters" array: { "column": ..., "ascending": ... } */
data class SortFilterUi(
    val column: String,
    val ascending: Boolean
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
    viewModel: ItemSalesLogViewModel = hiltViewModel()
) {
    // Maintain state if user currently requesting something
    val viewModelState = viewModel.viewModelState.value

    // Default to null so we can force the user to pick a scope on first open.
    val selectedScope = viewModel.selectedScope.value

    // FilterBottomSheet
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

    val informationDialogStatus: GeneralAlertDialogStatus = viewModel.informationDialogStatus.value

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ItemSalesLogViewModel.UIEvent.ErrorAndMustNavigateToSelectTenantScreen -> navController.navigate(
                    Screen.SELECT_TENANT
                ) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }

                is ItemSalesLogViewModel.UIEvent.GotoInvoiceDetailScreen -> {
                    println(Screen.createInvoiceDetailURL(event.orderItemId))
                    navController.navigate(Screen.createInvoiceDetailURL(event.orderItemId))
                }
            }
        }
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
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModelState.isLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(color = Primary)
                            Text(
                                text = "Please wait...",
                                style = TextStyle(fontSize = 13.sp, color = Gray400)
                            )
                        }
                    } else if (scopedTransactions.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No data...\nTry different filter if data not exist",
                                style = TextStyle(fontSize = 13.sp, color = Gray400, textAlign = TextAlign.Center)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(scopedTransactions) { purchasedItem ->
                                TransactionRecordCard(
                                    purchasedItem,
                                    onClickSeeDetail = {
                                        viewModel.onEvent(
                                            ItemSalesLogEvent.OnClickSeeOrderItemDetail(it)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PaginationRow(
                    currentPage,
                    totalPages,
                    onPageChange = { if (!viewModelState.isLoading) viewModel.onEvent(ItemSalesLogEvent.OnChangePage(it)) }
                )
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            onDismiss = { viewModel.onEvent(ItemSalesLogEvent.OnDismissFilterBottomSheet) },
            viewModel = viewModel
        )
    }

    if (informationDialogStatus.showDialog) {
        GeneralAlertDialog(
            informationDialogStatus,
            confirmText = "Close",
            cancelText = "", // Empty string will make the cancel button disappear
            onDismissRequest = { viewModel.onEvent(ItemSalesLogEvent.OnDismissInformationDialog) },
            onConfirmation = { viewModel.onEvent(ItemSalesLogEvent.OnDismissInformationDialog) })
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

