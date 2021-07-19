package com.gtgt.pokerjacks.ui.game.models

enum class PotType(val type: String) {
    GAME_POT("game_pot"),
    REFUND("refund")
}

data class Leaderboard(
    val cards_reveal: Boolean,
    val pot_distributions: List<PotDistribution>,
    val pot_winnings: List<PotWinning>,
    val users_best_hand: List<UsersBestHand>,
    val users_winnings: List<UsersWinning>,
    val community_cards: DealCommunityCards,
    val pot_refunds: List<potRefunds>
)


data class potRefunds(
    val user_unique_id: String,
    val wonAmt: Double,
    val pot_index: Int,
    val rank: Int
)
data class PotDistribution(
    val players: List<String>,
    val pot_value: Int,
    val pot_type:String
)

data class PotWinning(
    val pot_index: Int,
    val rank: Int,
    val user_unique_id: String,
    val wonAmt: Int
)

data class UsersBestHand(
    val best_hand_details: BestHandDetails,
    val card_1: String,
    val card_2: String,
    val seat_no: Int,
    val user_unique_id: String
)

data class UsersWinning(
    val user_unique_id: String,
    val wonAmt: Int
)

data class BestHandDetails(
    val cards: List<String>,
    val rankOrder: String,
    val hideCards: Boolean
)