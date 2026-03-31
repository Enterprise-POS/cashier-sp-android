package com.pos.cashiersp.presentation.stock_management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.R
import com.pos.cashiersp.model.dto.StockType
import com.pos.cashiersp.model.dto.StoreStockV2
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialog
import com.pos.cashiersp.presentation.global_component.SimpleSearchBar
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.stock_management.component.EditItemStockDialog
import com.pos.cashiersp.presentation.stock_management.component.ItemDetailBottomSheet
import com.pos.cashiersp.presentation.transaction_history.components.getPageNumbers
import com.pos.cashiersp.presentation.ui.theme.Danger
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray400
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Orange
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Purple700
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.ceil
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    drawerState: DrawerState,
    navController: NavController,
    viewModel: StockManagementViewModel = hiltViewModel()
) {
    val storeStocksV2 = viewModel.storeStocks.value
    val page = viewModel.page.value
    val itemsPerPage = viewModel.itemsPerPage.value
    val itemsTotal = viewModel.itemsTotal.value
    val requestingState = viewModel.requestingState.value

    val searchTextFieldState = viewModel.searchTextFieldState
    val openAlertDialog = remember { mutableStateOf(false) }

    val totalPages = ceil(itemsTotal.toDouble() / itemsPerPage.value.toDouble()).toInt()
    val (unlimitedStock, trackedStock, lowOrOufOfStock) = remember(storeStocksV2) {
        var unlimited = 0;
        var tracked = 0;
        var lowOrOut = 0
        for (item in storeStocksV2) {
            if (item.stockType == StockType.UNLIMITED) unlimited++ else tracked++
            if (item.stocks <= 5 && item.stockType == StockType.TRACKED) lowOrOut++
        }

        return@remember Triple(unlimited, tracked, lowOrOut)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is StockManagementViewModel.UIEvent.ErrorAndMustNavigateToSelectTenantScreen -> {
                    navController.navigate(Screen.SELECT_TENANT) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Secondary100),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Column {
                        TextWithNoPadding(
                            "Stock Management",
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        TextWithNoPadding(
                            "See all store products in details",
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
                enabled = !requestingState.isLoading,
                textFieldState = searchTextFieldState,
                onClear = { viewModel.onEvent(StockManagementEvent.OnClearSearchProduct) },
                onSearch = { viewModel.onEvent(StockManagementEvent.OnSearchProduct(it)) },
            )

            // Because only 1 store that user should access then
            // load only 1 store all items

            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                val cardTitles = listOf("Unlimited stock", "Tracked stock", "Low / out of Stock")
                cardTitles.forEach { title ->
                    Card(
                        modifier = Modifier
                            .weight(.33f)
                            .height(52.dp),
                        border = BorderStroke(width = 0.8.dp, color = Gray100.copy(alpha = 0.4f)),
                        colors = CardDefaults.cardColors(containerColor = White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.1.dp),
                    ) {
                        TextWithNoPadding(
                            title, // Will be tested
                            color = Gray300,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 10.dp)
                        )
                        Spacer(Modifier.height(2.dp))

                        val headerInfoTextUI = if (title == "Unlimited stock")
                            unlimitedStock
                        else if (title == "Tracked stock")
                            trackedStock
                        else lowOrOufOfStock
                        Text(
                            headerInfoTextUI.toString(),
                            color = Secondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            ProductCatalogCard(
                storeStocksV2 = storeStocksV2,
                currentPage = page,
                totalItemCount = itemsTotal,
                itemsPerPage = itemsPerPage,
                totalPages = totalPages, // Derived state
                viewModel = viewModel
            )
        }
    }

    ItemDetailBottomSheet()

    // This is where user could edit / request to add new item stock
    if (openAlertDialog.value) {
        EditItemStockDialog(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = {
                openAlertDialog.value = false
                println("Confirmation registered") // Add logic here to handle confirmation.
            },
            dialogTitle = "Alert dialog example",
            dialogText = "This is an example of an alert dialog with buttons.",
            icon = Icons.Default.Info
        )
    }

    IsolatedGeneralAlert()
}

@Composable
private fun IsolatedGeneralAlert(viewModel: StockManagementViewModel = hiltViewModel()) {
    val generalAlertDialogStatus = viewModel.generalAlertDialogStatus.value
    if (generalAlertDialogStatus.showDialog) {
        GeneralAlertDialog(
            generalAlertDialogStatus = generalAlertDialogStatus,
            onDismissRequest = { viewModel.onEvent(StockManagementEvent.OnCloseGeneralDialog) },
        )
    }
}


/* Labeling the. Will not be use outside this file*/
enum class StockStatus(val label: String) {
    IN_STOCK("In stock"),
    LOW_STOCK("Low stock"),
    OUT_OF_STOCK("Out of stock"),
    UNLIMITED("∞")
}


/** Color for the stock text on the right side */
private fun stockTextColor(status: StockStatus): Color = when (status) {
    StockStatus.IN_STOCK -> Gray400
    StockStatus.LOW_STOCK -> Primary
    StockStatus.OUT_OF_STOCK -> Danger
    StockStatus.UNLIMITED -> Gray400
}


@Composable
private fun InlineChip(label: String, containerColor: Color) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = containerColor,
    ) {
        Text(
            text = label,
            color = White,
            fontSize = 9.sp,
            fontWeight = FontWeight.W600,
            letterSpacing = 0.2.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
        )
    }
}


@Composable
fun ItemRowFlat(
    item: StoreStockV2,
    showDivider: Boolean = true,
    viewModel: StockManagementViewModel
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val (stockTextUIColor, stockTextUI) =
        if (item.stockType == StockType.UNLIMITED) Pair(StockStatus.UNLIMITED, StockStatus.UNLIMITED.label)
        else if (item.stocks == 0) Pair(StockStatus.OUT_OF_STOCK, StockStatus.OUT_OF_STOCK.label)
        else if (item.stocks > 5) Pair(StockStatus.IN_STOCK, "${StockStatus.IN_STOCK.label}: ${item.stocks}")
        else Pair(StockStatus.LOW_STOCK, "${StockStatus.LOW_STOCK.label}: ${item.stocks}")

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Emoji icon tile
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF3F0EC),
                modifier = Modifier.size(40.dp),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.noimage_compressed)
                        .size(120, 120) // sample down to needed size
                        .crossfade(true)
                        .build(),
                    contentDescription = "Product image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(10.dp))

            // Name + subtitle chips
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.itemName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(4.dp))

                // Subtitle: ID · Active/Inactive chip · bullet · Tracked/Unlimited chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "ID: ${item.id}",
                        fontSize = 11.sp,
                        color = Gray400,
                    )

                    /*
                    Text("·", fontSize = 11.sp, color = Gray400)

                    InlineChip(
                        label = if (item. == ItemActiveStatus.ACTIVE) "Active" else "Inactive",
                        containerColor = if (item.activeStatus == ItemActiveStatus.ACTIVE)
                            Success else Gray400,
                    )
                     */

                    Text("·", fontSize = 11.sp, color = Gray400)

                    InlineChip(
                        label = if (item.stockType == StockType.TRACKED) "Tracked" else "Unlimited",
                        containerColor = if (item.stockType == StockType.TRACKED)
                            Orange else Purple700,
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Right: price + stock label
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rp %.2f".format(item.price.toDouble()),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    color = Secondary,
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = stockTextUI,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W600,
                    color = stockTextColor(
                        stockTextUIColor
                    ),
                )
            }

            // Three-dot menu
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .size(28.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Gray400,
                        modifier = Modifier.size(18.dp),
                    )
                }

                ItemDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    onViewDetails = { viewModel.onEvent(StockManagementEvent.OnTapViewDetailsDropDown(item)) },
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = Gray100.copy(alpha = 0.7f),
                thickness = 0.8.dp,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
