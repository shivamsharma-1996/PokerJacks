package com.gtgt.pokerjacks.ui.profile.profile.model

data class GetDepositeLimit(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: GetDepositeLimitInfo
)

data class GetDepositeLimitInfo(
    val limit: Double,
    val deposits: Double,
    val minLimit: Double,
    val maxLimit: Double,
    val renewalDate: String
)
