package com.gtgt.pokerjacks.ui.login.models

import com.gtgt.pokerjacks.base.BaseModel

data class CreateUserByMobileResponse(
    val info: CreateUserInfo
): BaseModel()

data class CreateUserInfo(
    val user_unique_id: String
)