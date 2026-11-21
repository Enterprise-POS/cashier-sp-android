package com.pos.cashiersp.presentation.cashier

import com.pos.cashiersp.model.domain.Voucher
import com.pos.cashiersp.model.dto.VoucherType
import java.math.BigDecimal
import java.math.RoundingMode

internal fun Voucher.discountFor(total: Long): Long? {
    if (total < 0) return null

    return when (voucherType) {
        VoucherType.FROM_TOTAL_LIMITED_PERCENTAGE -> {
            if (percentage < 1 || maximumDecrement < 0) return null
            val roundingMode = when (roundingPolicy.uppercase()) {
                "FLOOR" -> RoundingMode.FLOOR
                "ROUND" -> RoundingMode.HALF_UP
                "CEIL" -> RoundingMode.CEILING
                else -> return null
            }
            val percentageDiscount = BigDecimal.valueOf(total)
                .multiply(BigDecimal.valueOf(percentage.toLong()))
                .divide(BigDecimal.valueOf(100), 0, roundingMode)
            val maximumDiscount = BigDecimal.valueOf(maximumDecrement.toLong())
            if (maximumDecrement > 0 && percentageDiscount > maximumDiscount) {
                maximumDecrement.toLong()
            } else {
                percentageDiscount.longValueExact()
            }
        }

        VoucherType.FROM_TOTAL_LIMITED_STATIC_DECREMENT,
        VoucherType.ITEM_COMBINATIONS ->
            decrement.takeIf { it > 0 && it.toLong() <= total }?.toLong()

        VoucherType.ITEM_QUANTITIES -> null
    }
}

internal fun calculateAppliedVoucherDiscount(
    vouchers: List<Voucher>,
    codes: List<String>,
    subtotal: Long,
): Long? {
    var remaining = subtotal
    for (code in codes) {
        val voucher = vouchers.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: return null
        val discount = voucher.discountFor(remaining) ?: return null
        if (discount > remaining) return null
        remaining -= discount
    }
    return subtotal - remaining
}

internal fun Voucher.hasRequiredItemCombination(cartItemIds: Collection<Int>): Boolean {
    if (itemIdCombinations.isEmpty()) return false

    val required = itemIdCombinations.groupingBy { it }.eachCount()
    val purchased = cartItemIds.groupingBy { it }.eachCount()

    return required.all { (itemId, requiredQuantity) ->
        (purchased[itemId] ?: 0) >= requiredQuantity
    }
}
