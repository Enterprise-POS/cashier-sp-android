package com.pos.cashiersp.presentation.stock_management

import com.pos.cashiersp.model.dto.StoreStockV2
import com.pos.cashiersp.presentation.cashier.CashierEvent

sealed class StockManagementEvent {
    object OnTapNextPageButton : StockManagementEvent()
    object OnTapPrevPageButton : StockManagementEvent()
    data class OnTapPaginationPageButton(val toPage: Int) : StockManagementEvent()

    object OnRefreshItemCatalogButton : StockManagementEvent()

    object OnCloseGeneralDialog : StockManagementEvent()

    data class OnTapViewDetailsDropDown(val selectedItem: StoreStockV2) : StockManagementEvent()
    object OnTapCloseDetailsBottomSheet : StockManagementEvent()

    data class OnSearchProduct(val text: String) : StockManagementEvent()
    object OnClearSearchProduct : StockManagementEvent()
}