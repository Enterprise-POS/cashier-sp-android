package com.pos.cashiersp.presentation.cashier

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
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.global_component.SimpleSearchBar
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierScreen(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Secondary100),
        topBar = {
            TopAppBar(
                actions = {
                    OutlinedButton(
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Secondary),
                        modifier = Modifier
                            .width(140.dp),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Shopping cart icon",
                            tint = White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("2 items", color = White)
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
                textFieldState = TextFieldState(),
                onSearch = fun(s: String) {},
                searchResults = listOf(),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(.96f)
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Categories", style = TextStyle.Default.copy(color = Secondary))
                Spacer(modifier = Modifier.width(18.dp))
                Text("20 total", style = TextStyle.Default.copy(color = Gray300))
                Spacer(modifier = Modifier.weight(1f))
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
                items(12) { count ->
                    @Composable
                    fun categoryCard(active: Boolean) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (active) Primary else Secondary),
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "All $count",
                                    style = TextStyle(color = White, fontWeight = FontWeight.W600, fontSize = 12.sp)
                                )
                            }
                        }
                    }
                    if (count == 0)
                        categoryCard(true)
                    else
                        categoryCard(false)
                }
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
                items(12) {
                    @Composable
                    fun itemCard(active: Boolean) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = White
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                // Coffee Image
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.fillMaxSize(),
                                        model = "https://images.unsplash.com/photo-1541167760496-1628856ab772?q=80&w=125&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                                        contentDescription = null,
                                    )
                                }

                                // Content Section
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    // Title
                                    Text(
                                        text = "Iced Coffee",
                                        fontSize = 12.sp,
                                        color = Secondary,
                                        modifier = Modifier.height(20.dp),
                                    )

                                    // Size and Volume
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Medium",
                                            fontSize = 10.sp,
                                            color = Gray300,
                                            fontWeight = FontWeight.W400,
                                            style = LocalTextStyle.current.merge(
                                                TextStyle(
                                                    lineHeight = 14.sp,
                                                    platformStyle = PlatformTextStyle(
                                                        includeFontPadding = false
                                                    ),
                                                    lineHeightStyle = LineHeightStyle(
                                                        alignment = LineHeightStyle.Alignment.Center,
                                                        trim = LineHeightStyle.Trim.None
                                                    )
                                                )
                                            ),
                                            modifier = Modifier
                                                .height(12.dp)
                                        )
                                        Text(
                                            text = " • ",
                                            fontSize = 10.sp,
                                            color = Gray300,
                                            fontWeight = FontWeight.W400,
                                            style = LocalTextStyle.current.merge(
                                                TextStyle(
                                                    lineHeight = 14.sp,
                                                    platformStyle = PlatformTextStyle(
                                                        includeFontPadding = false
                                                    ),
                                                    lineHeightStyle = LineHeightStyle(
                                                        alignment = LineHeightStyle.Alignment.Center,
                                                        trim = LineHeightStyle.Trim.None
                                                    )
                                                )
                                            ),
                                            modifier = Modifier
                                                .height(12.dp)
                                        )
                                        Text(
                                            text = "16 oz",
                                            fontSize = 10.sp,
                                            color = Gray300,
                                            fontWeight = FontWeight.W400,
                                            style = LocalTextStyle.current.merge(
                                                TextStyle(
                                                    lineHeight = 14.sp,
                                                    platformStyle = PlatformTextStyle(
                                                        includeFontPadding = false
                                                    ),
                                                    lineHeightStyle = LineHeightStyle(
                                                        alignment = LineHeightStyle.Alignment.Center,
                                                        trim = LineHeightStyle.Trim.None
                                                    )
                                                )
                                            ),
                                            modifier = Modifier
                                                .height(12.dp)
                                        )
                                    }

                                    // Price
                                    var price: Double = 10_000.0
                                    Text(
                                        text = "$${String.format("%.2f", price)}",
                                        fontSize = 12.sp,
                                        color = Secondary
                                    )

                                    // Price and Add Button
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Minus Button
                                        Button(
                                            onClick = { },
                                            modifier = Modifier.size(18.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Primary
                                            ),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                                contentDescription = "Add to cart",
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }

                                        Text(
                                            "99",
                                            fontSize = 14.sp,
                                            color = Secondary
                                        )

                                        // Add Button
                                        Button(
                                            onClick = { },
                                            modifier = Modifier.size(18.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Primary
                                            ),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.KeyboardArrowUp,
                                                contentDescription = "Add to cart",
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (it == 0)
                        itemCard(true)
                    else
                        itemCard(false)
                }
            }
        }
    }
}