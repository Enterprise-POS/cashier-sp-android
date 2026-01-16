package com.pos.cashiersp.presentation.select_tenant

import com.pos.cashiersp.model.dto.Tenant

sealed class SelectTenantEvent {
    data class OnClickTenantCard(val tenant: Tenant) : SelectTenantEvent()
    object OnClickSwitchAccount : SelectTenantEvent()
    data class SetOpenTryAgainDialog(val isOpen: Boolean) : SelectTenantEvent()
    data class SetOpenErrorSelectTenantDialog(val isOpen: Boolean) : SelectTenantEvent()
    object FetchTenantList : SelectTenantEvent()
    data class SetOpenAddNewTenant(val isOpen: Boolean) : SelectTenantEvent()
    data class EnteredTenantNameInp(val value: String) : SelectTenantEvent()
    object NewTenantRequest : SelectTenantEvent()
    object OnClickContinue : SelectTenantEvent()
}