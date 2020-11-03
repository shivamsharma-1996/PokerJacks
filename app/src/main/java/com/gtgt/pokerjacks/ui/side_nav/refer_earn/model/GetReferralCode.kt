package com.gtgt.pokerjacks.ui.side_nav.refer_earn.model

data class GetReferralCode(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: ReferralCodeInfo
)

data class ReferralCodeInfo(
    val code: String
)