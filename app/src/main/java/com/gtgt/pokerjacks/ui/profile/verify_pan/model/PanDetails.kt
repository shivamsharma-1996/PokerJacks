package com.gtgt.pokerjacks.ui.profile.verify_pan.model

import com.gtgt.pokerjacks.base.BaseModel

enum class PANErrorCodes(val code: Int) {
    VERIFICATION_SUCCESSFULLY(1),
    NOT_MATCHED(2),
    IMG_NOT_CLEAR(3),
    SAVED_SUCCESSFULLY(4),
    ASSIGNED_TO_OTHERS(6),
    CHOOSE_PAN(5),
}

//SAVED_SUCCESSFULLY,
data class PanDetails(val info: PanDetailsInfo?) : BaseModel() {
    data class PanDetailsInfo(
        val pan_id: String,
        val user_name: String,
        val pan_num: String,
        val dob: String,
        var pan_status: String,
        val pan_pic_path: String,
        val comments: String,
        val pan_name: String = ""
    )
}