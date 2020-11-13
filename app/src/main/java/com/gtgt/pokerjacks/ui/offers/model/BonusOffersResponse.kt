package com.gtgt.pokerjacks.ui.offers.model

import com.gtgt.pokerjacks.extensions.toDecimalFormat
import java.io.Serializable

data class BonusOffersResponse(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: BonusOffersInfo?
): Serializable

data class BonusOffersInfo(
    val bonusCodes: List<BonusCodes>?
): Serializable

data class BonusCodes(
    val bonus_config_id: String,
    val bonus_code_id: String,
    val bonus_type_id: String,
    val bonus_code: String,
    val bonus_type: String,
    val bonus_name: String,
    val sub_bonus_type: String,
    val is_visible: String,
    val user_category: String,
    val bonus_start_date: String,
    val bonus_end_date: String,
    val bonus_percentage: Double,
    val bonus_expiry_duration: String,
    val bonus_expiry_type: String,
    val user_count: String,
    val usage_count: String,
    val min_deposit_amount: Double,
    val max_cashback: Double,
    val total_bonus_amount: String,
    val is_active: String,
    val bonus_description1: String,
    val bonus_description2: String,
    val color_code1: String,
    val color_code2: String,
    val cashback_percent: String,
    val card_type: String,
    val card_opens_at: String,
    val product_name: String,
    val created_by: String,
    val creation_date: String,
    val last_updated_date: String,
    val last_updated_by: String,
    val bonus_terms: String
): Serializable{
    val fomatted_max_cashback: String
    get() = "₹"+max_cashback.toDecimalFormat()
}