package com.gtgt.pokerjacks.ui.profile.vrify_email.model

data class UpdateEmailResponse(
    val success: Boolean,
    val errorCode: Int,
    val description: String
)