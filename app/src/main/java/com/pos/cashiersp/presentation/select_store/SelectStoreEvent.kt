package com.pos.cashiersp.presentation.select_store

import com.pos.cashiersp.model.domain.Store

sealed class SelectStoreEvent {
    data class OnSelectStoreCard(val selectedStore: Store) : SelectStoreEvent()
    object OnBackButtonClicked : SelectStoreEvent()
    object OnContinueButtonClicked : SelectStoreEvent()
    object OnCloseErrorAlert : SelectStoreEvent()
    object OnTryAgainFetchStoreList : SelectStoreEvent()
}