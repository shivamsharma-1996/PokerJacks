package com.gtgt.pokerjacks.ui.profile.vrify_email.model

import com.gtgt.pokerjacks.base.BaseModel

data class UploadedDocumentDetails(val info: UploadedDocumentInfo) : BaseModel() {
    data class UploadedDocumentInfo(
        val document_id: String,
        val fileType: String,
        val file1: String,
        val file2: String,
        val status: String,
        val userId: String
    )
}