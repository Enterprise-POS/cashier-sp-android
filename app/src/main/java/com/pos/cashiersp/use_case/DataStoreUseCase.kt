package com.pos.cashiersp.use_case

data class DataStoreUseCase(
    val saveSelectedTenant: SaveSelectedTenant,
    val getCurrentTenant: GetCurrentSelectedTenant,
    val unselectTenant: UnselectTenant,

    val saveSelectedStore: SaveSelectedStore,
    val getCurrentStore: GetCurrentSelectedStore,
    val unselectStore: UnselectStore,
)