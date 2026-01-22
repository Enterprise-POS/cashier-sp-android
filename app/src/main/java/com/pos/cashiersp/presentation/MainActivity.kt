package com.pos.cashiersp.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pos.cashiersp.BuildConfig
import com.pos.cashiersp.presentation.bluetooth_settings.BluetoothSettingsScreen
import com.pos.cashiersp.presentation.cashier.CashierScreen
import com.pos.cashiersp.presentation.global_component.CashierDrawer
import com.pos.cashiersp.presentation.greeting.GreetingScreen
import com.pos.cashiersp.presentation.login_register.LoginRegisterScreen
import com.pos.cashiersp.presentation.select_store.SelectStoreScreen
import com.pos.cashiersp.presentation.select_tenant.SelectTenantScreen
import com.pos.cashiersp.presentation.stock_management.StockManagementScreen
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryScreen
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.i("AppStateMode", BuildConfig.MODE)

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            // Log the permission results
            perms.forEach { (permission, granted) ->
                Log.d("Permissions", "$permission: $granted")
            }

            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        // Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        } else {
            // Android 11 and below
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }

        // For testing we don't want MainActivity setContent
        if (BuildConfig.MODE == "dev" || BuildConfig.MODE == "prod") {
            setContent {
                CashierSPTheme {
                    val navController: NavHostController = rememberNavController()
                    CashierDrawer(navController) { drawerState: DrawerState ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.TRANSACTION_HISTORY
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
                            composable(route = Screen.TRANSACTION_HISTORY) {
                                TransactionHistoryScreen(navController)
                            }
                            composable(route = Screen.SETTINGS) {
                            }
                            composable(route = Screen.LOGIN_REGISTER) {
                                LoginRegisterScreen(navController)
                            }
                            composable(route = Screen.SELECT_TENANT) {
                                SelectTenantScreen(navController)
                            }
                            composable(
                                route = Screen.SELECT_STORE + "/{tenantId}/{tenantName}",
                                arguments = listOf(
                                    navArgument(name = "tenantId") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                    navArgument(name = "tenantName") {
                                        type = NavType.StringType
                                        defaultValue = ""
                                    }
                                )
                            ) {
                                val tenantId = it.arguments?.getInt("tenantId") ?: -1
                                val tenantName = it.arguments?.getString("tenantName") ?: ""
                                SelectStoreScreen(tenantId, tenantName, navController)
                            }
                            composable(route = Screen.BLUETOOTH_SETTINGS) {
                                BluetoothSettingsScreen(navController, drawerState)
                            }
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