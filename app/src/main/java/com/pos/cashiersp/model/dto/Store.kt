package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Store(
    @SerializedName("created_at")
    val createdAt: Date,
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    val name: String,
    @SerializedName("tenant_id")
    val tenantId: Int
)

fun Store.toDomain() = com.pos.cashiersp.model.domain.Store(
    id = this.id,
    isActive = this.isActive,
    name = this.name,
    tenantId = this.tenantId,
    createdAt = this.createdAt
)