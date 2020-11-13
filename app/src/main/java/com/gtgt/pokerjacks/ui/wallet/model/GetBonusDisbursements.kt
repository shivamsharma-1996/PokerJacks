package com.gtgt.pokerjacks.ui.wallet.model

import com.gtgt.pokerjacks.extensions.formatDate

data class GetBonusDisbursements(
    val success: Boolean,
    val errorCode: Int,
    val description: String,
    val info: List<GetBonusDisbursementsInfo>
)

data class GetBonusDisbursementsInfo(
    val amount: String = "",
    val description: String = "",
    val gameId: String = "",
    val creationDate: String = "",
    val isFooter: Boolean = false,
    val isHeader: Boolean = false
) {
    val formattedDate: String
        get() = creationDate.formatDate()
}