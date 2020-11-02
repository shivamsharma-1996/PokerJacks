package com.gtgt.pokerjacks.ui.login.models

data class ResendOTP(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String
)