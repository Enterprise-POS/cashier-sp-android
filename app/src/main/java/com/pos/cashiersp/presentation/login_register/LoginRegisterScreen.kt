package com.pos.cashiersp.presentation.login_register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.R
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.login_register.component.LoginOrRegisterSegmentedButton
import com.pos.cashiersp.presentation.login_register.component.LoginOrRegisterSegmentedButtonPage
import com.pos.cashiersp.presentation.login_register.component.LoginSide
import com.pos.cashiersp.presentation.login_register.component.RegisterSide
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.JwtStore
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRegisterScreen(navController: NavController, viewModel: LoginRegisterViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentSegmented = remember { mutableStateOf(LoginOrRegisterSegmentedButtonPage.Login) }

    /*
        This is how we manually take data data from DataStore without DI
    * */
    val context = LocalContext.current
    val userPayload by JwtStore(context).getPayload().collectAsState(initial = null)
    println("Payload: $userPayload")
    println("Username: ${userPayload?.name ?: ""}")

    // You can't combine 2 coroutine in LaunchEffect, the collectLatest will suspend indefinitely
    LaunchedEffect(key1 = true) {
        viewModel.loginUIEvent.collectLatest { event ->
            when (event) {
                is LoginRegisterViewModel.LoginUIEvent.NavigateToCashier -> {
                    navController.navigate(Screen.SELECT_TENANT) {
                        popUpTo(Screen.LOGIN_REGISTER) { inclusive = true }
                    }
                }

                is LoginRegisterViewModel.LoginUIEvent.ShowError -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long,
                        withDismissAction = true,
                        actionLabel = "Close"
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {
                            println("Do something when dismissed")
                        }

                        SnackbarResult.ActionPerformed -> {
                            println("Do something")
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.registerUIEvent.collectLatest { event ->
            when (event) {
                is LoginRegisterViewModel.RegisterUIEvent.NavigateToCashier -> {
                    navController.navigate(Screen.SELECT_TENANT) {
                        popUpTo(Screen.LOGIN_REGISTER) { inclusive = true }
                    }
                }

                is LoginRegisterViewModel.RegisterUIEvent.ShowError -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long,
                        withDismissAction = true,
                        actionLabel = "Close"
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {
                            println("Do something when dismissed")
                        }

                        SnackbarResult.ActionPerformed -> {
                            println("Do something")
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.autoRedirect.collectLatest { event ->
            when (event) {
                is LoginRegisterViewModel.AutoRedirect.IsUserLoggedIn -> {
                    val isRedirect = event.boolean
                    if (isRedirect)
                        println("Redirect user to somewhere else, and not this screen")
                    else
                        println("Don't redirect user")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier
            .background(color = Secondary100)
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.enterprise_pos_logo),
                contentDescription = stringResource(id = R.string.enterprise_pos_logo),
                modifier = Modifier
                    .size(65.dp)
                    .testTag(TestTags.ENTERPRISE_POS_LOGO)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                if (currentSegmented.value == LoginOrRegisterSegmentedButtonPage.Login) "Welcome back" else "Hello !",
                fontSize = 28.sp,
                color = Secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_TITLE)
            )
            Text(
                if (currentSegmented.value == LoginOrRegisterSegmentedButtonPage.Login) "Sign in to start a new sale or manage your store"
                else "Create new account and start manage your store",
                fontSize = 12.sp,
                color = Gray600,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_SUB_TITLE)
            )
            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
            ) {
                Column(
                    Modifier.padding(horizontal = 14.dp, vertical = 20.dp)
                ) {
                    LoginOrRegisterSegmentedButton(
                        initialValue = currentSegmented.value,
                        onChanged = { v -> currentSegmented.value = v },
                    )

                    Spacer(Modifier.height(12.dp))

                    when (currentSegmented.value) {
                        LoginOrRegisterSegmentedButtonPage.Login -> LoginSide(snackbarHostState)

                        LoginOrRegisterSegmentedButtonPage.Register -> RegisterSide(snackbarHostState)
                    }
                }
            }
        }
    }
}