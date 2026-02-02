package com.pos.cashiersp.presentation.logout

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.presentation.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LogoutScreen(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: LogoutViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.authorizationUIEvent.collectLatest { event ->
            drawerState.close()
            when (event) {
                LogoutViewModel.AuthorizationUIEvent.Logout -> navController.navigate(Screen.LOGIN_REGISTER) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}