private fun PaginationRow(
    currentPage: Int,
    totalPages: Int,
    itemsPerPage: ItemsPerPage,
    viewModel: StockManagementViewModel,
) {

    val requestingState = viewModel.requestingState.value
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Page $currentPage of $totalPages · ${itemsPerPage.value} products per page",
            fontSize = 11.sp,
            color = Gray400,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(
                onClick = { viewModel.onEvent(StockManagementEvent.OnTapPrevPageButton) },
                enabled = currentPage > 1 || !!requestingState.isLoading,
                shape = RoundedCornerShape(7.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                modifier = Modifier.height(28.dp),
                border = BorderStroke(0.8.dp, Gray100),
            ) {
                Text("‹ Prev", fontSize = 11.sp, color = Secondary)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Called from  PaginationBar from transactionHistory
                getPageNumbers(currentPage, totalPages, 7).forEach { page ->
                    if (page == -1) {
                        Text("…", fontSize = 12.sp, color = Gray400)
                    } else {
                        val isActive = page == currentPage
                        Button(
                            onClick = { viewModel.onEvent(StockManagementEvent.OnTapPaginationPageButton(page)) },
                            shape = RoundedCornerShape(7.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActive) Primary else White,
                                contentColor = if (isActive) White else Secondary,
                            ),
                            border = if (!isActive) BorderStroke(0.8.dp, Gray100) else null,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(28.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                        ) {
                            Text(
                                text = "$page",
                                fontSize = 11.sp,
                                fontWeight = if (isActive) FontWeight.W700 else FontWeight.W400,
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.onEvent(StockManagementEvent.OnTapNextPageButton) },
                enabled = currentPage < totalPages || !!requestingState.isLoading,
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                modifier = Modifier.height(28.dp),
            ) {
                Text("Next ›", fontSize = 11.sp, fontWeight = FontWeight.W600)
            }
        }
    }
}

@Composable
private fun ProductCatalogCard(
    storeStocksV2: List<StoreStockV2>,
    totalItemCount: Int,
    currentPage: Int,
    totalPages: Int,
    itemsPerPage: ItemsPerPage,
    viewModel: StockManagementViewModel,
) {
    val requestingState = viewModel.requestingState.value

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
                Offset.Zero
        }
    }

    Card(
        border = BorderStroke(width = 0.8.dp, color = Gray100.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier.padding(horizontal = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.1.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 16.dp)
                .fillMaxWidth(),
        ) {

            // ── Header ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Product catalog",
                        fontSize = 16.sp,
                        color = Secondary,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = "${if (requestingState.isLoading) "-" else totalItemCount} products total",
                        fontSize = 12.sp,
                        color = Gray400,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Refresh
                    Button(
                        onClick = { viewModel.onEvent(StockManagementEvent.OnRefreshItemCatalogButton) },
                        shape = RoundedCornerShape(8.dp),
                        enabled = !requestingState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sort",
                            tint = White,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    // Sort icon button  ↕
                    /*
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        enabled = !requestingState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Sort",
                            tint = White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                     */
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Item list ────────────────────────────────────────────────────
            LazyVerticalStaggeredGrid(
                verticalItemSpacing = 0.dp,
                columns = StaggeredGridCells.Fixed(1),
                modifier = Modifier
                    .nestedScroll(nestedScrollConnection)
                    .fillMaxWidth()
                    .fillMaxHeight(.84f),
            ) {
                if (requestingState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp), // or whatever fits your layout
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Primary, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Please wait while requesting...", color = Gray400, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    itemsIndexed(storeStocksV2, key = { _, item -> item.id }) { index, item ->
                        ItemRowFlat(
                            item = item,
                            showDivider = index < storeStocksV2.lastIndex,
                            viewModel = viewModel,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            HorizontalDivider(color = Gray100.copy(alpha = 0.6f), thickness = 0.8.dp)

            Spacer(Modifier.height(10.dp))

            // ── Pagination ────────────────────────────────────────────────────
            PaginationRow(
                currentPage = currentPage,
                totalPages = totalPages,
                itemsPerPage = itemsPerPage,
                viewModel
            )
        }
    }
}

private data class CatalogMenuAction(
    val label: String,
    val icon: ImageVector,
    val color: Color = Secondary,
    val hasSubMenu: Boolean = false,
)

private val menuActions = listOf(
    CatalogMenuAction(
        label = "View details",
        icon = Icons.Outlined.RemoveRedEye,
    ),

    /*
    CatalogMenuAction(
        label = "Edit item",
        icon = Icons.Outlined.Edit,
    ),
    CatalogMenuAction(
        label = "Deactivate",
        icon = Icons.Outlined.PowerSettingsNew,
        color = Danger,
        hasSubMenu = true,
    ),
    * */
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onViewDetails: () -> Unit, // Closure for access current item
    onEditItem: () -> Unit = {},
    onDeactivate: () -> Unit = {},
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(12.dp) // rounds the dropdown card itself
        )
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.width(180.dp),
        ) {
            menuActions.forEachIndexed { index, action ->
                DropdownMenuItem(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = action.color,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    text = {
                        Text(
                            text = action.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W500,
                            color = action.color,
                        )
                    },
                    trailingIcon = if (action.hasSubMenu) {
                        {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                contentDescription = "Has submenu",
                                tint = action.color,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    } else null,
                    onClick = {
                        onDismissRequest()
                        when (action.label) {
                            "View details" -> onViewDetails()
                            "Edit item" -> onEditItem()
                            // "Deactivate" -> onDeactivate()
                        }
                    },
                )


                // Divider between items except after the last one
                if (index < menuActions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        thickness = 0.6.dp,
                        color = Gray100,
                    )
                }
            }
        }
    }
}