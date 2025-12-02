package com.pos.cashiersp.presentation.cashier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
                                .offset(x = (-8).dp),
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
                                modifier = Modifier.size(24.dp)
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
                                textAlign = TextAlign.Center
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
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth(.96f)
                    .align(Alignment.CenterHorizontally),
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(4) { count ->
                    @Composable
                    fun categoryCard(active: Boolean) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (active) Primary else Secondary),
                            modifier = Modifier.height(42.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "All $count",
                                    style = TextStyle(color = White, fontWeight = FontWeight.W600, fontSize = 14.sp)
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
        }
    }
}