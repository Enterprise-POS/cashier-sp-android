package com.pos.cashiersp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.cashier.CashierViewModel.UIEvent
import com.pos.cashiersp.presentation.login_register.LoginRegisterViewModel.AutoRedirect
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val dataStoreUseCase: DataStoreUseCase,
) : ViewModel() {
    private val _splashUIEvent = MutableSharedFlow<SplashUIEvent>(replay = 1)
    val splashUIEvent = _splashUIEvent.asSharedFlow()

    init {
        userUseCase.isLoggedIn().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // println("Reading user datastore...")
                    _splashUIEvent.emit(SplashUIEvent.Loading)
                }

                is Resource.Success -> loadStoreAndTenantData()

                is Resource.Error -> {
                    _splashUIEvent.emit(SplashUIEvent.NavigateToLoginRegister)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStoreAndTenantData() {
        combine(
            dataStoreUseCase.getCurrentTenant(),
            dataStoreUseCase.getCurrentStore()
        ) { tenant, store -> Pair(tenant, store) }
            .onEach { (tenantResource, storeResource) ->
                when {
                    tenantResource is Resource.Success && storeResource is Resource.Success -> {
                        _splashUIEvent.emit(SplashUIEvent.NavigateToCashier)
                    }

                    tenantResource is Resource.Error || storeResource is Resource.Error -> {
                        _splashUIEvent.emit(SplashUIEvent.NavigateToSelectTenant)
                    }

                    else -> { /* Loading — do nothing */
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    sealed class SplashUIEvent {
        data object NavigateToCashier : SplashUIEvent()
        data object NavigateToSelectTenant : SplashUIEvent()
        data object NavigateToLoginRegister : SplashUIEvent()
        data object Loading : SplashUIEvent()
    }
}