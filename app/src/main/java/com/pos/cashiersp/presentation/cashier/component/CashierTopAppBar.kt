package com.pos.cashiersp.presentation.cashier.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.cashier.CashierEvent
import com.pos.cashiersp.presentation.cashier.CashierViewModel
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierTopAppBar(
    onHandleBottomSheet: () -> Unit,
    drawerState: DrawerState,
    viewModel: CashierViewModel = hiltViewModel()
) {
    val cart by viewModel.cart
    val scope = rememberCoroutineScope()

    TopAppBar(
        actions = {
            OutlinedButton(
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Secondary),
                onClick = onHandleBottomSheet,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
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

            OutlinedButton(
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Secondary),
                onClick = { viewModel.onEvent(CashierEvent.OnToggleInfoBtn(true)) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info icon",
                    tint = White
                )
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
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Title Text
                /*
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
                 */
            }
        },
    )
}