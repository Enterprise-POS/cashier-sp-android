package com.pos.cashiersp.presentation.login_register

sealed class LoginRegisterEvent {
    // Api related
    object Login : LoginRegisterEvent()
    object Register : LoginRegisterEvent()

    // Login Side
    data class EnteredEmailLoginInpField(val value: String) : LoginRegisterEvent()
    data class EnteredPasswordLoginInpField(val value: String) : LoginRegisterEvent()

    // Register Side
    data class EnteredEmailRegisterInpField(val value: String) : LoginRegisterEvent()
    data class EnteredUsernameRegisterInpField(val value: String) : LoginRegisterEvent()
    data class EnteredPasswordRegisterInpField(val value: String) : LoginRegisterEvent()
    data class EnteredPassword2RegisterInpField(val value: String) : LoginRegisterEvent()
}