package com.pos.cashiersp.use_case

data class UserUseCase(
    val loginRequest: LoginRequest,
    val signUpWithEmailAndPassword: SignUpWithEmailAndPasswordRequest
)
