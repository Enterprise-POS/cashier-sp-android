package com.pos.cashiersp.model.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String,
    val id: Int,
    val name: String,
    @SerialName("created_at")
    val createdAt: String,
)