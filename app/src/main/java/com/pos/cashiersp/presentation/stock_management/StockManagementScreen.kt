package com.pos.cashiersp.presentation.stock_management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.global_component.SimpleSearchBar
import com.pos.cashiersp.presentation.global_component.TextWithNoPadding
import com.pos.cashiersp.presentation.stock_management.component.EditItemStockDialog
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    drawerState: DrawerState,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val openAlertDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Secondary100),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(.90f),
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
                                Screen.STOCK_MANAGEMENT,
                                color = Secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag(TestTags.StockManagementScreen.STOCK_MANAGEMENT_TITLE)
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
                textFieldState = TextFieldState(),
                onSearch = fun(s: String) {},
                searchResults = listOf("Test Values"),
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Because only 1 store that user should access then
            // load only 1 store all items

            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                val cardTitles = listOf("Total Products", "Low Stock", "Out of Stock")
                cardTitles.forEach { title ->
                    Card(
                        modifier = Modifier
                            .weight(.33f)
                            .height(52.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        ),
                    ) {
                        Text(
                            title, // Will be tested
                            color = Gray300,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                        Text(
                            "180",
                            color = Secondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Here is the header
            val idColumnW = .18f
            val itemNameColumnW = .4f
            val qtyColumnW = .17f
            val priceColumnW = .25f
            Box(
                Modifier
                    .padding(horizontal = 6.dp)
                    .border(BorderStroke(1.dp, Secondary))
            ) {
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ID",
                        Modifier
                            .weight(idColumnW)
                            .padding(8.dp),
                        fontSize = 12.sp,
                        color = Secondary
                    )
                    Text(
                        text = "Name",
                        Modifier
                            .weight(itemNameColumnW)
                            .padding(8.dp),
                        fontSize = 12.sp,
                        color = Secondary
                    )
                    Text(
                        text = "Qty",
                        Modifier
                            .weight(qtyColumnW)
                            .padding(8.dp),
                        fontSize = 12.sp,
                        color = Secondary
                    )
                    Text(
                        text = "Price",
                        Modifier
                            .weight(priceColumnW)
                            .padding(8.dp),
                        fontSize = 12.sp,
                        color = Secondary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Items list
            Box {
                LazyVerticalStaggeredGrid(
                    verticalItemSpacing = 8.dp,
                    columns = StaggeredGridCells.Fixed(1),
                    modifier = Modifier
                        .nestedScroll(rememberNestedScrollInteropConnection())
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .height(452.dp)
                ) {
                    for (i in 1..8) {
                        item {
                            Card(
                                border = BorderStroke(1.dp, Primary),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                modifier = Modifier
                                    .fillMaxWidth(1f),
                                onClick = { openAlertDialog.value = true }
                            ) {
                                val priceFormatter = java.text.DecimalFormat("#,###")
                                Row(Modifier.padding(0.dp)) {
                                    Text(
                                        text = "102304",
                                        Modifier
                                            .weight(idColumnW)
                                            .padding(horizontal = 8.dp, vertical = 11.dp),
                                        fontSize = 12.sp,
                                        color = Secondary
                                    )
                                    Text(
                                        "This is a long name that should be truncate",
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .weight(itemNameColumnW)
                                            .padding(horizontal = 8.dp, vertical = 11.dp),
                                        fontSize = 12.sp,
                                        color = Secondary
                                    )
                                    Text(
                                        text = "10_000",
                                        Modifier
                                            .weight(qtyColumnW)
                                            .padding(horizontal = 8.dp, vertical = 11.dp),
                                        fontSize = 12.sp,
                                        color = Secondary
                                    )
                                    Text(
                                        text = "100_000_000",
                                        Modifier
                                            .weight(priceColumnW)
                                            .padding(horizontal = 8.dp, vertical = 11.dp),
                                        fontSize = 12.sp,
                                        color = Secondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Loading / Gray Frame
            }

            Spacer(Modifier.height(8.dp))

            // Button for next and previous page
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag(TestTags.StockManagementScreen.NEXT_LIST_PAGE_BUTTON),
                    colors = IconButtonColors(
                        containerColor = Primary,
                        contentColor = White,
                        disabledContainerColor = White,
                        disabledContentColor = White
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = "A button to load previous page of the items managements",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text("13 / 25", color = Secondary, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 16.dp))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag(TestTags.StockManagementScreen.NEXT_LIST_PAGE_BUTTON),
                    colors = IconButtonColors(
                        containerColor = Primary,
                        contentColor = White,
                        disabledContainerColor = White,
                        disabledContentColor = White
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "A button to load next page of the items managements",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

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
}