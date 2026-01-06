package com.pos.cashiersp.presentation.cashier

import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.presentation.util.PaymentMethod

sealed class CashierEvent {
    data class OnSelectCategory(val categoryId: Int) : CashierEvent()
    data class OnAddToCart(val cashierItem: CashierItem) : CashierEvent()
    data class OnAddQuantity(val cashierItem: CashierItem, val quantity: Int) : CashierEvent()
    data class OnDecreaseQuantity(val cashierItem: CashierItem, val quantity: Int) : CashierEvent()
    data class OnRemoveFromCart(val cashierItem: CashierItem) : CashierEvent()

    data class OnSelectPaymentMethod(val paymentMethod: PaymentMethod) : CashierEvent()

    data class EnteredCashBalance(val value: String) : CashierEvent()
    object OnConfirmTransactionBtnDialog : CashierEvent()
    object PlaceOrder : CashierEvent()
}