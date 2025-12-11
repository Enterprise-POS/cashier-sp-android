package com.pos.cashiersp.presentation.util

import com.google.gson.annotations.SerializedName

data class LoginRequestBody(
    val email: String,
    val password: String,
)

data class SignUpWithEmailAndPasswordRequestBody(
    val email: String,
    val password: String,
    val name: String,
)

data class NewTenantRequestBody(
    val name: String,

    @SerializedName("owner_user_id")
    val ownerUserId: Int
)