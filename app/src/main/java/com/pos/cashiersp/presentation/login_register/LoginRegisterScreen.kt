package com.pos.cashiersp.presentation.login_register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pos.cashiersp.R
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.login_register.component.LoginOrRegisterSegmentedButton
import com.pos.cashiersp.presentation.login_register.component.LoginOrRegisterSegmentedButtonPage
import com.pos.cashiersp.presentation.login_register.component.LoginSide
import com.pos.cashiersp.presentation.login_register.component.RegisterSide
import com.pos.cashiersp.presentation.ui.theme.CashierSPTheme
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
    //println("Payload: $userPayload")
    //println("Username: ${userPayload?.name ?: ""}")

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
                    // For debugging purpose, change from SELECT_TENANT into CASHIER or etc..
                    // Don't forget to change the tenantId and storeId while debugging
                    val isRedirect = event.boolean
                    if (isRedirect)
                    // from be
                        navController.navigate(Screen.SELECT_TENANT) {
                            popUpTo(Screen.LOGIN_REGISTER) { inclusive = true }
                        }
                    else
                    // Don't redirect user
                        return@collectLatest
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Secondary100
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isExpanded = maxWidth >= 840.dp
            val isMedium = maxWidth in 600.dp..839.dp

            if (isExpanded) {
                ExpandedLoginRegisterLayout(
                    currentSegmented = currentSegmented.value,
                    onSegmentedChange = { currentSegmented.value = it },
                    snackbarHostState = snackbarHostState
                )
            } else {
                CompactMediumLoginRegisterLayout(
                    isMedium = isMedium,
                    currentSegmented = currentSegmented.value,
                    onSegmentedChange = { currentSegmented.value = it },
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
private fun ExpandedLoginRegisterLayout(
    currentSegmented: LoginOrRegisterSegmentedButtonPage,
    onSegmentedChange: (LoginOrRegisterSegmentedButtonPage) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 48.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1040.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Pane: Brand identity & Welcome header
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.enterprise_pos_logo),
                    contentDescription = stringResource(id = R.string.enterprise_pos_logo),
                    modifier = Modifier
                        .size(80.dp)
                        .testTag(TestTags.ENTERPRISE_POS_LOGO)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = if (currentSegmented == LoginOrRegisterSegmentedButtonPage.Login) "Welcome back" else "Hello !",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_TITLE)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (currentSegmented == LoginOrRegisterSegmentedButtonPage.Login)
                        "Sign in to start a new sale or manage your store"
                    else
                        "Create new account and start manage your store",
                    fontSize = 15.sp,
                    color = Gray600,
                    lineHeight = 22.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_SUB_TITLE)
                )
            }

            // Right Pane: Form Card
            Column(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AuthCard(
                    currentSegmented = currentSegmented,
                    onSegmentedChange = onSegmentedChange,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
private fun CompactMediumLoginRegisterLayout(
    isMedium: Boolean,
    currentSegmented: LoginOrRegisterSegmentedButtonPage,
    onSegmentedChange: (LoginOrRegisterSegmentedButtonPage) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = if (isMedium) 32.dp else 16.dp,
                vertical = 24.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.enterprise_pos_logo),
                contentDescription = stringResource(id = R.string.enterprise_pos_logo),
                modifier = Modifier
                    .size(if (isMedium) 72.dp else 65.dp)
                    .testTag(TestTags.ENTERPRISE_POS_LOGO)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = if (currentSegmented == LoginOrRegisterSegmentedButtonPage.Login) "Welcome back" else "Hello !",
                fontSize = if (isMedium) 30.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = Secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_TITLE)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (currentSegmented == LoginOrRegisterSegmentedButtonPage.Login)
                    "Sign in to start a new sale or manage your store"
                else
                    "Create new account and start manage your store",
                fontSize = if (isMedium) 13.sp else 12.sp,
                color = Gray600,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.LoginRegisterScreen.LOGIN_REGISTER_SUB_TITLE)
            )
            Spacer(Modifier.height(20.dp))

            AuthCard(
                currentSegmented = currentSegmented,
                onSegmentedChange = onSegmentedChange,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
private fun AuthCard(
    currentSegmented: LoginOrRegisterSegmentedButtonPage,
    onSegmentedChange: (LoginOrRegisterSegmentedButtonPage) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            LoginOrRegisterSegmentedButton(
                initialValue = currentSegmented,
                onChanged = onSegmentedChange,
            )

            Spacer(Modifier.height(14.dp))

            when (currentSegmented) {
                LoginOrRegisterSegmentedButtonPage.Login -> LoginSide(snackbarHostState)
                LoginOrRegisterSegmentedButtonPage.Register -> RegisterSide(snackbarHostState)
            }
        }
    }
}

@Preview(name = "Compact Phone", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
fun LoginRegisterScreenCompactPreview() {
    CashierSPTheme {
        LoginRegisterScreen(navController = rememberNavController())
    }
}

@Preview(name = "Medium Tablet Portrait", widthDp = 700, heightDp = 1000, showBackground = true)
@Composable
fun LoginRegisterScreenMediumPreview() {
    CashierSPTheme {
        LoginRegisterScreen(navController = rememberNavController())
    }
}

@Preview(name = "Expanded Tablet Landscape", widthDp = 1024, heightDp = 768, showBackground = true)
@Composable
fun LoginRegisterScreenExpandedPreview() {
    CashierSPTheme {
        LoginRegisterScreen(navController = rememberNavController())
    }
}