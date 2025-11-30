package com.pos.cashiersp.presentation.greeting

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.domain.TenantGetMembers
import com.pos.cashiersp.model.dto.Member
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.TenantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GreetingViewModel @Inject constructor(private val tenantUseCase: TenantUseCase): ViewModel() {
    private val _state = mutableStateOf(StateStatus())
    val state: State<StateStatus> = _state

    private val _members = mutableStateOf(listOf<Member>())
    val members: State<List<Member>> = _members

    private var getTenantMembersJob: Job? = null

    init {
        // This is testing view model. will not displayed to any production
        // Use this file as reference when creating view model
        val tenantId = 1
        getTenantMembersJob?.cancel()
        getTenantMembersJob = tenantUseCase.getTenantMembers(tenantId)
            .onEach { resource: Resource<TenantGetMembers> ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = StateStatus(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = StateStatus() // Restart to idle state
                        _members.value = resource.data!!.members
                    }
                    is Resource.Error -> {
                        _state.value = StateStatus(error = resource.message!!)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent() {
        
    }
}