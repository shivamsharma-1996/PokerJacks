package com.gtgt.pokerjacks.ui.profile.vrify_email.model

import com.gtgt.pokerjacks.base.BaseModel

data class UserDocumentDetails(val info: UserDocumentDetailsInfo) : BaseModel() {
    data class UserDocumentDetailsInfo(
        val document_id: String,
        val pinCode: String,
        val fileType: String,
        val file1: String,
        val file2: String,
        val comments: String,
        val status: String
    )
}