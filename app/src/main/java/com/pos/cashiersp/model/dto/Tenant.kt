package com.pos.cashiersp.model.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tenant(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("id")
    val id: Int,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("owner_user_id")
    val ownerUserId: Int
)