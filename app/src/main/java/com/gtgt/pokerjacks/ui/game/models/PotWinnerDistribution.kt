package com.gtgt.pokerjacks.ui.game.models


enum class PotDistributionType(val event: String) {
    WINNER("winner"),
    REFUND("refund"),
}

data class PotWinnerDistribution(val user_unique_id: String, val wonAmt: Double, val type: PotDistributionType)