package com.gtgt.pokerjacks.ui.profile.profile.model

data class UploadProfilePicResponse(
    val errorDesc: String = "",
    val errorCode: Int = 0,
    var success: Boolean = false,
    val info: UploadProfilePicInfo
)

data class UploadProfilePicInfo(
    val picPath: String
)