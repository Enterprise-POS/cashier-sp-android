package com.pos.cashiersp.presentation.select_tenant

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.Tenant
import com.pos.cashiersp.presentation.util.InpTextFieldState
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.TenantUseCase
import com.pos.cashiersp.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SelectTenantViewModel @Inject constructor(
    private val tenantUseCase: TenantUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val _state = mutableStateOf(StateStatus())
    val state: State<StateStatus> = _state

    private val _tenantList = mutableStateOf<List<Tenant>>(listOf())
    val tenantList: State<List<Tenant>> = _tenantList

    private val _selectedTenant = mutableStateOf<Tenant?>(null)
    val selectedTenant: State<Tenant?> = _selectedTenant

    private val _authorizationUIEvent = MutableSharedFlow<AuthorizationUIEvent>()
    val authorizationUIEvent = _authorizationUIEvent.asSharedFlow()

    private val _openTryAgainDialog = mutableStateOf(false)
    val openTryAgainDialog: State<Boolean> = _openTryAgainDialog

    // Add new tenant dialog
    private val _openAddNewTenantDialog = mutableStateOf(false)
    val openAddNewTenantDialog: State<Boolean> = _openAddNewTenantDialog
    private val _inpNewTenantName = mutableStateOf(InpTextFieldState())
    val inpNewTenantName: State<InpTextFieldState> = _inpNewTenantName
    private val _isCreatingTenant = mutableStateOf(false)
    val isCreatingTenant: State<Boolean> = _isCreatingTenant

    init {
        // Get current user all related tenant
        this.fetchTenantList()
    }

    fun onEvent(event: SelectTenantEvent) {
        when (event) {
            is SelectTenantEvent.OnClickTenantCard -> {
                _selectedTenant.value = event.tenant
            }

            SelectTenantEvent.OnClickSwitchAccount -> {
                userUseCase.logout().onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.value = StateStatus(isLoading = true)
                        }

                        is Resource.Success -> {
                            _state.value = StateStatus()
                            _authorizationUIEvent.emit(AuthorizationUIEvent.Logout)
                        }

                        is Resource.Error -> {
                            _state.value = StateStatus()
                            _authorizationUIEvent.emit(AuthorizationUIEvent.Logout)
                        }
                    }
                }.launchIn(viewModelScope)
            }


            is SelectTenantEvent.FetchTenantList -> {
                // Prevent multiple click
                if (_state.value.isLoading) return

                // Maybe stop the job if in request ?
                this.fetchTenantList()
            }

            is SelectTenantEvent.EnteredTenantNameInp -> {
                val tenantName = event.value
                val isError = tenantName.isNotEmpty() && tenantName.length < 2
                _inpNewTenantName.value = _inpNewTenantName.value.copy(text = event.value, isError = isError)
            }

            is SelectTenantEvent.NewTenantRequest -> {
                if (_isCreatingTenant.value) return
                val inpNewTenantName = _inpNewTenantName.value.text
                tenantUseCase.newTenant(inpNewTenantName).onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> _isCreatingTenant.value = true

                        is Resource.Success -> {
                            _openAddNewTenantDialog.value = false
                            _isCreatingTenant.value = false

                            // Sync tenant data once more
                            this.fetchTenantList()
                        }

                        is Resource.Error -> {
                            println(resource.message)
                            _isCreatingTenant.value = false
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is SelectTenantEvent.SetOpenAddNewTenant -> {
                _openAddNewTenantDialog.value = event.isOpen
                _inpNewTenantName.value = InpTextFieldState()
            }

            is SelectTenantEvent.SetOpenTryAgainDialog -> _openTryAgainDialog.value = event.isOpen
        }
    }

    private fun fetchTenantList(): Job {
        return tenantUseCase.getTenantWithUser().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = StateStatus(isLoading = true)
                    _openTryAgainDialog.value = false
                }

                is Resource.Success -> {
                    _state.value = StateStatus()
                    _tenantList.value = resource.data!!.tenants
                }

                is Resource.Error -> {
                    if (resource.message!!.contains("[UNAUTHORIZED]")) {
                        _state.value = StateStatus()
                        _authorizationUIEvent.emit(AuthorizationUIEvent.Logout)
                    } else {
                        _state.value = StateStatus(error = resource.message)
                        _openTryAgainDialog.value = true
                        println("Error in SelectTenantViewModel when init with message: ${resource.message}")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed class AuthorizationUIEvent {
        object Logout : AuthorizationUIEvent()
    }
}