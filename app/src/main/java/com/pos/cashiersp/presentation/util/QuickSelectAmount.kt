package com.pos.cashiersp.presentation.util

enum class QuickSelectAmount(val uiLabel: String) {
    TEN_THOUNDSAND_RUPIAH(10000.toRupiah()),
    TWENTY_THOUNDSAND_RUPIAH(20000.toRupiah()),
    FIFTY_THOUNDSAND_RUPIAH(50000.toRupiah()),
    HUNDRED_THOUNDSAND_RUPIAH(100000.toRupiah()),
    EXACT("exact"),
}
