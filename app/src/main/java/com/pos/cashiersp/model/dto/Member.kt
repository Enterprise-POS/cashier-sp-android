package com.pos.cashiersp.model.dto

import com.google.gson.annotations.SerializedName

data class Member(
    val email: String,
    val id: Int,
    val name: String,
    @SerializedName("created_at") val createdAt: String,
)