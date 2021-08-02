package com.gtgt.pokerjacks.ui.game.models

import android.graphics.Color
import android.view.View
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import java.io.Serializable

data class PreviousHandDetails(val info: Info) : BaseModel() {
    data class Info(
        val gameDetails: GameDetails,
        val gameUsers: List<GameUserX>
    )

    data class GameUserX(
        val amount_invested: Int,
        val best_hand_details: BestHandDetails,
        val card_1: String,
        val card_2: String,
        val game_id: String,
        val position: String,
        val seat_no: Int,
        val status: String,
        val table_id: String,
        val user_name: String,
        val user_unique_id: String,
        val won_amt: Double
    ) : Serializable {
        val getFoldLabelVisibility: Int
            get() = if (status == TableSlotStatus.FOLD.name) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

        val getCommunityCardVisibility: Int
            get() = if (status != TableSlotStatus.FOLD.name) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

        val formattedWinnings: String
            get() = when {
                won_amt > 0.0 -> {
                    StringBuilder().append("+").append("₹").append((won_amt).toString())
                        .toString()
                }
                won_amt == 0.0 -> {
                    StringBuilder().append("-").append("₹").append((amount_invested).toString())
                        .toString()
                }
                else -> ""
            }


        val winningAmtColor: Int
            get() = when {
                won_amt > 0.0 -> {
                    Color.parseColor("#41c46e")
                }
                won_amt == 0.0 -> {
                    Color.parseColor("#ed1515")
                }
                else -> {
                    Color.parseColor("#f1f1f1")
                }
            }
    }

    data class BestHandDetails(
        val cards: List<String>,
        val rankOrder: String,
        val tieBreaker: List<String>
    )

    data class GameDetails(
        val _id: String,
        val card_1: String,
        val card_2: String,
        val card_3: String,
        val card_4: String,
        val card_5: String,
        val community_cards: List<String>,
        val current_card_round: String,
        val game_uid: String,
        val player_action_timer: Long,
        val player_grace_timer: Long,
        val start_time: Long,
        val status: String,
        val table_id: String,
        val cards_reveal: Boolean?
    )
}