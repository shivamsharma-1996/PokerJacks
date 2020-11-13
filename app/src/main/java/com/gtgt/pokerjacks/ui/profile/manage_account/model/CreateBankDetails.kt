package com.gtgt.pokerjacks.ui.profile.manage_account.model

import com.gtgt.pokerjacks.base.BaseModel

data class CreateBankDetails(
    val info: CreateBankDetailsInfo?
): BaseModel()

data class CreateBankDetailsInfo(
    val detailId: String,
    val bankAccountNo: String,
    val autoWithdrawl: Boolean,
    val deleted: Boolean,
    val ifscCode: String,
    val upi: String,
    val mobile: String,
    val bank_name: String,
    val branch: String,
    val nameAsPerBank: String,
    val status: String,
    val createdBy: String,
    val updatedBy: String,
    val deletedBy: String,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String,
    val userId: String
)