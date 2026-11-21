package com.pos.cashiersp.presentation.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ParseDateStringTest {
    // 2026-03-31T08:48:47Z
    private val baseEpochMs = 1774946927000L

    @Test
    fun `parses every timestamp shape the API sends`() {
        assertThat(parseDateString("2026-03-31T08:48:47.179Z").timeInMillis).isEqualTo(baseEpochMs + 179)
        assertThat(parseDateString("2026-03-31T17:48:47.179+09:00").timeInMillis).isEqualTo(baseEpochMs + 179)
        assertThat(parseDateString("2026-03-31T08:48:47.179418Z").timeInMillis).isEqualTo(baseEpochMs + 179)
    }

    @Test
    fun `parses timestamps without fractional seconds`() {
        // expense_date is stored from epoch seconds, so the API sends no fraction
        assertThat(parseDateString("2026-03-31T15:48:47+07:00").timeInMillis).isEqualTo(baseEpochMs)
        assertThat(parseDateString("2026-03-31T08:48:47Z").timeInMillis).isEqualTo(baseEpochMs)
        assertThat(parseDateString("2026-03-31T06:48:47-02:00").timeInMillis).isEqualTo(baseEpochMs)
    }
}
