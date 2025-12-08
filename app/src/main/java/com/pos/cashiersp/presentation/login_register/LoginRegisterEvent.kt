package com.pos.cashiersp.presentation.login_register

sealed class LoginRegisterEvent {
    data class Login(val email: String, val password: String) : LoginRegisterEvent()
    data class Register(val email: String, val password: String) : LoginRegisterEvent()
}