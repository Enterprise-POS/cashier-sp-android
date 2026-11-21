package com.pos.cashiersp.presentation.cashier

import com.google.common.truth.Truth.assertThat
import com.pos.cashiersp.model.domain.Voucher
import com.pos.cashiersp.model.dto.VoucherType
import org.junit.Test

class VoucherDiscountTest {
    @Test
    fun `percentage discount honors rounding policy`() {
        assertThat(voucher(roundingPolicy = "FLOOR").discountFor(15)).isEqualTo(1)
        assertThat(voucher(roundingPolicy = "ROUND").discountFor(15)).isEqualTo(2)
        assertThat(voucher(roundingPolicy = "CEIL").discountFor(11)).isEqualTo(2)
    }

    @Test
    fun `percentage discount is capped by maximum decrement`() {
        assertThat(voucher(percentage = 50, maximumDecrement = 20).discountFor(100))
            .isEqualTo(20)
    }

    @Test
    fun `zero maximum decrement means percentage discount is uncapped`() {
        assertThat(voucher(percentage = 10, maximumDecrement = 0).discountFor(100))
            .isEqualTo(10)
    }

    @Test
    fun `unsupported rounding policy and item quantities do not produce discounts`() {
        assertThat(voucher(roundingPolicy = "UNKNOWN").discountFor(100)).isNull()
        assertThat(voucher(voucherType = VoucherType.ITEM_QUANTITIES).discountFor(100)).isNull()
    }

    @Test
    fun `item combination allows extra cart items`() {
        val combinationVoucher = voucher().copy(
            voucherType = VoucherType.ITEM_COMBINATIONS,
            itemIdCombinations = listOf(1, 2)
        )

        assertThat(combinationVoucher.hasRequiredItemCombination(listOf(1, 2))).isTrue()
        assertThat(combinationVoucher.hasRequiredItemCombination(listOf(1, 2, 3))).isTrue()
        assertThat(combinationVoucher.hasRequiredItemCombination(listOf(1, 1, 2))).isTrue()
    }

    @Test
    fun `item combination accepts repeated cart quantities`() {
        val combinationVoucher = voucher().copy(
            voucherType = VoucherType.ITEM_COMBINATIONS,
            itemIdCombinations = listOf(9295, 9295)
        )
        val expandedCartItemIds = listOf(9295) + listOf(9295)

        assertThat(combinationVoucher.hasRequiredItemCombination(expandedCartItemIds)).isTrue()
    }

    @Test
    fun `item combination accepts extra cart items`() {
        val combinationVoucher = voucher().copy(
            voucherType = VoucherType.ITEM_COMBINATIONS,
            itemIdCombinations = listOf(9295, 9296)
        )

        assertThat(
            combinationVoucher.hasRequiredItemCombination(listOf(9295, 9296, 9000))
        ).isTrue()
    }

    @Test
    fun `fixed voucher discounts use their decrement`() {
        assertThat(voucher(voucherType = VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT, decrement = 30)
            .discountFor(100)).isEqualTo(30)
        assertThat(voucher(voucherType = VoucherType.ITEM_COMBINATIONS, decrement = 30)
            .discountFor(100)).isEqualTo(30)
    }

    @Test
    fun `applied vouchers stack against remaining total`() {
        val first = voucher(
            code = "FIRST",
            voucherType = VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT,
            decrement = 30
        )
        val second = voucher(
            code = "SECOND",
            voucherType = VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT,
            decrement = 20
        )

        assertThat(calculateAppliedVoucherDiscount(listOf(first, second), listOf("FIRST", "SECOND"), 100))
            .isEqualTo(50)
    }

    private fun voucher(
        code: String = "SAVE",
        voucherType: VoucherType = VoucherType.FROM_TOTAL_LIMITED_PERCENTAGE,
        percentage: Int = 10,
        maximumDecrement: Int = 5000,
        roundingPolicy: String = "ROUND",
        decrement: Int = 0,
    ) = Voucher(
        id = 1,
        code = code,
        name = "Discount",
        tenantId = 1,
        createdAt = null,
        voucherType = voucherType,
        usedCount = 0,
        usageLimit = 0,
        startAt = null,
        endAt = null,
        percentage = percentage,
        maximumDecrement = maximumDecrement,
        roundingPolicy = roundingPolicy,
        decrement = decrement,
        itemIdCombinations = emptyList(),
        itemQuantities = 0,
    )
}
