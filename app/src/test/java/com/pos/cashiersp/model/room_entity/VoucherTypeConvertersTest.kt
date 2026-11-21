package com.pos.cashiersp.model.room_entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VoucherTypeConvertersTest {
    private val converters = VoucherTypeConverters()

    @Test
    fun `item combinations survive Room conversion including duplicates`() {
        val itemIds = listOf(9299, 9299, 9296)

        assertThat(converters.toItemIdCombinations(converters.fromItemIdCombinations(itemIds)))
            .isEqualTo(itemIds)
    }

    @Test
    fun `empty item combinations survive Room conversion`() {
        assertThat(converters.toItemIdCombinations(converters.fromItemIdCombinations(emptyList())))
            .isEmpty()
    }
}
