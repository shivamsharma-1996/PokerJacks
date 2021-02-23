package com.gtgt.pokerjacks.ui.lobby.model

import android.os.Parcelable
import com.gtgt.pokerjacks.base.BaseModel
import kotlinx.android.parcel.Parcelize

data class LobbyTables(val info: List<Info>) : BaseModel() {
    data class Info(
        val activePlayers: Int,
        val avgBet: Double,
        val community_cards: List<String>,
        var empty_seats: List<Int>,
        val plan_details: PlanDetails,
        val status: String,
        val table_id: String
    )

    @Parcelize
    data class PlanDetails(
        val _id: String,
        val action_time: Int,
        val big_blind: Double,
        val disconnection_time: Int,
        val extra_time: Int,
        val game_type: String,
        val is_active: Boolean,
        val max_buyin: Double,
        val max_players: Int,
        val min_buyin: Double,
        val mode: String,
        val plan_name: String,
        val rake_structure: String,
        val small_blind: Double,
        val sub_game: String,
        val table_status: String
    ) : Parcelable
}