package com.gtgt.pokerjacks.ui.profile.manage_account.model

data class BankFromIFSC(
    val success: Boolean,
    val errorCode: Int,
    val description: String,
    val info: BankFromIFSCInfo
)

data class BankFromIFSCInfo(
    val BankName: String,
    val City: String,
    val State: String
)