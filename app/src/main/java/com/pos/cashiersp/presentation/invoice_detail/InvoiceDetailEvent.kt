package com.pos.cashiersp.presentation.invoice_detail

sealed class InvoiceDetailEvent {
    object OnClickDismissGeneralDialogStatusBtn : InvoiceDetailEvent()
    object OnClickPrintReceiptBtn : InvoiceDetailEvent()
    object OnClickBackToTransactionHistoryBtn : InvoiceDetailEvent()

    object OnClickCheckTransaction : InvoiceDetailEvent()
    object OnDismissPaymentDialog : InvoiceDetailEvent()
    object OnClickDismissPaymentGatewayDialogBtn : InvoiceDetailEvent()
}