package com.pos.cashiersp.use_case

data class DatabaseCacheMetadataUseCase(
    val writeMetadata: WriteMetadata,
    val getMetadata: GetMetadata,
    val writeAndReturnMetadata: WriteAndReturnMetadata
)
