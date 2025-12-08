package com.pos.cashiersp.presentation.login_register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.LoginResponseDto
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginRegisterViewModel @Inject constructor(val userUseCase: UserUseCase) : ViewModel() {
    private val _state = mutableStateOf(StateStatus())
    val state: State<StateStatus> = _state

    fun onEvent(event: LoginRegisterEvent) {
        when (event) {
            is LoginRegisterEvent.Login -> {
                if (_state.value.isLoading) return
                val email = event.email
                val password = event.password
                userUseCase.loginRequest(email, password).onEach { resource: Resource<LoginResponseDto> ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.value = StateStatus(isLoading = true)
                        }

                        is Resource.Success -> {
                            _state.value = StateStatus()
                            println(resource.data)
                            // Should be redirect to cashier page
                        }

                        is Resource.Error -> {
                            _state.value = StateStatus(isLoading = false, error = resource.message)
                            println(resource.message)
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is LoginRegisterEvent.Register -> {
                TODO()
            }
        }
    }
}