package com.pos.cashiersp.model.dto


data class SignUpResponseDto(
    val token: String,
    val user: User
)