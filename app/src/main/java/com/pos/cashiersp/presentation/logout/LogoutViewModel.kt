package com.pos.cashiersp.presentation.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
) : ViewModel() {
    private val _authorizationUIEvent = MutableSharedFlow<AuthorizationUIEvent>()
    val authorizationUIEvent = _authorizationUIEvent.asSharedFlow()

    init {
        logout()
    }

    private fun logout() {
        userUseCase.logout().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    _authorizationUIEvent.emit(AuthorizationUIEvent.Logout)
                }

                is Resource.Error -> {
                    _authorizationUIEvent.emit(AuthorizationUIEvent.Logout)
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed class AuthorizationUIEvent {
        object Logout : AuthorizationUIEvent()
    }
}