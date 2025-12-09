package com.pos.cashiersp.presentation.login_register.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pos.cashiersp.common.TestTags
import com.pos.cashiersp.presentation.login_register.LoginRegisterEvent
import com.pos.cashiersp.presentation.login_register.LoginRegisterViewModel
import com.pos.cashiersp.presentation.ui.theme.Danger900
import com.pos.cashiersp.presentation.ui.theme.Gray100
import com.pos.cashiersp.presentation.ui.theme.Gray200
import com.pos.cashiersp.presentation.ui.theme.Gray300
import com.pos.cashiersp.presentation.ui.theme.Gray600
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary500
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.Secondary600
import com.pos.cashiersp.presentation.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun LoginSide(snackbarHostState: SnackbarHostState, viewModel: LoginRegisterViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val viewModelState = viewModel.state.value
    val emailInpState = viewModel.inpLoginEmail.value
    val passwordInpState = viewModel.inpLoginPassword.value

    Text("Email", fontSize = 14.sp, color = Secondary)
    TextField(
        value = emailInpState.text,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = emailInpState.isError,
        enabled = !viewModelState.isLoading,
        onValueChange = { viewModel.onEvent(LoginRegisterEvent.EnteredEmailLoginInpField(it)) },
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
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Primary100,
            disabledIndicatorColor = Primary500
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.LoginRegisterScreen.LOGIN_EMAIL_INPUT)
    )
    Spacer(Modifier.height(12.dp))

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Password", fontSize = 14.sp, color = Secondary)
        Text("Registered account password", fontSize = 14.sp, color = Gray600)
    }
    TextField(
        value = passwordInpState.text,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = { viewModel.onEvent(LoginRegisterEvent.EnteredPasswordLoginInpField(it)) },
        placeholder = {
            Text("●●●●●●", color = Gray200)
        },
        isError = passwordInpState.isError,
        enabled = !viewModelState.isLoading,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            cursorColor = Primary,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Primary,
            errorIndicatorColor = Danger900,
            errorCursorColor = Danger900,
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Primary100,
            disabledIndicatorColor = Primary500
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.LoginRegisterScreen.LOGIN_PASSWORD_INPUT),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        var checked by remember { mutableStateOf(true) }
        Switch(
            checked = checked,
            enabled = !viewModelState.isLoading,
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
        onClick = { viewModel.onEvent(LoginRegisterEvent.Login) },
        colors = ButtonDefaults.textButtonColors(
            containerColor = Primary,
            contentColor = White
        ),
        enabled = !viewModelState.isLoading,
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag(TestTags.LoginRegisterScreen.CONTINUE_BUTTON)
    ) {
        if (viewModelState.isLoading) {
            CircularProgressIndicator(color = Primary, modifier = Modifier.size(18.dp))
        } else {
            Text("Continue")
        }
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