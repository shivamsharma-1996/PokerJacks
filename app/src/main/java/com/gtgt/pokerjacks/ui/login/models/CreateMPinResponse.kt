package com.gtgt.pokerjacks.ui.login.models

import com.gtgt.pokerjacks.base.BaseModel

data class CreateMPinResponse(
    val info: CreateMPinInfo
): BaseModel()

data class CreateMPinInfo(
    val token: String,
    val user_unique_id: String
)