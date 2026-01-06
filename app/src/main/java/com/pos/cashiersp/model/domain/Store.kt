package com.pos.cashiersp.model.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.util.Date


data class Store(
    val id: Int,
    val isActive: Boolean,
    val name: String,
    val tenantId: Int,
    val createdAt: Date,
)

@RequiresApi(Build.VERSION_CODES.O)
fun Store.toDTO() = com.pos.cashiersp.model.dto.Store(
    id = this.id,
    isActive = this.isActive,
    name = this.name,
    tenantId = this.tenantId,
    createdAt = this.createdAt,
)
