package com.pos.cashiersp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.BuildConfig
import com.pos.cashiersp.presentation.cashier.CashierScreen
import com.pos.cashiersp.presentation.global_component.CashierDrawer
import com.pos.cashiersp.presentation.greeting.GreetingScreen
import com.pos.cashiersp.presentation.login_register.LoginRegisterScreen
import com.pos.cashiersp.presentation.stock_management.StockManagementScreen
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.i("AppStateMode", BuildConfig.MODE)

        // For testing we don't want MainActivity setContent
        if (BuildConfig.MODE == "dev" || BuildConfig.MODE == "prod") {
            setContent {
                CashierSPTheme {
                    val navController: NavHostController = rememberNavController()
                    CashierDrawer(navController) { drawerState: DrawerState ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.LOGIN_REGISTER
                        ) {
                            composable(route = Screen.GREETING) {
                                GreetingScreen(navController)
                            }
                            composable(route = Screen.CASHIER) {
                                CashierScreen(navController, drawerState)
                            }
                            composable(route = Screen.STOCK_MANAGEMENT) {
                                StockManagementScreen(drawerState)
                            }
                            composable(route = Screen.STAFF_MANAGEMENT) {
                            }
                            composable(route = Screen.SETTINGS) {
                            }
                            composable(route = Screen.LOGIN_REGISTER) {
                                LoginRegisterScreen(navController)
                            }
                            composable(route = Screen.SELECT_TENANT) {
                            }
                            /*
                            composable(
                                route = Screen.CoinDetailScreen.route + "/{coinId}"
                            ) {
                                CoinDetailScreen()
                            }
                            *  */
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CashierSPTheme {
        val navController: NavHostController = rememberNavController()
        CashierDrawer(navController) { drawerState: DrawerState ->
            NavHost(
                navController = navController,
                startDestination = Screen.GREETING
            ) {
                composable(route = Screen.GREETING) {
                    GreetingScreen(navController)
                }
            }
        }
    }
}