package com.pos.cashiersp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.BuildConfig
import com.pos.cashiersp.presentation.greeting.GreetingScreen
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // For testing we don't want MainActivity setContent
        if (BuildConfig.MODE == "dev" || BuildConfig.MODE == "prod") {
            setContent {
                CashierSPTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Greeting.route
                    ) {
                        composable(
                            route = Screen.Greeting.route
                        ) {
                            GreetingScreen(navController)
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CashierSPTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screen.Greeting.route
        ) {
            composable(
                route = Screen.Greeting.route
            ) {
                GreetingScreen(navController)
            }
        }
    }
}