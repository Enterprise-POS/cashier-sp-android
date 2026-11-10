package com.pos.cashiersp.model.dto

import com.google.common.truth.Truth.assertThat
import com.pos.cashiersp.model.room_entity.toDBEntity
import org.junit.Test

class VoucherDtoTest {
    private val voucher = VoucherDto(
        id = 1,
        code = "SAVE",
        name = "Discount",
        tenantId = 2,
        voucherType = VoucherType.FROM_TOTAL_LIMITED_PERCENTAGE,
        usedCount = 0,
        usageLimit = 0,
        percentage = 10,
        maximumDecrement = 5000,
        roundingPolicy = "ROUND",
        decrement = 0,
        itemIdCombinations = null,
        itemQuantities = 0,
    )

    @Test
    fun `missing item combinations map to an empty domain list`() {
        assertThat(voucher.toDomain().itemIdCombinations).isEmpty()
    }

    @Test
    fun `missing item combinations map to an empty cached list`() {
        assertThat(voucher.toDBEntity(tenantId = 2, storeId = 3).itemIdCombinations).isEmpty()
    }
}
