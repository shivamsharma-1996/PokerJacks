package com.gtgt.pokerjacks.ui.profile.profile.model

data class UserProfileDetails(
    val errorDesc: String = "",
    val errorCode: Int = 0,
    var success: Boolean = false,
    val info: UserProfileDetailsInfo
)

data class UserProfileDetailsInfo(
    val username: String,
    val mobile: String,
    val email: String,
    val isKycDone: Boolean,
    val isEmailVerified: Boolean,
    val isMobileVerified: Boolean,
    val isPanVerified: String,
    val isAddressVerified: String,
    val isUserNameUpdated: Boolean,
    var  user_status: String,
    val unblock_time: String?,
    val profile_pic_path: String? = ""
)