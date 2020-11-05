package com.gtgt.pokerjacks.ui.offers.model

import com.gtgt.pokerjacks.extensions.formatDate
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import java.io.Serializable

data class ScratchCardResponse(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: ScratchCardInfo
)

data class ScratchCardInfo(
    val totalAmount: String?,
    val totalScratchCards: List<TotalScratchCards>?
)

data class TotalScratchCards(
    val user_bonus_master_id: String = "",
    val user_id: String = "",
    val username: String = "",
    val email: String = "",
    val mobile: String = "",
    val wallet_type: String = "",
    val bonus_config_id: String = "",
    val bonus_code_id: String = "",
    val bonus_type_id: String = "",
    val bonus_code: String = "",
    val bonus_type: String = "",
    val bonus_name: String = "",
    val bonus_amount: Double = 0.0,
    val issued_amount: Double = 0.0,
    val usable_amount: Double = 0.0,
    val bonus_expired_amount: Double = 0.0,
    val issued_bonus_date: String = "",
    val sub_bonus_type: String = "",
    val is_bonus_expired: Boolean = false,
    val card_type: String = "",
    val card_description1: String = "",
    val card_description2: String = "",
    val max_cashback: String = "",
    val bonus_expiry_start: String = "",
    val bonus_expiry_end: String = "",
    val created_by: String = "",
    val creation_date: String = "",
    val last_updated_date: String = "",
    val last_updated_by: String = "",
    val card_opens_at: String? = "",
    var isFooter: Boolean = false
) : Serializable {
    val formatedCardOpenAt: String?
        get() {
            return if (card_opens_at == "") {
                ""
            } else {
                card_opens_at?.formatDate() ?: ""
            }
        }

    val formattedIssuedAmount: String
        get() = issued_amount.toDecimalFormat()

    val formattedBonusAmount: String
        get() = bonus_amount.toDecimalFormat()
}