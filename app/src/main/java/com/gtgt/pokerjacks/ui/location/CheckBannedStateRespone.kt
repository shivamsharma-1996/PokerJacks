package com.gtgt.pokerjacks.ui.location

data class CheckBannedStateRespone(
    val success: Boolean,
    val errorCode: Int,
    val description: String,
    val info: Boolean
)