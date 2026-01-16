package com.pos.cashiersp.presentation.cashier

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.cashier.component.CashierPartialBottomSheet
import com.pos.cashiersp.presentation.cashier.component.CategoryCard
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialog
import com.pos.cashiersp.presentation.cashier.component.ItemCard
import com.pos.cashiersp.presentation.global_component.SimpleSearchBar
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: CashierViewModel = hiltViewModel()
) {
    // viewmodel
    val vmState = viewModel.state.value
    val cart = viewModel.cart.value
    val categories = viewModel.categories.value
    val selectedCategoryId = viewModel.selectedCategory.value
    val cashierItems = viewModel.cashierItems.value
    val searchProductString = viewModel.searchProductString.value
    val generalAlertDialogStatus = viewModel.generalAlertDialogStatus.value
    val loadAllProductsDialogStatus = viewModel.loadAllProductsDialogStatus.value

    // scope
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val searchTextFieldState = remember { TextFieldState() }

    val onHandleBottomSheet = fun() {
        showBottomSheet = !showBottomSheet
    }
    val filteredCashierItems =
        if (searchProductString.isNotEmpty()) {
            // If user input something at Search Bar
            if (selectedCategoryId == -1) {
                // User input something at Search Bar but don't select any categories available / default at 'All'
                cashierItems.filter { it.itemName.contains(searchProductString) }
            } else {
                // User input something at Search Bar and select of the categories available
                cashierItems.filter { it.itemName.contains(searchProductString) && it.categoryId == selectedCategoryId }
            }
        } else if (selectedCategoryId == -1) {
            // If User don't input something, and don't select any categories available / default as 'All'
            cashierItems
        } else {
            // If User don't input something, but click 1 of the categories available
            cashierItems.filter { it.categoryId == selectedCategoryId }
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
        modifier = Modifier
            .fillMaxSize()
            .background(Secondary100),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                actions = {
                    OutlinedButton(
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Secondary),
                        onClick = onHandleBottomSheet,
                        modifier = Modifier
                            .width(140.dp)
                            .testTag(TestTags.CashierScreen.CHART_BUTTON),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Shopping cart icon",
                            tint = White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("${cart.size} items", color = White)
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Button to open drawer (Hamburger Button)
                        IconButton(
                            modifier = Modifier
                                .width(48.dp)
                                // Because by default the action button has padding
                                // We need to tweak a bit offset
                                .offset(x = (-8).dp)
                                .testTag(TestTags.CashierScreen.MENU_DRAWER_BUTTON),
                            colors = IconButtonColors(
                                containerColor = Secondary,
                                contentColor = White,
                                disabledContainerColor = Secondary,
                                disabledContentColor = Secondary,
                            ),
                            onClick = {
                                scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Menu to open drawer (Hamburger Button)",
                                tint = White,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }


                        // Title Text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Cashier",
                                color = Secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag(TestTags.CashierScreen.CASHIER_TITLE)
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            SimpleSearchBar(
                searchResults = listOf(),
                enabled = !vmState.isLoading,
                textFieldState = searchTextFieldState,
                onClear = { viewModel.onEvent(CashierEvent.OnClearSearchProduct) },
                onSearch = { viewModel.onEvent(CashierEvent.OnSearchProduct(it)) },
            )
            Spacer(modifier = Modifier.height(2.dp))

            if (vmState.isLoading) {
                // Center the loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(48.dp) // Made it bigger for better visibility
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = vmState.loadingMessage,
                            color = Gray300,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(.96f)
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Categories", style = TextStyle.Default.copy(color = Secondary))
                    Spacer(modifier = Modifier.width(18.dp))
                    Text("${categories.size} total", style = TextStyle.Default.copy(color = Gray300))
                    Spacer(modifier = Modifier.weight(1f))
                    if (categories.size >= 12) {
                        TextButton(
                            modifier = Modifier.height(30.dp),
                            colors = ButtonColors(
                                containerColor = White,
                                contentColor = Secondary,
                                disabledContainerColor = Secondary,
                                disabledContentColor = Secondary,
                            ),
                            onClick = {},
                        ) {
                            Text("View all", style = TextStyle(fontSize = 14.sp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

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
                    categories.toSortedMap().forEach { item(it.key) { CategoryCard(it.value) } }
                }

                Spacer(Modifier.height(4.dp))

                // In this items grid, I want the card grid remember the value that user inputted even they changed
                // the categories, and it's re render all the card
                // Items Grid
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth(.96f)
                        .fillMaxHeight(.96f)
                        .heightIn(min = 120.dp)
                        .align(Alignment.CenterHorizontally),
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    filteredCashierItems.forEach { item { ItemCard(it) } }
                }
            }
        }

        if (showBottomSheet) {
            CashierPartialBottomSheet(
                onHandleBottomSheet
            )
        }

        if (generalAlertDialogStatus.showDialog) {
            GeneralAlertDialog(
                generalAlertDialogStatus = generalAlertDialogStatus,
                onConfirmation = { viewModel.onEvent(CashierEvent.OnConfirmGeneralAlertDialog) },
                onDismissRequest = { viewModel.onEvent(CashierEvent.OnConfirmGeneralAlertDialog) },
            )
        }

        if (loadAllProductsDialogStatus.showDialog) {
            GeneralAlertDialog(
                generalAlertDialogStatus = loadAllProductsDialogStatus,
                confirmText = "Try Again",
                cancelText = "Dismiss",
                onConfirmation = { viewModel.onEvent(CashierEvent.TryAgainRequestAllProducts) },
                onDismissRequest = { viewModel.onEvent(CashierEvent.OnDismissTryAgainRequestAllProducts) },
            )
        }
    }
}