package com.pos.cashiersp.presentation.util

data class LoginRequest(
    val email: String,
    val password: String,
)

data class SignUpWithEmailAndPasswordRequestBody(
    val email: String,
    val password: String,
    val name: String,
)