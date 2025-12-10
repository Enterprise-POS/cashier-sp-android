package com.pos.cashiersp.presentation.select_tenant

import androidx.lifecycle.ViewModel
import com.pos.cashiersp.use_case.TenantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectTenantViewModel @Inject constructor(tenantUseCase: TenantUseCase) : ViewModel() {
    init {

    }

    fun onEvent() {}
}