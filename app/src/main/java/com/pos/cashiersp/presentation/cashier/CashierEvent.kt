package com.pos.cashiersp.presentation.cashier

import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.model.domain.Voucher
import com.pos.cashiersp.model.dto.VoucherType
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryEvent
import com.pos.cashiersp.presentation.util.KeypadNumberElement
import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.QuickSelectAmount

sealed class CashierEvent {
    object TryAgainRequestAllProducts : CashierEvent()
    object OnDismissTryAgainRequestAllProducts : CashierEvent()

    data class OnSearchProduct(val text: String) : CashierEvent()
    object OnClearSearchProduct : CashierEvent()

    data class OnSelectCategory(val categoryId: Int) : CashierEvent()
    data class OnAddToCart(val cashierItem: CashierItem) : CashierEvent()
    data class OnAddQuantity(val cashierItem: CashierItem, val quantity: Int) : CashierEvent()
    data class OnDecreaseQuantity(val cashierItem: CashierItem, val quantity: Int) : CashierEvent()
    data class OnRemoveFromCart(val cashierItem: CashierItem) : CashierEvent()

    data class OnSelectPaymentMethod(val paymentMethod: PaymentMethod) : CashierEvent()
    data class OnQuickSelectAmount(val quickSelectAmount: QuickSelectAmount) : CashierEvent()

    data class EnteredCashBalance(val value: String) : CashierEvent()
    object OnConfirmTransactionBtnDialog : CashierEvent()

    object OnConfirmGeneralAlertDialog : CashierEvent()

    object PlaceOrder : CashierEvent()

    object OnPressPrintReceipt : CashierEvent()

    object RefreshCashierItem : CashierEvent()

    data class OnToggleInfoBtn(val activate: Boolean?) : CashierEvent()

    object OnDeleteAllCartItem : CashierEvent()

    object OnToggleCategoriesExpanded : CashierEvent()

    object OnOpenVoucherPicker : CashierEvent()
    object OnDismissVoucherPicker : CashierEvent()
    data class OnSelectVoucherCategory(val voucherType: VoucherType?) : CashierEvent()
    data class OnSelectVoucher(val voucher: Voucher) : CashierEvent()
    data class OnVoucherCodeChange(val value: String) : CashierEvent()
    object OnAddVoucher : CashierEvent()
    data class OnRemoveAppliedVoucher(val index: Int) : CashierEvent()

    object OnDismissPaymentGatewayDialog : CashierEvent()

    data class OnKeypadClicked(val keypadNumberElement: KeypadNumberElement) : CashierEvent()
}