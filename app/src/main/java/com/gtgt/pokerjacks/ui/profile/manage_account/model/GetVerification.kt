package com.gtgt.pokerjacks.ui.profile.manage_account.model

import com.gtgt.pokerjacks.base.BaseModel

data class GetVerification(
    val info: GetVerificationInfo?
) : BaseModel()

data class GetVerificationInfo(
    val bankId: String,
    val Username: String?,
    val Status: String
)