package com.pos.cashiersp.presentation.login_register.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.pos.cashiersp.presentation.ui.theme.Gray200
import com.pos.cashiersp.presentation.ui.theme.Primary
import com.pos.cashiersp.presentation.ui.theme.Primary100
import com.pos.cashiersp.presentation.ui.theme.Primary500
import com.pos.cashiersp.presentation.ui.theme.Secondary
import com.pos.cashiersp.presentation.ui.theme.White

@Composable
fun RegisterSide(snackbarHostState: SnackbarHostState, viewModel: LoginRegisterViewModel = hiltViewModel()) {
    val viewModelState = viewModel.state.value
    val email = viewModel.inpRegisterEmail.value
    val username = viewModel.inpRegisterUsername.value
    val password = viewModel.inpRegisterPassword.value
    val password2 = viewModel.inpRegisterPassword2.value

    Text("Email", fontSize = 14.sp, color = Secondary)
    TextField(
        value = email.text,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = email.isError,
        onValueChange = { viewModel.onEvent(LoginRegisterEvent.EnteredEmailRegisterInpField(it)) },
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
            .testTag(TestTags.LoginRegisterScreen.REGISTER_EMAIL)
    )
    Spacer(Modifier.height(12.dp))

    Text("Username", fontSize = 14.sp, color = Secondary)
    TextField(
        value = username.text,
        onValueChange = { viewModel.onEvent(LoginRegisterEvent.EnteredUsernameRegisterInpField(it)) },
        placeholder = {
            Text("John Doe", color = Gray200)
        },
        isError = username.isError,
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
            .testTag(TestTags.LoginRegisterScreen.REGISTER_USERNAME)
    )

    Text("Password", fontSize = 14.sp, color = Secondary)
    TextField(
        value = password.text,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = { viewModel.onEvent(LoginRegisterEvent.EnteredPasswordRegisterInpField(it)) },
        enabled = !viewModelState.isLoading,
        isError = password.isError,
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
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Primary100,
            disabledIndicatorColor = Primary500
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.LoginRegisterScreen.REGISTER_PASSWORD)
    )

    Text("Confirm Password", fontSize = 14.sp, color = Secondary)
    TextField(
        value = password2.text,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        enabled = !viewModelState.isLoading,
        isError = password2.isError,
        onValueChange = { viewModel.onEvent(LoginRegisterEvent.EnteredPassword2RegisterInpField(it)) },
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
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Primary100,
            disabledIndicatorColor = Primary500
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.LoginRegisterScreen.REGISTER_PASSWORD2)
    )

    Spacer(Modifier.height(12.dp))
    TextButton(
        onClick = { viewModel.onEvent(LoginRegisterEvent.Register) },
        colors = ButtonDefaults.textButtonColors(
            containerColor = Primary,
            contentColor = White
        ),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag(TestTags.LoginRegisterScreen.REGISTER_BUTTON)
    ) {
        if (viewModelState.isLoading)
            CircularProgressIndicator(color = Primary, modifier = Modifier.size(18.dp))
        else
            Text("Register")
    }
}