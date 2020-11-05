package com.gtgt.pokerjacks.ui.offers.model

data class OpenScratchCardResponse(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: TotalScratchCards?
)