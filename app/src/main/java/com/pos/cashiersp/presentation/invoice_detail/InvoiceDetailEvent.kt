package com.pos.cashiersp.presentation.invoice_detail

sealed class InvoiceDetailEvent {
    object OnClickDismissGeneralDialogStatusBtn : InvoiceDetailEvent()
    object OnClickPrintReceiptBtn : InvoiceDetailEvent()
    object OnClickBackToTransactionHistoryBtn : InvoiceDetailEvent()
}