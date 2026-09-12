package com.pos.cashiersp.use_case

data class OrderItemUseCase(
    val transaction: Transactions,
    val searchTransactions: SearchTransactions,
    val findTransactionsById: FindTransactionsById,
    val checkPaymentStatus: CheckPaymentStatus,
    val cancelTransaction: CancelTransaction,
)
