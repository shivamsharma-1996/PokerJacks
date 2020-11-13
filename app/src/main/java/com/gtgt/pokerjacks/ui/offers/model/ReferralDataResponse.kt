package com.gtgt.pokerjacks.ui.offers.model

import com.gtgt.pokerjacks.extensions.toDecimalFormat
import java.io.Serializable

data class ReferralDataResponse(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: List<ReferralDataInfo>
): Serializable

data class ReferralDataInfo(
    val userName: String,
    val mobile: String,
    val amount: Double
): Serializable {
    val amountFormated
        get() = amount.toDecimalFormat()

    /*val maskMobile
    get() = mobile.replace("\\w(?=\\w{3})", "*")*/

    val maskMobile
        get() = "*******" + mobile?.let {
            it.substring(it.length - 3)
        }
}