package com.gtgt.pokerjacks.ui.profile.vrify_email.model

data class VerifyEmailResponse(
    val success: Boolean,
    val errorCode: Int,
    val description: String,
    val info: VerifyEmailInfo
)

data class VerifyEmailInfo(
    val email: String
)