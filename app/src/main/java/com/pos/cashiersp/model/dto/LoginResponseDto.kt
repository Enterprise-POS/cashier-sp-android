package com.pos.cashiersp.model.dto


data class LoginResponseDto(
    val token: String,
    val user: User
)