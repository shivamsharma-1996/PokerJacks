package com.gtgt.pokerjacks.ui.profile.verify_pan.model

data class UploadedPanDetailsInfo(
    val autoVerified: Boolean,
    val code: Int,
    val message: String,
    val Name: String,
    val Number: String,
    val DOB: String
)