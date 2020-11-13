package com.gtgt.pokerjacks.ui.wallet.model

import com.gtgt.pokerjacks.extensions.getDateFormatForBonus
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import java.text.SimpleDateFormat
import java.util.*

data class GetBonusHistory(
    val success: Boolean,
    val errorCode: Int,
    val description: String,
    val info: GetBonusHistoryInfo
)

data class GetBonusHistoryInfo(
    val totalReceivedAmount: String,
    val totalDisbursedAmount: String,
    val totalRemainingBonus: String,
    val totalExpiredBonus: String,
    val bonusHistory: List<BonusHistory>
)

data class BonusHistory(
    val bonusCode: String = "",
    val bonusType: String = "",
    val utilizedAmount: Double = 0.0,
    val bonusValue: Double = 0.0,
    val pendingAmount: Double = 0.0,
    val expiredBonusAmount: String = "",
    val issuedBonusDate: String = "",
    val bonusExpiryDate: String = "",
    val sourceType: String = "",
    val isLoading: Boolean = false
) {
    val formattedDate: String
        get() = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.US
        ).format(getDateFormatForBonus(issuedBonusDate))

    val formattedBonusValue: String
    get() = "₹${bonusValue.toDecimalFormat()}"

    val formattedUtilizedAmount: String
        get() = "₹${utilizedAmount.toDecimalFormat()}"

    val formattedPendingAmount: String
        get() = "₹${pendingAmount.toDecimalFormat()}"
}