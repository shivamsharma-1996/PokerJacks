package com.gtgt.pokerjacks.ui.game.models

import com.gtgt.pokerjacks.base.BaseModel

data class JoinModel(val info: Info) : BaseModel() {
    data class Info(
        val inWatchMode: Boolean,
        val join_id: String,
        val plan_details: PlanDetails,
        val plan_id: String,
        val table_id: String,
        val user_details: UserDetails
    )

    data class PlanDetails(
        val big_blind: Double,
        val join_id: String,
        val max_buyin: Double,
        val max_players: Int,
        val min_buyin: Double,
        val mode: String,
        val plan_id: String,
        val small_blind: Double
    )
}