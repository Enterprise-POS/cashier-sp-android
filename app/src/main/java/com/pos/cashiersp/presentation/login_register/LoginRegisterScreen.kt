package com.pos.cashiersp.presentation.login_register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pos.cashiersp.R
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.Screen
import com.pos.cashiersp.presentation.login_register.component.LoginOrRegisterSegmentedButton
import com.pos.cashiersp.presentation.login_register.component.LoginOrRegisterSegmentedButtonPage
import com.pos.cashiersp.presentation.login_register.component.TextDivider
import com.pos.cashiersp.presentation.ui.theme.Danger900
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray200
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary100
import com.pos.cashiersp.presentation.ui.theme.Secondary600
import com.pos.cashiersp.presentation.ui.theme.White
import com.pos.cashiersp.presentation.util.JwtStore
import kotlinx.coroutines.launch

@Composable
fun LoginRegisterScreen(navController: NavController, viewModel: LoginRegisterViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentSegmented = remember { mutableStateOf(LoginOrRegisterSegmentedButtonPage.Login) }
    val context = LocalContext.current
    val token by JwtStore.getToken(context).collectAsState(initial = null)

    println("token: $token")

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
                        LoginOrRegisterSegmentedButtonPage.Login -> {
                            var email by remember { mutableStateOf("") }
                            val emailHasErrors by remember {
                                derivedStateOf {
                                    if (email.isNotEmpty()) {
                                        // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
                                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                    } else {
                                        false
                                    }
                                }
                            }
                            Text("Email", fontSize = 14.sp, color = Secondary)
                            TextField(
                                value = email,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = emailHasErrors,
                                onValueChange = { email = it },
                                placeholder = {
                                    Text("name@store.com", color = Gray200)
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    cursorColor = Primary,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Primary,
                                    errorIndicatorColor = Danger900,
                                    errorCursorColor = Danger900,
                                    errorContainerColor = Color.Transparent
                                ),
                            )
                            Spacer(Modifier.height(12.dp))

                            var password by remember { mutableStateOf("") }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Password", fontSize = 14.sp, color = Secondary)
                                Text("Registered account password", fontSize = 14.sp, color = Gray600)
                            }
                            TextField(
                                value = password,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                onValueChange = { password = it },
                                placeholder = {
                                    Text("●●●●●●", color = Gray200)
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    cursorColor = Primary,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Primary,
                                    errorIndicatorColor = Danger900,
                                    errorCursorColor = Danger900,
                                    errorContainerColor = Color.Transparent
                                ),
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var checked by remember { mutableStateOf(true) }
                                Switch(
                                    checked = checked,
                                    onCheckedChange = {
                                        checked = it
                                    },
                                    colors = SwitchDefaults.colors(
                                        uncheckedIconColor = Secondary,
                                        uncheckedThumbColor = Secondary,
                                        uncheckedBorderColor = Secondary,
                                        checkedThumbColor = Primary,
                                        checkedBorderColor = Secondary,
                                        checkedTrackColor = Secondary,
                                        uncheckedTrackColor = Secondary600
                                    )
                                )

                                Spacer(Modifier.width(12.dp))
                                Text("Remember this account", fontSize = 14.sp, color = Gray600)
                            }

                            Spacer(Modifier.height(12.dp))
                            TextButton(
                                onClick = {
                                    viewModel.onEvent(LoginRegisterEvent.Login(email, password))
                                    //navController.navigate(Screen.CASHIER)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Primary,
                                    contentColor = White
                                ),
                                shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag(TestTags.LoginRegisterScreen.CONTINUE_BUTTON)
                            ) {
                                Text("Continue")
                            }

                            TextDivider("or")

                            TextButton(
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Currently under development 🙏")
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Primary,
                                    contentColor = White,
                                    disabledContentColor = Gray300,
                                    disabledContainerColor = Gray100
                                ),
                                enabled = false,
                                shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            ) {
                                Text("Sign in with Google")
                            }
                        }

                        LoginOrRegisterSegmentedButtonPage.Register -> {
                            var email by remember { mutableStateOf("") }
                            val emailHasErrors by remember {
                                derivedStateOf {
                                    if (email.isNotEmpty()) {
                                        // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
                                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                    } else {
                                        false
                                    }
                                }
                            }
                            Text("Email", fontSize = 14.sp, color = Secondary)
                            TextField(
                                value = email,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = emailHasErrors,
                                onValueChange = { email = it },
                                placeholder = {
                                    Text("name@store.com", color = Gray200)
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    cursorColor = Primary,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Primary,
                                    errorIndicatorColor = Danger900,
                                    errorCursorColor = Danger900,
                                    errorContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag(TestTags.LoginRegisterScreen.LOGIN_EMAIL_INPUT)
                            )
                            Spacer(Modifier.height(12.dp))

                            var password by remember { mutableStateOf("") }
                            Text("Password", fontSize = 14.sp, color = Secondary)
                            TextField(
                                value = password,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                onValueChange = { password = it },
                                placeholder = {
                                    Text("enter with minimal 6 characters", color = Gray200)
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    cursorColor = Primary,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Primary,
                                    errorIndicatorColor = Danger900,
                                    errorCursorColor = Danger900,
                                    errorContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag(TestTags.LoginRegisterScreen.LOGIN_PASSWORD_INPUT)
                            )

                            var password2 by remember { mutableStateOf("") }
                            Text("Confirm Password", fontSize = 14.sp, color = Secondary)
                            TextField(
                                value = password2,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                onValueChange = { password2 = it },
                                placeholder = {
                                    Text("re enter your password", color = Gray200)
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    cursorColor = Primary,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Primary,
                                    errorIndicatorColor = Danger900,
                                    errorCursorColor = Danger900,
                                    errorContainerColor = Color.Transparent
                                ),
                            )

                            Spacer(Modifier.height(12.dp))
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        JwtStore.saveToken(context, "This is Only test")
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Primary,
                                    contentColor = White
                                ),
                                shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            ) {
                                Text("Register")
                            }
                        }
                    }
                }
            }
        }
    }
}