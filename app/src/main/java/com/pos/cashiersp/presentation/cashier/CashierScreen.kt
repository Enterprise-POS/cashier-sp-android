package com.pos.cashiersp.presentation.cashier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import com.pos.cashiersp.model.domain.Category
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.cashier.component.CashierPartialBottomSheet
import com.pos.cashiersp.presentation.cashier.component.CashierCartSummaryBar
import com.pos.cashiersp.presentation.cashier.component.CashierTopAppBar
import com.pos.cashiersp.presentation.cashier.component.CategoryCard
import com.pos.cashiersp.presentation.cashier.component.FoodItemListContainer
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialog
import com.pos.cashiersp.presentation.cashier.component.ItemCard
import com.pos.cashiersp.presentation.cashier.component.MidtransPaymentDialog
import com.pos.cashiersp.presentation.cashier.component.PaymentSummary
import com.pos.cashiersp.presentation.cashier.component.calculatePaymentTotals
import com.pos.cashiersp.presentation.cashier.component.TransactionCompleteDialog
import com.pos.cashiersp.presentation.global_component.SimpleSearchBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: CashierViewModel = hiltViewModel()
) {
    // viewmodel
    val vmState by viewModel.state
    val categories by viewModel.categories
    val selectedCategoryId by viewModel.selectedCategory
    val cashierItems by viewModel.cashierItems
    val cart by viewModel.cart
    val searchProductString by viewModel.searchProductString
    val generalAlertDialogStatus by viewModel.generalAlertDialogStatus
    val loadAllProductsDialogStatus by viewModel.loadAllProductsDialogStatus
    val transactionCompleteDialogState by viewModel.transactionCompleteDialogState
    val completeTransactionParams by viewModel.completeTransactionReference
    val isPrinting by viewModel.isPrinting
    val informationDialogStatus by viewModel.informationDialogStatus
    val isCategoriesExpanded by viewModel.isCategoriesExpanded // Collapse whenever a category gets selected
    val midtransDialogState = viewModel.midtransPaymentDialogState.value
    val paymentURL = viewModel.midtransPaymentURL.value

    // scope
    var showBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val searchTextFieldState = remember { TextFieldState() }

    val isExpanded = currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    val categoryColumnCount = if (isExpanded) 5 else 3
    val collapsedRows = (
            (categories.size + categoryColumnCount - 1) / categoryColumnCount
            ).coerceIn(1, 3)
    val categoriesGridHeight by animateDpAsState(
        targetValue = if (isCategoriesExpanded) 400.dp else (52.dp * collapsedRows),
        animationSpec = tween(durationMillis = 300),
        label = "categoriesGridHeight"
    )


    val totalItemQuantity = remember(cart) { cart.values.sumOf { it.quantity } }
    val paymentTotals = remember(cart) { calculatePaymentTotals(cart) }
    val filteredCashierItems = remember(searchProductString, selectedCategoryId, cashierItems) {
        if (searchProductString.isNotEmpty()) {
            if (selectedCategoryId == -1) {
                cashierItems.filter { it.itemName.contains(searchProductString) }
            } else {
                cashierItems.filter { it.itemName.contains(searchProductString) && it.categoryId == selectedCategoryId }
            }
        } else if (selectedCategoryId == -1) {
            cashierItems
        } else {
            cashierItems.filter { it.categoryId == selectedCategoryId }
        }
    }

    val sortedCategories = remember(categories) {
        categories.entries.sortedBy { it.key }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CashierViewModel.UIEvent.ErrorAndMustNavigateToSelectTenantScreen -> navController.navigate(Screen.SELECT_TENANT) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }

                is CashierViewModel.UIEvent.CloseCashierPartialSheet -> {
                    showBottomSheet = false
                }

                is CashierViewModel.UIEvent.ShowErrorSnackbar -> {
                    val userClickedSnackbarBtn = snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long,
                        withDismissAction = true,
                        actionLabel = "Close"
                    )
                    when (userClickedSnackbarBtn) {
                        SnackbarResult.Dismissed -> {
                            println("Do something when dismissed")
                        }

                        SnackbarResult.ActionPerformed -> {
                            println("Do something")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (!isExpanded) {
                CashierCartSummaryBar(
                    itemQuantity = totalItemQuantity,
                    transactionTotal = paymentTotals.total,
                    onViewCart = { showBottomSheet = true }
                )
            }
        },
        topBar = {
            CashierTopAppBar(
                drawerState = drawerState
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .then(if (isExpanded) Modifier else Modifier.padding(horizontal = 16.dp))
        ) {
            if (isExpanded && !vmState.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        SimpleSearchBar(
                            searchResults = listOf(),
                            enabled = true,
                            allowExpansion = false,
                            textFieldState = searchTextFieldState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            onClear = { viewModel.onEvent(CashierEvent.OnClearSearchProduct) },
                            onSearch = { viewModel.onEvent(CashierEvent.OnSearchProduct(it)) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CashierProductCatalog(
                            sortedCategories = sortedCategories,
                            categoryCount = categories.size,
                            isCategoriesExpanded = isCategoriesExpanded,
                            categoriesGridHeight = categoriesGridHeight,
                            filteredCashierItems = filteredCashierItems,
                            itemColumnCount = 4,
                            categoryColumnCount = categoryColumnCount,
                            onToggleCategories = {
                                viewModel.onEvent(CashierEvent.OnToggleCategoriesExpanded)
                            },
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(360.dp)
                            .fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        FoodItemListContainer(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 12.dp),
                            viewModel = viewModel
                        )
                        PaymentSummary(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 360.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 4.dp)
                                .verticalScroll(rememberScrollState()),
                            viewModel = viewModel,
                            showCashKeypad = true
                        )
                    }
                }
            } else {
                SimpleSearchBar(
                    searchResults = listOf(),
                    enabled = !vmState.isLoading,
                    allowExpansion = false,
                    textFieldState = searchTextFieldState,
                    modifier = Modifier.fillMaxWidth(),
                    onClear = { viewModel.onEvent(CashierEvent.OnClearSearchProduct) },
                    onSearch = { viewModel.onEvent(CashierEvent.OnSearchProduct(it)) },
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (vmState.isLoading) {
                    // Center the loading indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = vmState.loadingMessage,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Categories",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${categories.size} total",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        // 3 columns x 3 rows = 9 items visible when collapsed
                        if (categories.size > 9) {
                            val rotation by animateFloatAsState(
                                targetValue = if (isCategoriesExpanded) 180f else 0f,
                                label = "chevronRotation"
                            )
                            TextButton(
                                modifier = Modifier.heightIn(min = 48.dp),
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.secondary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                onClick = { viewModel.onEvent(CashierEvent.OnToggleCategoriesExpanded) },
                            ) {
                                Text(
                                    if (isCategoriesExpanded) "Show less" else "View all",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .graphicsLayer { rotationZ = rotation }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    /*
                    // Categories grid
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth(.96f)
                            .heightIn(min = 40.dp, max = (40.dp * 3))
                            .align(Alignment.CenterHorizontally),
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // items(sortedCategories, key = { it.key }) { CategoryCard(it.value, viewModel) }
                        items(20, key = { it }) { CategoryCard(Category(it, "$it category name", it, Date()), viewModel) }
                    }
                    */
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .height(categoriesGridHeight),
                        columns = GridCells.Fixed(3),
                        userScrollEnabled = isCategoriesExpanded, // no scroll needed when collapsed to 9
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(sortedCategories, key = { it.key }) { CategoryCard(it.value, viewModel) }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Items Grid
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .align(Alignment.CenterHorizontally),
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Passing the viewModel because don't want to generate new viewModel for every card
                        items(filteredCashierItems, key = { it.itemId }) { ItemCard(it, viewModel) }
                    }
                }
            }
        }

        if (showBottomSheet && !isExpanded) {
            CashierPartialBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            )
        }

        if (generalAlertDialogStatus.showDialog) {
            GeneralAlertDialog(
                generalAlertDialogStatus,
                onConfirmation = { viewModel.onEvent(CashierEvent.OnConfirmGeneralAlertDialog) },
                onDismissRequest = { viewModel.onEvent(CashierEvent.OnConfirmGeneralAlertDialog) },
            )
        }

        if (midtransDialogState) {
            MidtransPaymentDialog(
                paymentURL,
                onDismiss = { viewModel.onEvent(CashierEvent.OnDismissPaymentGatewayDialog); showBottomSheet = false })
        } else if (transactionCompleteDialogState) {
            TransactionCompleteDialog(
                completeTransactionParams = completeTransactionParams,
                isPrinting = isPrinting,
                onConfirm = {
                    viewModel.onEvent(CashierEvent.OnConfirmTransactionBtnDialog)
                    showBottomSheet = false
                },
                onDismiss = {
                    viewModel.onEvent(CashierEvent.OnConfirmTransactionBtnDialog)
                    showBottomSheet = false
                },
                onPrint = { viewModel.onEvent(CashierEvent.OnPressPrintReceipt) }
            )
        }

        if (loadAllProductsDialogStatus.showDialog) {
            GeneralAlertDialog(
                loadAllProductsDialogStatus,
                confirmText = "Try Again",
                cancelText = "Dismiss",
                onConfirmation = { viewModel.onEvent(CashierEvent.TryAgainRequestAllProducts) },
                onDismissRequest = { viewModel.onEvent(CashierEvent.OnDismissTryAgainRequestAllProducts) },
            )
        }

        if (informationDialogStatus.showDialog) {
            InformationDialog(false, viewModel)
        }
    }
}

@Composable
private fun CashierProductCatalog(
    sortedCategories: List<Map.Entry<Int, Category>>,
    categoryCount: Int,
    isCategoriesExpanded: Boolean,
    categoriesGridHeight: Dp,
    filteredCashierItems: List<CashierItem>,
    itemColumnCount: Int,
    categoryColumnCount: Int,
    onToggleCategories: () -> Unit,
    viewModel: CashierViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Categories",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "$categoryCount total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))

            if (categoryCount > categoryColumnCount * 3) {
                val rotation by animateFloatAsState(
                    targetValue = if (isCategoriesExpanded) 180f else 0f,
                    label = "chevronRotation"
                )
                TextButton(
                    modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    onClick = onToggleCategories,
                ) {
                    Text(
                        if (isCategoriesExpanded) "Show less" else "View all",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .height(categoriesGridHeight),
            columns = GridCells.Fixed(categoryColumnCount),
            userScrollEnabled = isCategoriesExpanded,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedCategories, key = { it.key }) { CategoryCard(it.value, viewModel) }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .align(Alignment.CenterHorizontally),
            columns = GridCells.Fixed(itemColumnCount),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCashierItems, key = { it.itemId }) { ItemCard(it, viewModel) }
        }
    }
}