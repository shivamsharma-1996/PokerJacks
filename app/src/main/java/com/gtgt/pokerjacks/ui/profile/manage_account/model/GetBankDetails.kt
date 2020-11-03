package com.gtgt.pokerjacks.ui.profile.manage_account.model

import com.gtgt.pokerjacks.base.BaseModel
import java.io.Serializable

data class GetBankDetails(
    val info: List<GetBankDetailsInfo>?
) : BaseModel()

data class GetBankDetailsInfo(
    val detailId: String,
    val bankAccountNo: String?,
    val ifscCode: String,
    val upi: String?,
    val paytm_wallet_number: String?,
    val mobile: String?,
    val bank_name: String? ="",
    val branch: String,
    val nameAsPerBank: String,
    val status: String,
    val orderId: String
) : Serializable {
    val formattedAccNumber: String
        get() {
            return when {
                !bankAccountNo.isNullOrEmpty() -> {
                    "*******" + bankAccountNo.let {
                        it.substring(it.length - 4)
                    }
                }
                !upi.isNullOrEmpty() -> {
                    "*******" + upi.let {
                        it.substring(it.length - 4)
                    }
                }
                else -> {
                    "*******" + paytm_wallet_number?.let {
                        it.substring(it.length - 4)
                    }
                }
            }
        }

    val AccountNameByType: String
        get() {
            return when {
                !bankAccountNo.isNullOrEmpty() -> {
                    "Bank Transfer"
                }
                !upi.isNullOrEmpty() -> {
                    "UPI"
                }
                else -> {
                    "PAYTM"
                }
            }
        }
}

enum class BankStatus(val code: String) {
    INITIATED("Initiated"),
    PENDING("Pending"),
    VERIFIED("Verified"),
    FAILED("Failed"),
    REJECTED("Rejected")
}