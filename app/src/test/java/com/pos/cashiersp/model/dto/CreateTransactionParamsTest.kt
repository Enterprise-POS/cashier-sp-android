package com.pos.cashiersp.model.dto

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.pos.cashiersp.presentation.util.PaymentMethod
import org.junit.Test

class CreateTransactionParamsTest {
    @Test
    fun `applied voucher codes serialize in transaction request`() {
        val params = CreateTransactionParams(
            items = emptyList(),
            purchasedPrice = 20_000,
            totalQuantity = 1,
            totalAmount = 19_000,
            discountAmount = 1_000,
            subTotal = 20_000,
            userId = 167,
            tenantId = 333,
            storeId = 141,
            paymentMethod = PaymentMethod.CASH,
            applyVouchers = listOf("FZ10"),
        )

        val requestBody = JsonParser.parseString(Gson().toJson(params)).asJsonObject

        assertThat(requestBody.getAsJsonArray("apply_vouchers").map { it.asString })
            .containsExactly("FZ10")
    }
}
