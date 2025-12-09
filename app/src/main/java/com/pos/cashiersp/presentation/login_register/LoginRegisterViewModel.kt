package com.pos.cashiersp.presentation.login_register

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.LoginResponseDto
import com.pos.cashiersp.presentation.login_register.LoginRegisterViewModel.LoginUIEvent.*
import com.pos.cashiersp.presentation.util.InpTextFieldState
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginRegisterViewModel @Inject constructor(val userUseCase: UserUseCase) : ViewModel() {
    private val _state = mutableStateOf(StateStatus())
    val state: State<StateStatus> = _state

    // AutoRedirect
    private val _autoRedirect = MutableSharedFlow<AutoRedirect>()
    val autoRedirect = _autoRedirect.asSharedFlow()

    // LoginSide
    private val _inpLoginEmail = mutableStateOf(InpTextFieldState())
    val inpLoginEmail: State<InpTextFieldState> = _inpLoginEmail
    private val _inpLoginPassword = mutableStateOf(InpTextFieldState())
    val inpLoginPassword: State<InpTextFieldState> = _inpLoginPassword
    private val _loginUIEvent = MutableSharedFlow<LoginUIEvent>()
    val loginUIEvent = _loginUIEvent.asSharedFlow()

    // RegisterSide
    private val _inpRegisterEmail = mutableStateOf(InpTextFieldState())
    val inpRegisterEmail: State<InpTextFieldState> = _inpRegisterEmail
    private val _inpRegisterUsername = mutableStateOf(InpTextFieldState())
    val inpRegisterUsername: State<InpTextFieldState> = _inpRegisterUsername
    private val _inpRegisterPassword = mutableStateOf(InpTextFieldState())
    val inpRegisterPassword: State<InpTextFieldState> = _inpRegisterPassword
    private val _inpRegisterPassword2 = mutableStateOf(InpTextFieldState())
    val inpRegisterPassword2: State<InpTextFieldState> = _inpRegisterPassword2
    private val _registerUIEvent = MutableSharedFlow<RegisterUIEvent>()
    val registerUIEvent = _registerUIEvent.asSharedFlow()

    init {
        userUseCase.isLoggedIn().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    println("Reading user datastore...")
                }

                is Resource.Success -> {
                    _autoRedirect.emit(AutoRedirect.IsUserLoggedIn(true))
                    println("User is logged in: ${resource.data}")
                }

                is Resource.Error -> {
                    _autoRedirect.emit(AutoRedirect.IsUserLoggedIn(false))
                    println("User is not logged in")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: LoginRegisterEvent) {
        when (event) {
            is LoginRegisterEvent.Login -> {
                if (_state.value.isLoading) return
                val email = _inpLoginEmail.value.text
                val password = _inpLoginPassword.value.text
                userUseCase.loginRequest(email, password).onEach { resource: Resource<LoginResponseDto> ->
                    when (resource) {
                        is Resource.Loading -> _state.value = StateStatus(isLoading = true)

                        // If the flow success then, should be redirect
                        // _state.value = StateStatus()
                        is Resource.Success -> _loginUIEvent.emit(NavigateToCashier)

                        is Resource.Error -> {
                            _state.value = StateStatus(isLoading = false)
                            _loginUIEvent.emit(ShowError(resource.message!!))
                            println(resource.message)
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is LoginRegisterEvent.Register -> {
                if (_state.value.isLoading) return
                val email = _inpRegisterEmail.value.text
                val password = _inpRegisterPassword.value.text
                val username = _inpRegisterUsername.value.text
                userUseCase.signUpWithEmailAndPassword(email, password, username).onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.value = StateStatus(isLoading = true)

                        is Resource.Success -> _registerUIEvent.emit(RegisterUIEvent.NavigateToCashier)

                        is Resource.Error -> {
                            _state.value = StateStatus(isLoading = false)
                            _loginUIEvent.emit(ShowError(resource.message!!))
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is LoginRegisterEvent.EnteredEmailLoginInpField -> {
                val email = event.value
                val isError = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                _inpLoginEmail.value = _inpLoginEmail.value.copy(
                    text = email,
                    isError = isError,
                )
            }

            is LoginRegisterEvent.EnteredPasswordLoginInpField -> {
                val password = event.value
                val isError = password.isNotEmpty() && password.length <= 6
                _inpLoginPassword.value = _inpLoginPassword.value.copy(
                    text = password,
                    isError = isError
                )
            }

            is LoginRegisterEvent.EnteredEmailRegisterInpField -> {
                val email = event.value
                val isError = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                _inpRegisterEmail.value = _inpRegisterEmail.value.copy(
                    text = email,
                    isError = isError,
                )
            }

            is LoginRegisterEvent.EnteredPasswordRegisterInpField -> {
                val password = event.value
                val isError = password.isNotEmpty() && password.length <= 8
                _inpRegisterPassword.value = _inpRegisterPassword.value.copy(
                    text = password,
                    isError = isError
                )
            }

            is LoginRegisterEvent.EnteredPassword2RegisterInpField -> {
                val password2 = event.value
                val isError = password2.isNotEmpty() && _inpRegisterPassword.value.text != password2
                _inpRegisterPassword2.value = _inpRegisterPassword2.value.copy(
                    text = password2,
                    isError = isError
                )
            }

            is LoginRegisterEvent.EnteredUsernameRegisterInpField -> {
                val username = event.value
                val isError = username.isNotEmpty() && username.length <= 1
                _inpRegisterUsername.value = _inpRegisterUsername.value.copy(
                    text = username,
                    isError = isError
                )
            }
        }
    }

    sealed class LoginUIEvent {
        data object NavigateToCashier : LoginUIEvent()
        data class ShowError(val message: String) : LoginUIEvent()
    }

    sealed class RegisterUIEvent {
        data object NavigateToCashier : RegisterUIEvent()
        data class ShowError(val message: String) : RegisterUIEvent()
    }

    sealed class AutoRedirect {
        data class IsUserLoggedIn(val boolean: Boolean) : AutoRedirect()
    }
}