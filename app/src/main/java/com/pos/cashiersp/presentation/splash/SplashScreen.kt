package com.pos.cashiersp.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "splashFade"
    )

    LaunchedEffect(key1 = true) {
        viewModel.splashUIEvent.collectLatest { event ->
            when (event) {
                SplashViewModel.SplashUIEvent.NavigateToCashier -> {
                    navController.navigate(Screen.CASHIER) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }

                SplashViewModel.SplashUIEvent.NavigateToLoginRegister -> {
                    navController.navigate(Screen.LOGIN_REGISTER) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }

                SplashViewModel.SplashUIEvent.Loading -> { /* ... */
                }

                SplashViewModel.SplashUIEvent.NavigateToSelectTenant -> {
                    navController.navigate(Screen.SELECT_TENANT) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }
            }
        }

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Secondary100,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.height(28.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Checking your session...",
                    color = Secondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}