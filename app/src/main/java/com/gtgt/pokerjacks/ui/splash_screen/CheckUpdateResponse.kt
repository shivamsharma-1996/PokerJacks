package com.gtgt.pokerjacks.ui.splash_screen

import com.gtgt.pokerjacks.base.BaseModel

data class CheckUpdateResponse(val info: CheckUpdateInfo) : BaseModel()

class CheckUpdateInfo(
    val forceUpdate: Boolean = false,
    val recommendedUpdate: Boolean = false,
    val newVersion: String?="",
    val productUrl: String?="",
    val features: String?=""
)
