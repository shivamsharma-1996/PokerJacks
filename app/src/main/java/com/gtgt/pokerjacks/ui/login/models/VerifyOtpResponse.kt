package com.gtgt.pokerjacks.ui.login.models

import com.gtgt.pokerjacks.base.BaseModel

data class VerifyOtpResponse(
    val info: VerifyOTPInfo
): BaseModel()

data class VerifyOTPInfo(
    val setMpin: Boolean,
    val user_unique_id: String
)