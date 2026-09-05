package com.pos.cashiersp.presentation.global_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pos.cashiersp.R
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import kotlinx.coroutines.launch

private val pagesUtils = mapOf(
    Screen.CASHIER to Pair("Cashier Screen Navigation", Icons.Outlined.ShoppingCart),
    Screen.STOCK_MANAGEMENT to Pair("Stock Management Screen Navigation", Icons.Outlined.List),
    Screen.TRANSACTION_HISTORY to Pair("Transaction History Screen Navigation", Icons.Outlined.Archive),
    Screen.ITEM_SALES_LOG to Pair("Item Sales Log / Purchased Item List Navigation", Icons.Outlined.Summarize),
    Screen.BLUETOOTH_SETTINGS to Pair("Bluetooth Settings Navigation", Icons.Default.Bluetooth),
)

private val othersUtils = mapOf(
    Screen.SETTINGS to Pair("Settings Button", Icons.Outlined.Settings),
    Screen.LOGOUT to Pair("Logout Button", Icons.Outlined.ExitToApp)
)

@Composable
fun CashierDrawer(navController: NavController, content: @Composable (drawerState: DrawerState) -> Unit) {
    val scope = rememberCoroutineScope()
    val currentRoute by navController.currentBackStackEntryAsState()
    val currentTitle = remember(currentRoute) { currentRoute?.destination?.route }

    val isAllowedToOpen = remember(currentTitle) {
        when {
            currentTitle == Screen.LOGIN_REGISTER -> false
            currentTitle == Screen.SELECT_TENANT -> false
            currentTitle?.contains(Screen.SELECT_STORE) == true -> false
            else -> true
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val onNavigate: (String) -> Unit = remember {
        { route ->
            scope.launch { drawerState.close() }
            navController.navigate(route)
        }
    }

    // Content
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isAllowedToOpen,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(210.dp)
                    .testTag(TestTags.DRAWER_CONTAINER)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.enterprise_pos_logo),
                        contentDescription = stringResource(id = R.string.enterprise_pos_logo),
                        modifier = Modifier
                            .size(65.dp)
                            .testTag(TestTags.ENTERPRISE_POS_LOGO)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Navigation buttons
                // Header
                Text("Pages", fontSize = 14.sp, color = Gray600, modifier = Modifier.padding(start = 8.dp))
                Spacer(Modifier.height(8.dp))

                pagesUtils.forEach { (title, values) ->
                    TextButton(
                        modifier = Modifier
                            .padding(start = 6.dp, end = 12.dp)
                            .fillMaxWidth()
                            .height(36.dp), // Button height but padding like
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            onNavigate(title)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = values.second,
                                contentDescription = values.first,
                                tint = if (currentTitle == title) Primary else Gray600,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 6.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                title,
                                color = if (currentTitle == title) Primary else Gray600,
                                fontSize = 16.sp,
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
                                )
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    Text("Others", fontSize = 14.sp, color = Gray600, modifier = Modifier.padding(start = 8.dp))
                    Spacer(Modifier.height(8.dp))

                    othersUtils.forEach { (title, values) ->
                        TextButton(
                            modifier = Modifier
                                .padding(start = 6.dp, end = 12.dp)
                                .fillMaxWidth()
                                .height(36.dp), // Button height but padding like
                            contentPadding = PaddingValues(0.dp),
                            onClick = { onNavigate(title) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = values.second,
                                    contentDescription = values.first,
                                    tint = if (currentTitle == title) Primary else Gray600,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(start = 6.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    title,
                                    color = if (currentTitle == title) Primary else Gray600,
                                    fontSize = 16.sp,
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
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        content(drawerState)
    }
}