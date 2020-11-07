package com.gtgt.pokerjacks.ui.wallet.wallet

data class WalletDetailsResponse(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: WalletDetailsInfo
)

data class WalletDetailsInfo(
    var deposits: Double,
    val winnings: Double,
    val bonus: Double,
    val totalIssuedBonus: Double
) {
    val total: Double
        get() = deposits + winnings + bonus
}