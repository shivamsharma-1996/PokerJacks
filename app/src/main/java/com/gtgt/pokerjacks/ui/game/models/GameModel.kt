package com.gtgt.pokerjacks.ui.game.models

import com.gtgt.pokerjacks.base.BaseModel

data class TablePlayers(
    val tableSlots: List<TableSlot>,
    val gameUsers: List<GameUser>
)

data class GameModel(val info: Info) : BaseModel() {
    data class Info(
        val gameDetails: GameDetails,
        val tableDetails: TableDetails,
        val tableSlots: List<TableSlot>,
        val userContestDetails: UserContestDetails,
        val gameUsers: List<GameUser>,
        val playerTurn: PlayerTurn?
    )

    data class GameDetails(
        val _id: String,
        val big_blind: Double,
        val big_blind_position: Int,
        val card_1: String,
        val card_2: String,
        val card_3: String,
        val card_4: String,
        val card_5: String,
        val community_cards: List<String>,
        val created_at: String,
        val current_bet_amount: Double,
        val current_bettor_position: Int,
        val current_card_round: String,
        val current_min_raise: Double,
        val dealer_position: Int,
        val game_uid: String,
        val plan_id: String,
        val player_action_timer: Long,
        val player_grace_timer: Long,
        val small_blind: Double,
        val small_blind_position: Int,
        val start_time: Long,
        val status: String,
        val table_id: String,
        val upLongd_at: String,
        val total_pot_value: Double,
        val side_pots: List<SidePot>
    )

    data class TableDetails(
        val _id: String,
        val avg_bet: Int,
        val game_id: String,
        val is_filled: Boolean,
        val is_toss_done: Boolean,
        val jobs_scheduled: Boolean,
        val plan_id: String,
        val spots_filled: Int,
        val start_time: Double,
        val status: String
    )

    data class UserContestDetails(
        val _id: String,
        val bankroll: Double,
        val card_1: String,
        val card_2: String,
        val created_at: String,
        val game_id: String,
        val game_uid: String,
        val grace_time: Double,
        val join_id: String,
        val plan_id: String,
        val position: String,
        val rank: Int,
        val ref_txn_id: String,
        val seat_no: Int,
        val status: String,
        val subtotal_bet: Double,
        val table_id: String,
        val total_bet: Double,
        val upLongd_at: String,
        val user_name: String,
        val user_unique_id: String,
        val won_amt: Double
    )
}

data class GameUser(
    val game_id: String,
    val game_inplay_amount: Double,
    val amount_invested: Double,
    val position: String,
    val seat_no: Int,
    val current_round_invested: Double,
    val status: String,
    val table_id: String,
    val user_name: String,
    val user_unique_id: String
)

data class PlayerTurn(
    val action_choices: List<String>,
    val player_min_amount_to_call: Double,
    val current_min_raise: Double,
    val game_max_bet_amount: Double,
    val game_inplay_amount: Double,
    val player_action_timer: Long,
    val player_grace_timer: Long,
    val player_turn: String,
    val current_bettor_position: Int,
    val side_pots: List<SidePot>,
    val total_pot_value: Double
)


data class SidePot(
    val players: List<String>,
    val pot_value: Double
)

data class DealCommunityCards(
    val card_1: String,
    val card_2: String,
    val card_3: String,
    val card_4: String,
    val card_5: String
)

