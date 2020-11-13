package com.gtgt.pokerjacks.ui.login.models

import com.gtgt.pokerjacks.base.BaseModel

data class LoginResponse(
    val info: LoginInfo
): BaseModel()

data class LoginInfo(
    val token: String
)